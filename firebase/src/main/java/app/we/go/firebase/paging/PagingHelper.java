package app.we.go.firebase.paging;

import java.util.List;

import rx.Observable;

/**
 * A helper class that can be user to load and serve paged data. It is supposed to be used
 * by the presenter, which then feeds the items to the adapter.
 *
 * @param <T> The type of items we are loading
 *
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public interface PagingHelper<T> {

    /**
     * Loads the next page of data.
      * @return
     */
    Observable<List<T>> loadNextPage();

    /**
     * Returns all the data that the helper has loaded so far.
     * @return
     */
    List<T> getAllLoadedData();

    /**
     * If <code>true</code>, there is no more data to load. This value should be consulted inside
     * {@link #loadNextPage}
     * @return
     */
    boolean isFinished();

    /**
     * Method that allows cleanup operations to be performed (eg unregister listeners etc).
     */
    void cleanup();


}
