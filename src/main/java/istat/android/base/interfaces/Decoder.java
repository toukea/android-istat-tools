package istat.android.base.interfaces;

/**
 * Created by istat on 22/11/17.
 */

public interface Decoder<Output, Input> {

    Output decode(Input input) throws Exception;

}
