package istat.android.base.interfaces;

import java.util.List;

/**
 * Created by istat on 08/03/18.
 */

public interface Pageable<T> {
    int getCount();

    List<T> getItems(int page, int itemPerPage);
}
