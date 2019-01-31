package istat.android.base.interfaces;

public interface Callback<Result, Error> {
    int
            STATE_LATENT = -1,
            STATE_STARTED = 7,
            STATE_PROCESSING = 31,
            STATE_SUCCESS = 255,
            STATE_ERROR = 127,
            STATE_FAILED = 111,
            STATE_ABORTED = 95,
            STATE_PENDING = 15,
            STATE_DROPPED = 1,
            STATE_FLAG_FINISHED = 65;

    void onSuccess(Result result);

    void onError(Error result);

    void onFinish(int state);

}
