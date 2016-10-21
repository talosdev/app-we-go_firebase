package app.we.go.firebase.paging;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Interface that allows Firebase {@link Query} parameters (eg ordering and limit parameters)
 * to be appended to a {@link DatabaseReference}
 *
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public interface QueryParamsAppender {

    /**
     * Appends the query params to the {@link DatabaseReference}
     * @param ref The {@link DatabaseReference} that points to the node that we will be querying
     * @return A {@link Query}
     * TODO maybe make the parameter of type {@link Query} to allow for cascading builders
     */
    Query applyTo(DatabaseReference ref);

}
