package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.property.GetterInterceptor;
import edu.ualberta.med.biobank.common.wrappers.property.LazyLoaderInterceptor;
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
public class NotNullPreCheck<E> extends WrapperCheck<E> implements PreCheck {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_STRING = "The {0} of {1} {2} must be defined (cannot be null).";

    private final Property<?, ? super E> property;
    private final String modelString;

    /**
     * 
     * @param wrapper
     * @param property
     */
    public NotNullPreCheck(ModelWrapper<E> wrapper,
        Property<?, ? super E> property) {
        super(wrapper);
        this.property = property;
        this.modelString = wrapper.toString();
    }

    @Override
    public void doCheck(Session session) throws BiobankSessionException {
        E model = getModel();

        GetterInterceptor lazyLoad = new LazyLoaderInterceptor(session, 1);

        Object value = property.get(model, lazyLoad);

        if (value == null) {
            String propertyName = Format.propertyName(property);
            String modelClass = Format.modelClass(getModelClass());

            String msg = MessageFormat.format(EXCEPTION_STRING, propertyName,
                modelClass, modelString);

            throw new NullPropertyException(msg);
        }
    }
}
