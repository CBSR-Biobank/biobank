package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.BiobankWrapperAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;

public class CheckCollectionIsEmpty<E> extends BiobankWrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_MESSAGE = "{0} {1} has one or more {2}.";
    private static final String COUNT_HQL = "SELECT m.{0}.size FROM {1} m WHERE m = ?";

    private final Property<?, ? super E> property;
    private final String modelString;
    private final String exceptionMessage;

    /**
     * Check that the {@code Collection} {@code Property} of the given
     * {@code ModelWrapper} has a size of zero.
     * 
     * @param wrapper to get the model object from
     * @param property the collection to ensure has size zero
     * @param exceptionMessage (optional) will override a default message
     *            generated if the collection is not empty. Set to null if you
     *            want to use the default message.
     */
    public CheckCollectionIsEmpty(ModelWrapper<E> wrapper,
        Property<? extends Collection<?>, ? super E> property,
        String exceptionMessage) {
        super(wrapper);
        this.property = property;
        this.modelString = wrapper.toString();
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        String hql = MessageFormat.format(COUNT_HQL, property.getName(),
            getModelClass().getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, getModel());

        List<?> results = query.list();
        Long count = CheckUtil.getCountFromResult(results);

        if (count == null || count > 0) {
            String message = getExceptionMessage();
            throw new CollectionNotEmptyException(message);
        }

        return null;
    }

    private String getExceptionMessage() {
        String exceptionMessage = this.exceptionMessage;

        if (exceptionMessage == null) {
            String modelClass = Format.modelClass(getModelClass());
            String propertyName = Format.propertyName(property);

            exceptionMessage = MessageFormat.format(EXCEPTION_MESSAGE,
                modelClass, modelString, propertyName);
        }

        return exceptionMessage;
    }
}