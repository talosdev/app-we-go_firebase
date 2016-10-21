package app.we.go.firebase.rx;

import com.google.firebase.database.DataSnapshot;

import app.we.go.firebase.rx.read.ChildEventObservable;

/**
 * Subclass of {@link FirebaseChildEvent} that includes the raw child data
 * (as a {@link DataSnapshot}
 *
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class FirebaseRawChildEvent extends FirebaseChildEvent<DataSnapshot> {

    public FirebaseRawChildEvent(DataSnapshot data, ChildEventObservable.EventType eventType, String prevName) {
        super(data, eventType, prevName);
    }
}
