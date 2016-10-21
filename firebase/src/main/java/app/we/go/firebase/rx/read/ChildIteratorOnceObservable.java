package app.we.go.firebase.rx.read;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import app.we.go.firebase.rx.FirebaseUtils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * An Observable that emits the children {@link DataSnapshot}s of a node.
 * This is based on a {@link ValueEventOnceObservable}, so it inherits all its properties.
 * It will eventually call {@link Subscriber#onCompleted()}, so the {@link Observable#toList}
 * operator can used upon it.
 *
 * @see ChildIteratorObservable
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class ChildIteratorOnceObservable {

    private final Query ref;
    private Observable<DataSnapshot> obs;

    public ChildIteratorOnceObservable(Query ref) {
        this.ref = ref;
    }

    public Observable<DataSnapshot> get() {
        if (obs == null) {
            obs = new ValueEventOnceObservable(ref)
                    .get()
                    .flatMap(
                            new Func1<DataSnapshot, Observable<DataSnapshot>>() {
                                @Override
                                public Observable<DataSnapshot> call(DataSnapshot dataSnapshot) {
                                    return Observable.from(dataSnapshot.getChildren());
                                }
                            }
                    );
        }

        return obs;
    }

    public <T> Observable<T> get(final Class<T> clazz) {
        return get()
                .map(FirebaseUtils.dataSnapshotToObject(clazz));
    }


}
