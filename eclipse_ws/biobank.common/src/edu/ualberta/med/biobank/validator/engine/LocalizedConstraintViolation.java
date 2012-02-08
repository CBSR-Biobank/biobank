package edu.ualberta.med.biobank.validator.engine;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * Delegates the {@link ConstraintViolation} functionality to another
 * implementation, but uses a {@link javax.validation.MessageInterpolator} and
 * transient message to re-interpolate the message whenever serialized and
 * deserialized (presumably in a different environment).
 * 
 * @author Jonathan Ferland
 */
public class LocalizedConstraintViolation<T> implements ConstraintViolation<T> {
    private final ConstraintViolation<T> delegate;
    private transient String message;

    public LocalizedConstraintViolation(ConstraintViolation<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMessageTemplate() {
        return delegate.getMessageTemplate();
    }

    @Override
    public T getRootBean() {
        return delegate.getRootBean();
    }

    @Override
    public Class<T> getRootBeanClass() {
        return delegate.getRootBeanClass();
    }

    @Override
    public Object getLeafBean() {
        return delegate.getLeafBean();
    }

    @Override
    public Path getPropertyPath() {
        return delegate.getPropertyPath();
    }

    @Override
    public Object getInvalidValue() {
        return delegate.getInvalidValue();
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return delegate.getConstraintDescriptor();
    }
}
