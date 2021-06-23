package com.mzh.emock.util.entity;

import java.lang.reflect.Field;
import java.util.List;

public class EMFieldInfo {
    public EMFieldInfo(int index, List<String> trace) {
        this.index = index;
        this.isArrayIndex = true;
        this.fieldTrace=trace;
    }

    public EMFieldInfo(Field field, List<String> trace) {
        this.nativeField = field;
        this.isArrayIndex = false;
        this.fieldTrace=trace;
    }

    private final boolean isArrayIndex;
    private Field nativeField;
    private int index;
    List<String> fieldTrace;

    public boolean isArrayIndex() {
        return isArrayIndex;
    }

    public Field getNativeField() {
        return nativeField;
    }

    public int getIndex() {
        return index;
    }

    public List<String> getFieldTrace() {
        return fieldTrace;
    }
}
