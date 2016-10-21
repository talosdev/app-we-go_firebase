package app.we.go.firebase.rx.read;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.util.List;

import app.we.go.firebase.rx.FirebaseUtils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * An Observable that emits the children {@link DataSnapshot}s of a node.
 * This is based on a {@link ValueEventObservable}, so it inherits all its properties.
 * It might emit more than once and will not call {@link Subscriber#onCompleted()}.
 * It is directly emitting lists of items instead of individual items so that we can differentiate
 * between initial emition and consequent ones.
 *
 * @see ChildIteratorOnceObservable
 *
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class ChildIteratorObservable {

    private final Query ref;
    private Observable<List<DataSnapshot>> obs;

    public ChildIteratorObservable(Query ref) {
        this.ref = ref;
    }

    public Observable<List<DataSnapshot>> get() {
        if (obs == null) {
            obs = new ValueEventObservable(ref)
                    .get()
                    .flatMap(
                            new Func1<DataSnapshot, Observable<List<DataSnapshot>>>() {
                                @Override
                                public Observable<List<DataSnapshot>> call(DataSnapshot dataSnapshot) {
                                    return Observable.from(dataSnapshot.getChildren()).toList();
                                }
                            }
                    );
        }

        return obs;
    }

    public <T> Observable<List<T>> get(final Class<T> clazz) {
        return get()
                .flatMap(new Func1<List<DataSnapshot>, Observable<T>>() {
                    @Override
                    public Observable<T> call(List<DataSnapshot> dataSnapshots) {
                        return Observable.from(dataSnapshots)
                                .map(FirebaseUtils.dataSnapshotToObject(clazz));
                    }
                })
                .toList();
    }
}
