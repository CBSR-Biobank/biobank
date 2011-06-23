package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.NullPropertyException;

/**
 * Check that a {@link Property} of the {@link ModelWrapper}'s wrapped object is
 * not null.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public class NotNullCheck<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_STRING = "The {0} of {1} {2} must be defined (cannot be null).";

    private final Property<?, ? super E> property;
    private final String modelString;

    /**
     * 
     * @param wrapper
     * @param property
     */
    public NotNullCheck(ModelWrapper<E> wrapper, Property<?, ? super E> property) {
        super(wrapper);
        this.property = property;
        this.modelString = wrapper.toString();
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        E model = getModel();
        Object value = property.get(model);

        if (value == null) {
            String propertyName = Format.propertyName(property);
            String modelClass = Format.modelClass(getModelClass());

            String msg = MessageFormat.format(EXCEPTION_STRING, propertyName,
                modelClass, modelString);

            throw new NullPropertyException(msg);
        }

        return null;
    }
}
