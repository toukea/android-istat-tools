package istat.android.base.interfaces;

/**
 * Created by istat on 04/04/18.
 */

public interface Puller<Result, From> {
    Result pull(From from);
}
