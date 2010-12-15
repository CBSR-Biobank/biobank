package edu.ualberta.med.biobank.common.util;

/**
 * Holds a reference to a particular type of object, to use like a pointer.
 * 
 * @author jferland
 * 
 * @param <T>
 */
public class Holder<T> {
    private T value;

    public Holder(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
