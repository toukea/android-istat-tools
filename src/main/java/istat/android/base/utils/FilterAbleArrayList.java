package istat.android.base.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import istat.android.base.interfaces.Filter;
import istat.android.base.interfaces.FilterAble;

/**
 * Created by istat on 22/01/18.
 */

public class FilterAbleArrayList<T> extends ArrayList<T> implements FilterAble<T> {
    List<T> fullContent = new ArrayList();
    Filter<T> filter;

    public FilterAbleArrayList() {
        super();
    }

    public FilterAbleArrayList(Collection<T> collection) {
        this(collection, null);
    }

    public FilterAbleArrayList(Collection<T> collection, Filter<T> filter) {
        super(collection);
        this.fullContent.addAll(collection);
        this.filter = filter;
        if (filter != null) {
            apply(filter);
        }
    }

    public Filter<T> getFilter() {
        return filter;
    }

    @Override
    public void apply(Filter<T> filter) {
        this.filter = filter;
        super.clear();
        if (filter == null) {
            super.addAll(fullContent);
            return;
        }
        for (T entity : fullContent) {
            if (filter.apply(entity)) {
                super.add(entity);
            }
        }
    }

    @Override
    public boolean add(T t) {
        this.fullContent.add(t);
        return super.add(t);
    }

    @Override
    public void add(int index, T element) {
        this.fullContent.add(index, element);
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        this.fullContent.addAll(c);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        this.fullContent.addAll(index, c);
        return super.addAll(index, c);
    }

    @Override
    public T remove(int index) {
        this.fullContent.remove(index);
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        this.fullContent.remove(o);
        return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.fullContent.removeAll(c);
        return super.removeAll(c);
    }

    @Override
    public void clear() {
        this.fullContent.clear();
        super.clear();
    }
}
