package istat.android.base.memories;

import java.util.Set;

/**
 * Created by istat on 06/03/18.
 */
public interface Cache<T> {
    T put(String key, T value);

    T remove(String filePath);

    boolean containsKey(String filePath);

    T get(String name);

    int size();

    void clear();

    boolean isEmpty();

    Set<String> keySet();
}
