package istat.android.base.interfaces;

/**
 * Created by istat on 04/04/18.
 */

public interface Pusher<Data, Destination, Result> {
    Result push(Data data, Destination destination);
}
