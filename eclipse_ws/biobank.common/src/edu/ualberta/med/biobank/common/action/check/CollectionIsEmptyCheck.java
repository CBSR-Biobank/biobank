package edu.ualberta.med.biobank.common.action.check;

import java.text.MessageFormat;
import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;

/**
 * Check that the {@link Collection} {@link Property} of a model object has a
 * size of zero.
 * 
 * @author delphine
 */
public class CollectionIsEmptyCheck<T> {
    private static final String EXCEPTION_MESSAGE =
        "{0} {1} has one or more {2}."; //$NON-NLS-1$

    private static final String COUNT_HQL =
        "SELECT m.{0}.size FROM {1} m WHERE m = ?"; //$NON-NLS-1$

    private final Class<T> modelClass;
    private final T modelObject;
    private final Property<? extends Collection<?>, ? super T> collectionProperty;
    private final String exceptionMessage;
    private final String modelString;

    /**
     * @param collectionProperty the collection to ensure has size zero
     * @param exceptionMessage (optional) will override a default message
     *            generated if the collection is not empty. Set to null if you
     *            want to use the default message.
     */
    public CollectionIsEmptyCheck(Class<T> modelClass, T modelObject,
        Property<? extends Collection<?>, ? super T> collectionProperty,
        String modelString, String exceptionMessage) {
        this.modelClass = modelClass;
        this.modelObject = modelObject;
        this.collectionProperty = collectionProperty;
        this.modelString = modelString;
        this.exceptionMessage = exceptionMessage;
    }

    public EmptyResult run(User user, Session session) throws ActionException {
        String hql = MessageFormat.format(COUNT_HQL,
            collectionProperty.getName(), modelClass.getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, modelObject);

        Long count = HibernateUtil.getCountFromQuery(query);

        if (count > 0) {
            String message = getExceptionMessage();
            throw new CollectionNotEmptyException(message);
        }

        return new EmptyResult();
    }

    private String getExceptionMessage() {
        String exceptionMessage = this.exceptionMessage;

        if (exceptionMessage == null) {
            String modelClassName = Format.modelClass(modelClass);
            String propertyName = Format.propertyName(collectionProperty);

            exceptionMessage = MessageFormat.format(EXCEPTION_MESSAGE,
                modelClassName, modelString, propertyName);
        }

        return exceptionMessage;
    }

}