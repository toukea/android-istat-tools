package istat.android.base.utils;

public class Result<Value, Error> {
    public final static int
            STATE_SUCCESS = 255,
            STATE_ERROR = 127,
            STATE_FAILED = 111,
            STATE_ABORTED = 95;
    protected Value value;
    protected Error error;
    protected int state;

    Result(Value value, Error error, int state) {
        this.value = value;
        this.error = error;
        this.state = state;
    }

    public Result(Value value) {
        this(value, null, STATE_SUCCESS);
    }

    public Result(Error error, int state) {
        this(null, error, state);
    }

    public int getState() {
        return state;
    }

    public Value getValue() {
        return value;
    }

    public Error getError() {
        return error;
    }

    public boolean hasError() {
        return error != null || state == STATE_ERROR || state == STATE_FAILED;
    }

    public boolean isCanceled() {
        return value == null && state == STATE_ABORTED;
    }

    public boolean isSuccess() {
        return !hasError() && state == STATE_SUCCESS;
    }
}
