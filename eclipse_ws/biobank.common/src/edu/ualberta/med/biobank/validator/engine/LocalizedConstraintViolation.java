package edu.ualberta.med.biobank.validator.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator.Context;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.engine.MessageInterpolatorContext;

import edu.ualberta.med.biobank.validator.messageinterpolator.OgnlMessageInterpolator;

/**
 * Delegates the {@link ConstraintViolation} functionality to another
 * implementation, but uses a {@link javax.validation.MessageInterpolator} and
 * transient message to re-interpolate the message whenever serialized and
 * deserialized (presumably in a different environment).
 * 
 * @author Jonathan Ferland
 */
public class LocalizedConstraintViolation<T>
    implements ConstraintViolation<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private static final OgnlMessageInterpolator MESSAGE_INTERPOLATOR =
        new OgnlMessageInterpolator();

    private final ConstraintViolation<T> delegate;
    private transient String message;

    public LocalizedConstraintViolation(ConstraintViolation<T> delegate) {
        this.delegate = delegate;

        interpolateMessage();
    }

    @Override
    public String getMessage() {
        return message;
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

    private void readObject(ObjectInputStream s) throws IOException,
        ClassNotFoundException {
        s.defaultReadObject();
        interpolateMessage();
    }

    private void interpolateMessage() {
        String template = getMessageTemplate();

        ConstraintDescriptor<?> descriptor = getConstraintDescriptor();
        T rootBean = getRootBean();
        Context context = new MessageInterpolatorContext(descriptor, rootBean);

        message = MESSAGE_INTERPOLATOR.interpolate(template, context);
    }
}
