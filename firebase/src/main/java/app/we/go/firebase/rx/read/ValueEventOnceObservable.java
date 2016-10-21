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
 * {@link com.google.firebase.database.DatabaseReference#addListenerForSingleValueEvent(ValueEventListener)}
 * method.
 * <p>
 * Calls {@link Subscriber#onNext(Object)}} and {@link Subscriber#onCompleted()} (only emits one
 * value).
 * <p>
 * Should not be used in offline mode, because the offline, cached value will always be emitted.
 * <p>
 * The listener is removed as soon as the subscriber unsubscribes (although
 * that should not be strictly necessary).
 * <p>
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class ValueEventOnceObservable {


    private final Query ref;
    private Observable<DataSnapshot> obs;


    public ValueEventOnceObservable(Query ref) {
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
                                subscriber.onCompleted();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(new FirebaseException(databaseError));
                            }
                        }
                    };

                    ref.addListenerForSingleValueEvent(listener);

                    subscriber.add(FirebaseUtils.unsubscribeValueListener(ref, listener));
                }
            });
        }
        return obs
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
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
