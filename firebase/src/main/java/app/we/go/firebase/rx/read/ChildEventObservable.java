package app.we.go.firebase.rx.read;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import app.we.go.firebase.FirebaseException;
import app.we.go.firebase.rx.FirebaseChildEvent;
import app.we.go.firebase.rx.FirebaseRawChildEvent;
import app.we.go.firebase.rx.FirebaseUtils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A collection of Observables that emit the {@link DataSnapshot}s returned by a
 * {@link com.google.firebase.database.ChildEventListener}.
 * Will never call {@link Subscriber#onCompleted()}. There are methods that returned all events
 * and others that return the events filtered (eg only a specific type of events).
 * <p/>
 * The listener is removed as soon as the subscriber unsubscribes.
 * <p/>
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class ChildEventObservable {


    private final Query ref;
    private Observable<FirebaseRawChildEvent> obs;

    public ChildEventObservable(Query ref) {
        this.ref = ref;
    }


    public Observable<FirebaseRawChildEvent> get() {

        if (obs == null) {
            obs = Observable.create(new Observable.OnSubscribe<FirebaseRawChildEvent>() {
                                        @Override
                                        public void call(final Subscriber<? super FirebaseRawChildEvent> subscriber) {
                                            final ChildEventListener listener = new ChildEventListener() {

                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                    if (!subscriber.isUnsubscribed()) {
                                                        subscriber.onNext(
                                                                new FirebaseRawChildEvent(dataSnapshot, EventType.CHILD_ADDED, s));
                                                    }
                                                }

                                                @Override
                                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                    if (!subscriber.isUnsubscribed()) {
                                                        subscriber.onNext(
                                                                new FirebaseRawChildEvent(dataSnapshot, EventType.CHILD_CHANGED, s));
                                                    }
                                                }

                                                @Override
                                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                    if (!subscriber.isUnsubscribed()) {
                                                        subscriber.onNext(
                                                                new FirebaseRawChildEvent(dataSnapshot, EventType.CHILD_REMOVED, null));
                                                    }
                                                }

                                                @Override
                                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                                    if (!subscriber.isUnsubscribed()) {
                                                        subscriber.onNext(
                                                                new FirebaseRawChildEvent(dataSnapshot, EventType.CHILD_MOVED, s));
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    if (!subscriber.isUnsubscribed()) {
                                                        subscriber.onError(new FirebaseException(databaseError));
                                                    }
                                                }
                                            };
                                            ref.addChildEventListener(listener);

                                            subscriber.add(FirebaseUtils.unsubscribeChildListener(ref, listener));
                                        }
                                    }
            );
        }
        return obs
                .subscribeOn(Schedulers.io());
    }


    private static Func1<FirebaseChildEvent, Boolean> makeEventFilter(final EventType eventType) {
        return new Func1<FirebaseChildEvent, Boolean>() {

            @Override
            public Boolean call(FirebaseChildEvent firebaseChildEvent) {
                return firebaseChildEvent.eventType == eventType;
            }
        };
    }

    public Observable<DataSnapshot> observeChildAdded() {
        return get().filter(makeEventFilter(EventType.CHILD_ADDED))
                .map(firebaseChildEventToDataSnapshot());
    }

    public <T> Observable<T> observeChildAdded(Class<T> clazz) {
        return observeChildAdded()
                .map(FirebaseUtils.dataSnapshotToObject(clazz));
    }


    public Observable<DataSnapshot> observeChildChanged() {
        return get().filter(makeEventFilter(EventType.CHILD_CHANGED))
                .map(firebaseChildEventToDataSnapshot());
    }

    public <T> Observable<T> observeChildChanged(Class<T> clazz) {
        return observeChildChanged()
                .map(FirebaseUtils.dataSnapshotToObject(clazz));
    }

    public Observable<DataSnapshot> observeChildMoved() {
        return get().filter(makeEventFilter(EventType.CHILD_MOVED))
                .map(firebaseChildEventToDataSnapshot());
    }

    public <T> Observable<T> observeChildMoved(Class<T> clazz) {
        return observeChildMoved()
                .map(FirebaseUtils.dataSnapshotToObject(clazz));
    }

    public Observable<DataSnapshot> observeChildRemoved() {
        return get().filter(makeEventFilter(EventType.CHILD_REMOVED))
                .map(firebaseChildEventToDataSnapshot());
    }


    public <T> Observable<T> observeChildRemoved(Class<T> clazz) {
        return observeChildRemoved()
                .map(FirebaseUtils.dataSnapshotToObject(clazz));
    }


    public Observable<FirebaseRawChildEvent> observeChildAddedOrChanged() {
        return get()
                .filter(new Func1<FirebaseRawChildEvent, Boolean>() {
                    @Override
                    public Boolean call(FirebaseRawChildEvent firebaseChildEvent) {
                        return firebaseChildEvent.eventType == EventType.CHILD_ADDED
                                || firebaseChildEvent.eventType == EventType.CHILD_CHANGED;
                    }
                });
    }

    public <T> Observable<FirebaseChildEvent<T>> observeChildAddedOrChanged(final Class<T> clazz) {
        return get()
                .filter(new Func1<FirebaseRawChildEvent, Boolean>() {
                    @Override
                    public Boolean call(FirebaseRawChildEvent firebaseChildEvent) {
                        return firebaseChildEvent.eventType == EventType.CHILD_ADDED
                                || firebaseChildEvent.eventType == EventType.CHILD_CHANGED;
                    }
                })
                // TODO try to simplify this
                .flatMap(new Func1<FirebaseRawChildEvent, Observable<FirebaseChildEvent<T>>>() {
                    @Override
                    public Observable<FirebaseChildEvent<T>> call(final FirebaseRawChildEvent firebaseRawChildEvent) {
                        return Observable.just(firebaseRawChildEvent)
                                .map(new Func1<FirebaseRawChildEvent, DataSnapshot>() {
                                    @Override
                                    public DataSnapshot call(FirebaseRawChildEvent firebaseRawChildEvent) {
                                        return firebaseRawChildEvent.data;
                                    }
                                })
                                .map(FirebaseUtils.dataSnapshotToObject(clazz))
                                .map(new Func1<T, FirebaseChildEvent<T>>() {
                                    @Override
                                    public FirebaseChildEvent<T> call(T t) {
                                        return new FirebaseChildEvent<>(
//                                firebaseRawChildEvent.key,
                                                t,
                                                firebaseRawChildEvent.eventType,
                                                firebaseRawChildEvent.prevName);
                                    }
                                });
                    }


                });
    }

    private Func1<FirebaseRawChildEvent, DataSnapshot> firebaseChildEventToDataSnapshot() {
        return new Func1<FirebaseRawChildEvent, DataSnapshot>() {
            @Override
            public DataSnapshot call(FirebaseRawChildEvent firebaseChildEvent) {
                return firebaseChildEvent.data;
            }
        };
    }


    public enum EventType {
        CHILD_ADDED, CHILD_CHANGED, CHILD_REMOVED, CHILD_MOVED
    }


}
