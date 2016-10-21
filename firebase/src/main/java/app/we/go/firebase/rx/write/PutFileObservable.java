package app.we.go.firebase.rx.write;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * An Observable that can be used for executing a firebase storage <code>putFile</code>
 * operation.
 * It calls <code>onNext()</code>, followed by <code>onCompleted</code> only when the
 * {@link OnSuccessListener} signals that the put has been a success.
 */
public class PutFileObservable {

    private StorageReference ref;

    public PutFileObservable(StorageReference ref) {
        this.ref = ref;
    }

    public Observable<UploadTask.TaskSnapshot> putFile(final Uri fileUri) {

        Observable<UploadTask.TaskSnapshot> obs =
                Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
            @Override
            public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                UploadTask uploadTask = ref.putFile(fileUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        subscriber.onError(exception);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        subscriber.onNext(taskSnapshot);
                        subscriber.onCompleted();
                    }
                });
            }
        });

        return obs
                .subscribeOn(Schedulers.io());
    }


}
