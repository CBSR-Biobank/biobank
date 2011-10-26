package edu.ualberta.med.biobank.common.action.check;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;

/**
 * Check that the {@link Collection} {@link Property} of a {@link ModelWrapper}
 * has a size of zero.
 * 
 * @author delphine
 */
public class CollectionIsEmptyCheck<T extends Serializable> extends
    ActionCheck<T> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_MESSAGE = "{0} {1} has one or more {2}."; //$NON-NLS-1$
    private static final String COUNT_HQL = "SELECT m.{0}.size FROM {1} m WHERE m.{2} = ?"; //$NON-NLS-1$

    private final Property<? extends Collection<?>, ? super T> collectionProperty;
    private final String exceptionMessage;
    private final String modelString;

    /**
     * @param wrapper to get the model object from
     * @param collectionProperty the collection to ensure has size zero
     * @param exceptionMessage (optional) will override a default message
     *            generated if the collection is not empty. Set to null if you
     *            want to use the default message.
     */
    public CollectionIsEmptyCheck(ValueProperty<T> idProperty,
        Class<T> modelClass,
        Property<? extends Collection<?>, ? super T> collectionProperty,
        String modelString, String exceptionMessage) {
        super(idProperty, modelClass);
        this.collectionProperty = collectionProperty;
        this.modelString = modelString;
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public T run(User user, Session session) throws ActionException {
        String hql = MessageFormat.format(COUNT_HQL,
            collectionProperty.getName(), getModelClass().getName(),
            getIdProperty().getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, getModelId());

        Long count = HibernateUtil.getCountFromQuery(query);

        if (count > 0) {
            String message = getExceptionMessage();
            throw new CollectionNotEmptyException(message);
        }

        return null;
    }

    private String getExceptionMessage() {
        String exceptionMessage = this.exceptionMessage;

        if (exceptionMessage == null) {
            String modelClass = Format.modelClass(getModelClass());
            String propertyName = Format.propertyName(collectionProperty);

            exceptionMessage = MessageFormat.format(EXCEPTION_MESSAGE,
                modelClass, modelString, propertyName);
        }

        return exceptionMessage;
    }

}