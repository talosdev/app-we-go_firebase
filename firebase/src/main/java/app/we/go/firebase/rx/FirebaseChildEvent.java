package app.we.go.firebase.rx;

import app.we.go.firebase.rx.read.ChildEventObservable;

/**
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class FirebaseChildEvent<D> {
//    public String key;
    public D data;
    public ChildEventObservable.EventType eventType;
    public String prevName;

    public FirebaseChildEvent(
//            String key,
            D data, ChildEventObservable.EventType eventType, String prevName) {
//        this.key = key;
        this.data = data;
        this.eventType = eventType;
        this.prevName = prevName;
    }
}
