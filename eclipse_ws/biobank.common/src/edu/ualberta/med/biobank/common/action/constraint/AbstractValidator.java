package edu.ualberta.med.biobank.common.action.constraint;


public abstract class AbstractValidator<T> implements IConstraintValidator<T> {
    private final String message;

    public AbstractValidator(String message) {
        this.message = message;
    }
}
