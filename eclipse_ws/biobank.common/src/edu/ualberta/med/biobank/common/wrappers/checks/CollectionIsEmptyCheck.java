package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;

/**
 * Check that the {@link Collection} {@link Property} of a {@link ModelWrapper}
 * has a size of zero.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public class CollectionIsEmptyCheck<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_MESSAGE = Messages
        .getString("CollectionIsEmptyCheck.one.or.more.msg"); //$NON-NLS-1$
    private static final String COUNT_HQL = "SELECT m.{0}.size FROM {1} m WHERE m = ?"; //$NON-NLS-1$

    private final Property<?, ? super E> property;
    private final String modelString;
    private final String exceptionMessage;

    /**
     * @param wrapper to get the model object from
     * @param property the collection to ensure has size zero
     * @param exceptionMessage (optional) will override a default message
     *            generated if the collection is not empty. Set to null if you
     *            want to use the default message.
     */
    public CollectionIsEmptyCheck(ModelWrapper<E> wrapper,
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
            String propertyName = Format.propertyName(property);

            exceptionMessage = MessageFormat.format(EXCEPTION_MESSAGE,
                modelClass, modelString, propertyName);
        }

        return exceptionMessage;
    }
}