package istat.android.base.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by istat on 12/05/17.
 */

public abstract class AbsListWrapper<T> {
    final List<T> items = new ArrayList<>();

    public AbsListWrapper() {

    }

    public AbsListWrapper(Collection<T> items) {
        items.addAll(items);
    }

    public List<T> asArrays() {
        return items;
    }

    public int size() {
        return items.size();
    }


    public boolean isEmpty() {
        return items.isEmpty();
    }


    public boolean contains(Object o) {
        return items.contains(o);
    }


    public Iterator<T> iterator() {
        return items.iterator();
    }


    public Object[] toArray() {
        return items.toArray();
    }


    public <T> T[] toArray(T[] a) {
        return items.toArray(a);
    }


    public boolean add(T T) {
        return items.add(T);
    }


    public boolean remove(Object o) {
        return items.remove(o);
    }


    public boolean containsAll(Collection<?> c) {
        return items.containsAll(c);
    }


    public boolean addAll(Collection<? extends T> c) {
        return items.addAll(c);
    }


    public boolean addAll(int index, Collection<? extends T> c) {
        return items.addAll(index, c);
    }


    public boolean removeAll(Collection<?> c) {
        return items.removeAll(c);
    }


    public boolean retainAll(Collection<?> c) {
        return items.retainAll(c);
    }


    public void clear() {
        items.clear();
    }


    public T get(int index) {
        return items.get(index);
    }


    public T set(int index, T element) {
        return items.set(index, element);
    }


    public void add(int index, T element) {
        items.add(index, element);
    }


    public T remove(int index) {
        return items.remove(index);
    }


    public int indexOf(Object o) {
        return items.indexOf(o);
    }


    public int lastIndexOf(Object o) {
        return items.lastIndexOf(o);
    }


    public ListIterator<T> listIterator() {
        return items.listIterator();
    }


    public ListIterator<T> listIterator(int index) {
        return items.listIterator(index);
    }


    public List<T> subList(int fromIndex, int toIndex) {
        return items.subList(fromIndex, toIndex);
    }
}
