package app.we.go.firebase.paging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 *
 * @param <T> The type of the entries of the adapter
 * @param <S> Type argument that defines the class of the <code>criticalValue</code>. (ie
 *           the class of the attribute by which the entries are ordered).
 *
 * Created by Aristides Papadopoulos (github:talosdev).
 */
public abstract class AbstractPagingHelper<T, S> implements PagingHelper<T>{

    /**
     * This value corresponds to the attribute based on which the items are ordered.
     * It holds the starting value for the next page to load.
     */
    protected S criticalValue;
    protected boolean finished;
    protected int limit;
    protected List<T> data = new ArrayList<>();

    /**
     *
     * @param limit Number of items to load in each page
     */
    public AbstractPagingHelper(int limit) {
        this.limit = limit;
    }


    @Override
    public Observable<List<T>> loadNextPage() {
        if (finished) {
            return Observable.just(Collections.<T>emptyList());
        }

        QueryParamsAppender queryParamsAppender = getPagingQueryParamsAppender(criticalValue);

        return fetchData(queryParamsAppender)
                .map(bookkeeping());
    }

    protected abstract Observable<List<T>> fetchData(QueryParamsAppender queryParamsAppender);

    /**
     * This method is available of all the bookkeeping of the helper. It keeps track and updates
     * the <code>criticalValue</code>. It does so by requesting for each page one item more than
     * the <code>limit</code> specified, and storing value of the attribute of this item that is
     * used for ordering.
     * @return
     */
    protected Func1<List<T>, ? extends List<T>> bookkeeping() {
        return new Func1<List<T>, List<T>>() {
            @Override
            public List<T> call(List<T> list) {
                if (list.size() > 0) {
                    criticalValue = extractCriticalValue(list.get(list.size() - 1));
                    if (list.size() > 1) { // greater than 1, rather than 0, because otherwise we would
                                           //  never end up showing the last element (it would always
                                           // get left out as the extra - critical value one
                        list.remove(list.size() - 1);
                    } else {
                        finished = true;
                    }
                    // If the field that we are sorting by allows ties, we
                    // want to filter data that we've included already.
                    while (list.size() > 0 && data.contains(list.get(0))) {
                        list.remove(0);
                    }
                    data.addAll(list);
                }
                return list;
            }
        };
    }

    @Override
    public List<T> getAllLoadedData() {
        return data;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    /**
     * Method to be overridden, specifying how the <code>criticalValue</code> (ie the attribute
     * according to which the items are ordered,  can be extracted from an item.
     *
     * @param t The item from which we want to extract the critical value
     * @return
     */
    protected abstract S extractCriticalValue(T t);

    /**
     * Note that if <code>S</code> is not an immutable class, <code>criticalValue</code> must
     * be properly cloned or copied before being passed as the <code>value</code> parameter.
     * @param value The <code>criticalValue</code>. We need to pass it as a parameter here rather
     *              then accessing it directly, to make sure that the value at the moment of creation
     *              is used.
     * @return
     */
    protected abstract QueryParamsAppender getPagingQueryParamsAppender(S value);


    @Override
    public void cleanup() {
        criticalValue = null;
        finished = false;
        data = new ArrayList<>();
    }
}
