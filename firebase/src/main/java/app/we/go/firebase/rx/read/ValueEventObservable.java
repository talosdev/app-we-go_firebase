package app.we.go.firebase.rx.read;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import app.we.go.firebase.FirebaseException;
import app.we.go.firebase.rx.FirebaseUtils;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * An Observable that emits the data returned by a {@link ValueEventListener}, set with the
 * {@link com.google.firebase.database.DatabaseReference#addValueEventListener(ValueEventListener)}
 * method.
 * <p/>
 * Will never call {@link Subscriber#onCompleted()} (because in offline mode, the listener might
 * receive one or two events and there's no way to know when the emission is done).
 * <p/>
 * The listener is removed as soon as the subscriber unsubscribes.
 * <p/>
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class ValueEventObservable {


    private final Query ref;

    private Observable<DataSnapshot> obs;

    public ValueEventObservable(Query ref) {
        this.ref = ref;
    }


    /**
     * Get the singleton instance of the Observable with the data, as {@link DataSnapshot}s.
     *
     * @return
     */
    public Observable<DataSnapshot> get() {
        if (obs == null) {
            obs = Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
                @Override
                public void call(final Subscriber<? super DataSnapshot> subscriber) {
                    final ValueEventListener listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!subscriber.isUnsubscribed()) {

                                subscriber.onNext(dataSnapshot);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(new FirebaseException(databaseError));
                            }
                        }
                    };

                    ref.addValueEventListener(listener);

                    subscriber.add(FirebaseUtils.unsubscribeValueListener(ref, listener));
                }
            });
        }

        return obs
                .subscribeOn(Schedulers.io());
    }


    /**
     * Get the singleton instance of the Observable with the data, casted to a specific class.
     *
     * @return
     */
    public <T> Observable<T> get(final Class<T> clazz) {
        return get()
                .map(FirebaseUtils.dataSnapshotToObject(clazz));
    }

}
