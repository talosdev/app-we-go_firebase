package app.we.go.firebase.rx;

import android.support.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import app.we.go.firebase.HasId;

import java.util.Map;

import app.we.go.firebase.FirebaseException;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class FirebaseUtils {


    public static <T> Func1<DataSnapshot, T> dataSnapshotToObject(final Class<T> clazz) {
        return new Func1<DataSnapshot, T>() {
            @Override
            public T call(DataSnapshot dataSnapshot) {
                T t = dataSnapshot.getValue(clazz);
                if (t instanceof HasId) {
                    ((HasId) t).setId(dataSnapshot.getKey());
                }
                return t;
            }
        };
    }

    @NonNull
    public static DatabaseReference.CompletionListener getDefaultCompletionListener(final Subscriber<? super Void> subscriber) {
        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new FirebaseException(databaseError));
                }
            }
        };
    }

    public static void addMapToChildUpdates(Map<String, Object> map, String basePath, Map<String, Object> childUpdates) {
        for(Map.Entry entry: map.entrySet()) {
            if (basePath.equals("")) {
                childUpdates.put((String) entry.getKey(), entry.getValue());
            } else {
                childUpdates.put(basePath + "/" + entry.getKey(), entry.getValue());
            }
        }
    }


    public static Subscription unsubscribeValueListener(final Query ref, final ValueEventListener listener) {
        return Subscriptions.create(
                new Action0() {
                    @Override
                    public void call() {
                        ref.removeEventListener( listener);
                    }
                }
        );
    }

    public static Subscription unsubscribeChildListener(final Query ref, final ChildEventListener listener) {
        return Subscriptions.create(
                new Action0() {
                    @Override
                    public void call() {
                        ref.removeEventListener(listener);
                    }
                }
        );
    }

}
