package istat.android.base.tools;

import java.util.Comparator;

public interface Sortable<T> {
    void sort(Comparator<T> comparable);
}
