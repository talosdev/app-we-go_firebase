package app.we.go.firebase.paging;

import android.support.v4.BuildConfig;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;


/**
 * @param <T> The type of the entries of the adapter
 * @param <S> Type argument that defines the class of the <code>criticalValue</code>. (ie
 *            the class of the attribute by which the entries are ordered).
 *            <p>
 *            Created by Aristides Papadopoulos (github:talosdev).
 */
public abstract class AbstractPagingAndChangesHelper<T, S> extends AbstractPagingHelper<T, S>
        implements PagingAndChangesHelper<T> {

    protected final PublishSubject<DataSetChange<T>> changesObservable;
    protected CompositeSubscription subscriptions;
    private static final boolean LOG_ENABLED = BuildConfig.DEBUG;

    public AbstractPagingAndChangesHelper(int limit) {
        super(limit);
        this.changesObservable = PublishSubject.create();
        this.subscriptions = new CompositeSubscription();
    }


    @Override
    public Observable<DataSetChange<T>> getChangesObservable() {
        return changesObservable;
    }


    /**
     * Loads the next page of data and also configures the changesObservable
     *
     * @return
     */
    @Override
    public Observable<List<T>> loadNextPage() {
        if (LOG_ENABLED) {
            Log.v(Tags.PCH, this.getClass().getSimpleName() + " loadNextPage called");
        }

        if (finished) {
            if (LOG_ENABLED) {
                Log.d(Tags.PCH, this.getClass().getSimpleName() + " has finished, returning empty list");
            }
            return Observable.just(Collections.<T>emptyList());
        }

        final QueryParamsAppender queryParamsAppender = getPagingQueryParamsAppender(criticalValue);
        if (LOG_ENABLED) {
            Log.v(Tags.PCH,
                    this.getClass().getSimpleName() + " calling fetchData for critical value " +
                            (criticalValue == null ? "null" : criticalValue.toString()));
        }
        return fetchData(queryParamsAppender)
                .map(bookkeeping())
                .doOnNext(new Action1<List<T>>() {
                    @Override
                    public void call(List<T> data) {
                        registerChangeListeners(queryParamsAppender, data);
                    }
                });
    }


    public abstract QueryParamsAppender getChangesQueryParamsAppender(T firstObject);

    @Override
    public void cleanup() {
        super.cleanup();
        subscriptions.clear();
    }
}
