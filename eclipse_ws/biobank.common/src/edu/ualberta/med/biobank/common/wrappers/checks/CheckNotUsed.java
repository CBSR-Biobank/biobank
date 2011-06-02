package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.BiobankWrapperAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ModelIsUsedException;

class CheckNotUsed<E> extends BiobankWrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_MESSAGE = "{0} {1} is still in use by {2}.";
    private static final String COUNT_HQL = "SELECT count(m) FROM {0} m WHERE m.{1} = ?";

    private final Property<? super E, ?> property;
    private final Class<?> propertyClass;
    private final String modelString;
    private final String exceptionMessage;

    /**
     * Check that the model object in the {@code ModelWrapper} is not used by
     * the given {@code Property}.
     * 
     * @param wrapper to get the model object from
     * @param property the property of another object that references the model
     *            object
     * @param exceptionMessage the message in the {@code ModelIsUsedException}
     *            thrown if this model object is used.
     */
    <T> CheckNotUsed(ModelWrapper<E> wrapper,
        Property<? super E, ? super T> property, Class<T> propertyClass,
        String errorMessage) {
        super(wrapper);
        this.propertyClass = propertyClass;
        this.property = property;
        this.modelString = wrapper.toString();
        this.exceptionMessage = errorMessage;
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        String hql = MessageFormat.format(COUNT_HQL, propertyClass.getName(),
            property.getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, getModel());

        List<?> results = query.list();
        Long count = Check.getCountFromResult(results);

        if (count == null || count > 0) {
            String message = getExceptionMessage();
            throw new ModelIsUsedException(message);
        }

        return null;
    }

    private String getExceptionMessage() {
        String exceptionMessage = this.exceptionMessage;

        if (exceptionMessage == null) {
            String modelClass = Format.modelClass(getModelClass());
            String propertyClass = Format.modelClass(this.propertyClass);

            exceptionMessage = MessageFormat.format(EXCEPTION_MESSAGE,
                modelClass, modelString, propertyClass);
        }

        return exceptionMessage;
    }
}
