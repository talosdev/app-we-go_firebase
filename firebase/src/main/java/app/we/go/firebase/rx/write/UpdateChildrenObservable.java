package app.we.go.firebase.rx.write;

import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import app.we.go.firebase.rx.FirebaseUtils;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * An Observable that can be used for executing a firebase <code>updateChildren</code> operation.
 * The Observable calls <code>onNext()</code> as soon as the <code>childUpdates</code>
 * operation is issued, and <code>onCompleted</code> as soon as the
 * {@link DatabaseReference.CompletionListener} signals
 * that the operation has completed (in the remote database).
 */
public class UpdateChildrenObservable {

    private DatabaseReference ref;

    public UpdateChildrenObservable(DatabaseReference ref) {
        this.ref = ref;
    }

    public Observable<Void> updateChildren(final Map<String, Object> childUpdates) {

        Observable<Void> obs = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                ref.updateChildren(childUpdates,
                        FirebaseUtils.getDefaultCompletionListener(subscriber));
                subscriber.onNext(null);
            }
        });

        return obs
                .subscribeOn(Schedulers.io());
    }



}
