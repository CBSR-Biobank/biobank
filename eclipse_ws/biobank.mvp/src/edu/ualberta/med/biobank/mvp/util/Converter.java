package edu.ualberta.med.biobank.mvp.util;

public interface Converter<T, U> {
    U convert(T object);
}
