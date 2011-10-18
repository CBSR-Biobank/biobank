package edu.ualberta.med.biobank.mvp.validation;

public interface Validator<T> {
    public void validate(T value, ValidationResultAdder results);
}
