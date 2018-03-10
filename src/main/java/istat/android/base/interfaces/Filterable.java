package istat.android.base.interfaces;

/**
 * Created by istat on 22/01/18.
 */

public interface Filterable<T> {
    void apply(Filter<T> filter);
}
