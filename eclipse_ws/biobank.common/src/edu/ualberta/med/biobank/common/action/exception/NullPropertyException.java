package edu.ualberta.med.biobank.common.action.exception;

import java.text.MessageFormat;

import edu.ualberta.med.biobank.common.wrappers.Property;

public class NullPropertyException extends ActionException {
    private static final long serialVersionUID = 1L;
    private Property<?, ?> property;

    private static final String EXCEPTION_STRING = "{0} cannot be empty"; //$NON-NLS-1$

    public NullPropertyException(Property<?, ?> property) {
        super(""); //$NON-NLS-1$
        this.property = property;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format(EXCEPTION_STRING, property.getName());
    }

}
