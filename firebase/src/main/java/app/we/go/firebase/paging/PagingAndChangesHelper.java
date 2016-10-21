package app.we.go.firebase.paging;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Sub-interface of {@link PagingHelper} that apart from only loading data, also monitors them
 * for changes.
 *
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public interface PagingAndChangesHelper<T> extends PagingHelper<T> {

    /**
     * Returns an Observable that emits the changes that happen to the data the helper has loaded.
     * @return
     */
    Observable<DataSetChange<T>> getChangesObservable();

    /**
     * Registers the change listeners. Called after each page of data is loaded.
     * @param queryParamsAppender The {@link QueryParamsAppender} that was used for loading the page.
     * @param pageData The data of th page that has just been loaded.
     */
    void registerChangeListeners(QueryParamsAppender queryParamsAppender, List<T> pageData);

    /**
     * Utility function that can be used to unmarshal the DataSnapshot to the domain object
     * of type <code>T</code>
     * @return
     */
    Func1<DataSnapshot, Observable<T>> snapshotToObject();
}
