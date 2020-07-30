package istat.android.base.interfaces;

/**
 * Created by istat on 22/11/17.
 */

public interface DataProvider<Data, Key> {

    Data getData(Key encoded) throws Exception;

}
