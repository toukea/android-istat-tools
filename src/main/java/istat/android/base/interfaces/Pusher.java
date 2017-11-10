package istat.android.base.interfaces;

/**
 * Created by Istat Toukea on 23/08/2017.
 */

public interface Pusher<INP, OUT> {
    OUT onPush(INP input);
}
