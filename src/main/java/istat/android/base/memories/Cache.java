package istat.android.base.memories;

import java.util.Collection;
import java.util.Set;

/**
 * Created by istat on 06/03/18.
 */
public interface Cache<T> {
    T put(String key, T value);

    T remove(String entryName);

    boolean containsKey(String entryName);

    T get(String entryName);

    int size();

    int clear();

    boolean isEmpty();

    Set<String> keySet();

    Collection<T> values();
}
