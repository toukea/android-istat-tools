package istat.android.base.utils;

import java.util.Arrays;
import java.util.List;

public class PayLoad {
    private Object[] variableArray;

    public PayLoad(Object[] vars) {
        this.variableArray = vars != null ? vars : new Object[0];
    }

    public int getCount() {
        return variableArray.length;
    }

    public Object[] asArray() {
        return variableArray;
    }

    public List<?> asList() {
        return Arrays.asList(variableArray);
    }

    public <T> T getVariable(int index) {
        if (variableArray.length <= index) {
            return null;
        }
        try {
            return (T) variableArray[index];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isVarInstanceOf(int index, Class<?> cLass) {
        Object var = getVariable(index);
        return var != null && cLass != null && cLass.isAssignableFrom(var.getClass());
    }

    public <T> T getVariable(int index, Class<T> cLass) throws ArrayIndexOutOfBoundsException {
        if (variableArray.length <= index) {
            throw new ArrayIndexOutOfBoundsException("executionVariables length=" + variableArray.length + ", requested index=" + index
            );
        }
        Object var = variableArray[index];
        if (var == null) {
            return null;
        }
        if (cLass.isAssignableFrom(var.getClass())) {
            return (T) var;
        } else {
            throw new IllegalArgumentException("Item at index=" + index + " has type class=" + var.getClass() + ", requested class=" + cLass);
        }
    }

    public String getStringVariable(int index) throws ArrayIndexOutOfBoundsException {
        if (variableArray.length <= index) {
            throw new ArrayIndexOutOfBoundsException("executionVariables length=" + variableArray.length + ", requested index=" + index
            );
        }
        Object var = variableArray[index];
        if (var == null) {
            return null;
        }
        return String.valueOf(var);
    }

    public int getIntVariable(int index) throws ArrayIndexOutOfBoundsException {
        if (variableArray.length <= index) {
            throw new ArrayIndexOutOfBoundsException("executionVariables length=" + variableArray.length + ", requested index=" + index
            );
        }
        Object var = variableArray[index];
        if (var == null) {
            return 0;
        }
        return Integer.valueOf(String.valueOf(var));
    }

    public long getLongVariable(int index) throws ArrayIndexOutOfBoundsException {
        if (variableArray.length <= index) {
            throw new ArrayIndexOutOfBoundsException("executionVariables length=" + variableArray.length + ", requested index=" + index
            );
        }
        Object var = variableArray[index];
        if (var == null) {
            return 0;
        }
        return Long.valueOf(String.valueOf(var));
    }

    public float getFloatVariable(int index) throws ArrayIndexOutOfBoundsException {
        if (variableArray.length <= index) {
            throw new ArrayIndexOutOfBoundsException("executionVariables length=" + variableArray.length + ", requested index=" + index
            );
        }
        Object var = variableArray[index];
        if (var == null) {
            return 0;
        }
        return Float.valueOf(String.valueOf(var));
    }

    public double getDoubleVariable(int index) throws ArrayIndexOutOfBoundsException {
        if (variableArray.length <= index) {
            throw new ArrayIndexOutOfBoundsException("executionVariables length=" + variableArray.length + ", requested index=" + index
            );
        }
        Object var = variableArray[index];
        if (var == null) {
            return 0;
        }
        return Double.valueOf(String.valueOf(var));
    }

    public int length() {
        return variableArray.length;
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public boolean getBooleanVariable(int index) {
        if (variableArray.length <= index) {
            throw new ArrayIndexOutOfBoundsException("executionVariables length=" + variableArray.length + ", requested index=" + index
            );
        }
        Object var = variableArray[index];
        if (var == null) {
            return false;
        }
        return Boolean.valueOf(String.valueOf(String.valueOf(var)));
    }
}