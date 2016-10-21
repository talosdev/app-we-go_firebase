package app.we.go.firebase.rx.write;

import com.google.firebase.database.DatabaseReference;

import app.we.go.firebase.rx.FirebaseUtils;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * An Observable that can be used for executing a firebase <code>setValue</code> operation.
 * It calls <code>onNext()</code> as soon as the save operation is
 * issued, and <code>onCompleted</code> as soon as the
 * {@link com.google.firebase.database.DatabaseReference.CompletionListener} signals
 * that the operation has completed (in the remote database).
 */
public class SetValueObservable<T> {


    private DatabaseReference ref;

    public SetValueObservable(DatabaseReference ref) {
        this.ref = ref;
    }

    public Observable<Void> setValue(final T value) {

        Observable<Void> obs = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                ref.setValue(value, FirebaseUtils.getDefaultCompletionListener(subscriber));
                subscriber.onNext(null);
            }
        });

        return obs
                .subscribeOn(Schedulers.io());
    }


}
