package app.we.go.firebase.rx.read;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import app.we.go.firebase.FirebaseException;
import app.we.go.firebase.rx.FirebaseChildEvent;
import app.we.go.firebase.rx.FirebaseUtils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 *
 * @deprecated I am not using this after all.
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class MonitoringChildObservable {
    private Query ref;
    private PublishSubject<FirebaseChildEvent> updates;

    private boolean isInitialLoad;
//    private List<DataSnapshot> dataSnapshotList;


    public MonitoringChildObservable(Query ref) {
        this.ref = ref;

        isInitialLoad = true;

        updates = PublishSubject.create();

    }


    public Observable<DataSnapshot> getInitial() {


        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (isInitialLoad) {
//                    dataSnapshotList.add(dataSnapshot);
                } else {
                    updates.onNext(new FirebaseChildEvent<>(
//                            dataSnapshot.getKey(),
                            dataSnapshot, ChildEventObservable.EventType.CHILD_ADDED, s));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                updates.onNext(new FirebaseChildEvent<>(
//                        dataSnapshot.getKey(),
                        dataSnapshot, ChildEventObservable.EventType.CHILD_REMOVED, null));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
            @Override
            public void call(final Subscriber<? super DataSnapshot> subscriber) {
                final ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        isInitialLoad = false;
                        subscriber.onNext(dataSnapshot);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(new FirebaseException(databaseError));
                    }
                };

                ref.addListenerForSingleValueEvent(listener);
                subscriber.add(FirebaseUtils.unsubscribeValueListener(ref, listener));
            }
        })
                .flatMap(new Func1<DataSnapshot, Observable<DataSnapshot>>() {
                    @Override
                    public Observable<DataSnapshot> call(DataSnapshot dataSnapshot) {
                        return Observable.from(dataSnapshot.getChildren());
                    }
                });

    }

    public <T> Observable<T> get(final Class<T> clazz) {
        return getInitial()
                .map(FirebaseUtils.dataSnapshotToObject(clazz));
    }


    public Observable<FirebaseChildEvent> getUpdates() {
        return updates;
    }
}
