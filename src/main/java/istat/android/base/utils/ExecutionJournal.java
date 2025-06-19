package istat.android.base.utils;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

//TODO on peut ajouter a cela une notion de source et de destination.
public class ExecutionJournal<Data> {
    final static int
            STATE_UNKNOWN = 0,
            STATE_SUCCESS = 255,
            STATE_PARTIAL_SUCCESS = 127,
            STATE_FAILED = 111;
    List<Data> data = new ArrayList<>();
    final SparseArray<Throwable> errorMap = new SparseArray<>();

    ExecutionJournal() {

    }

    public List<Data> getData() {
        return data;
    }

    public int getDataCount() {
        return data != null ? data.size() : 0;
    }

    public List<Throwable> getErrors() {
        List<Throwable> output = new ArrayList<>();
        for (Data erroredData : getErrorData()) {
            output.add(getError(erroredData));
        }
        return output;
    }

    public int getState() {
        if (data == null && errorMap.size() == 0) {
            return STATE_UNKNOWN;
        }
        if (data != null) {
            if (data.size() == errorMap.size()) {
                return STATE_FAILED;
            } else if (errorMap.size() == 0) {
                return STATE_SUCCESS;
            } else {
                return STATE_PARTIAL_SUCCESS;
            }
        }
        return STATE_UNKNOWN;
    }

    public boolean hasError(Data item) {
        int itemIndex = data.indexOf(item);
        if (itemIndex < 0) {
            return false;
        }
        return hasError(itemIndex);
    }

    public boolean hasError(int index) {
        return errorMap.get(index) != null;
    }

    public boolean hasErrors() {
        return errorMap.size() > 0;
    }

    public Throwable getError(Data item) {
        int itemIndex = data.indexOf(item);
        if (itemIndex < 0) {
            return null;
        }
        return getError(itemIndex);
    }

    public Throwable getError(int index) {
        return errorMap.get(index);
    }

    public int getErrorCount() {
        return errorMap.size();
    }

    public int getSuccessCount() {
        return getDataCount() - getErrorCount();
    }

    public List<Integer> getErrorIndex() {
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < errorMap.size(); i++) {
            index.add(errorMap.keyAt(i));
        }
        return index;
    }

    public List<Data> getErrorData() {
        List<Data> index = new ArrayList<>();
        int key;
        for (int i = 0; i < errorMap.size(); i++) {
            key = errorMap.keyAt(i);
            index.add(data.get(key));
        }
        return index;
    }

    public Data getData(int index) {
        return data.size() > index ? data.get(index) : null;
    }

    public static class Builder<Data> {
        ExecutionJournal<Data> output = new ExecutionJournal<>();

        public Builder<Data> setSuccess(Data data) {
            output.data.add(data);
            return this;
        }

        public Builder<Data> setFailure(Data data, Throwable cause) {
            output.data.add(data);
            output.errorMap.put(output.data.size() - 1, cause);
            return this;
        }

        public ExecutionJournal<Data> build() {
            return output;
        }
    }
}