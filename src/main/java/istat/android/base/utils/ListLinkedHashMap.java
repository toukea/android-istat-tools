package istat.android.base.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ListLinkedHashMap<T, Y> extends LinkedHashMap<T, List<Y>> {

    public void append(T name, Y value) {
        List<Y> list = get(name);
        if (list == null) {
            list = new ArrayList();
            put(name, list);
        }
        list.add(value);
    }

    public void append(T name, List<Y> value) {
        List<Y> list = get(name);
        if (list == null) {
            list = new ArrayList();
            put(name, list);
        }
        if (value != null && !value.isEmpty()) {
            list.addAll(value);
        }
    }

}
