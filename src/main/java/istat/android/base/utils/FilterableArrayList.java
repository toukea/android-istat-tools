package istat.android.base.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import istat.android.base.interfaces.Filter;
import istat.android.base.interfaces.Filterable;

/**
 * Created by istat on 22/01/18.
 */

public class FilterableArrayList<T> extends ArrayList<T> implements Filterable<T> {
    public final List<T> fullContent;
    Filter<T> filter;

    public FilterableArrayList() {
        this(null);
    }

    public FilterableArrayList(Collection<T> collection) {
        this(collection, null);
    }

    public FilterableArrayList(List<T> collection, Filter<T> filter) {
        super(collection);
        if (collection != null) {
            this.fullContent = collection;
        } else {
            this.fullContent = new ArrayList<>();
            if (!collection.isEmpty()) {
                this.fullContent.addAll(collection);
            }
        }

        this.filter = filter;
        if (filter != null) {
            apply(filter);
        }
    }

    public FilterableArrayList(Collection<T> collection, Filter<T> filter) {
        super(collection);
        this.fullContent = new ArrayList<>();
        if (collection != null && !collection.isEmpty()) {
            this.fullContent.addAll(collection);
        }
        this.filter = filter;
        if (filter != null) {
            apply(filter);
        }
    }

    public List<T> getFullContent() {
        return new ArrayList<>(fullContent);
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
