package app.we.go.firebase.rx.write;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import app.we.go.firebase.FirebaseException;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public abstract class TransactionObservable {


    private final DatabaseReference ref;

    private Observable<DataSnapshot> obs;

    public TransactionObservable(DatabaseReference ref) {
        this.ref = ref;
    }



    public Observable<Void> get() {
        Observable<DataSnapshot> obs = Observable.create(new Observable.OnSubscribe<DataSnapshot>() {

            @Override
            public void call(final Subscriber<? super DataSnapshot> subscriber) {
                Transaction.Handler handler = new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        return processData(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        if (b) {
                            subscriber.onNext(dataSnapshot);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new FirebaseException(databaseError));
                        }
                    }
                };

                ref.runTransaction(handler);
            }
        });

        return obs
                .map(new Func1<DataSnapshot, Void>() { // TODO what???
                    @Override
                    public Void call(DataSnapshot dataSnapshot) {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public abstract Transaction.Result processData(MutableData mutableData);
}
