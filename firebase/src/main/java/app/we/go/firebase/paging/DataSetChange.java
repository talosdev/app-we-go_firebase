package app.we.go.firebase.paging;



/**
 * Represents a change in a dataset. Meant to be used primarily in RecyclerViews.
 *
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public class DataSetChange<S> {
    private ChangeType type;
    private int position;
    private S value;

    public DataSetChange(ChangeType type, int position, S value) {
        this.type = type;
        this.position = position;
        this.value = value;
    }

    public DataSetChange(ChangeType type, int position) {
        this.type = type;
        this.position = position;
    }

    public ChangeType getType() {
        return type;
    }

    public S getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }

    public enum ChangeType {
        ADDED, MOVED, CHANGED, REMOVED
    }
}
