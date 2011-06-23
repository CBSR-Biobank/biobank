package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankWrapperAction;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ModelIsUsedException;

/**
 * Check that the model object in a {@link ModelWrapper} is not used by a
 * specific {@link Property}.
 * 
 * @author jferland
 * 
 * @param <E>
 * @throws ModelIsUsedException if the wrapped object is used by the specific
 *             {@link Property}.
 */
public class NotUsedCheck<E> extends BiobankWrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_MESSAGE = "{0} {1} is still in use by {2}.";
    private static final String COUNT_HQL = "SELECT count(m) FROM {0} m WHERE m.{1} = ?";

    private final Property<? super E, ?> property;
    private final Class<?> propertyClass;
    private final String modelString;
    private final String exceptionMessage;

    /**
     * 
     * @param wrapper to get the model object from
     * @param property the {@link Property} of another object that references
     *            the {@link ModelWrapper}'s model object
     * @param exceptionMessage the message in the {@link ModelIsUsedException}
     *            thrown if this model object is used.
     */
    public <T> NotUsedCheck(ModelWrapper<E> wrapper,
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

        Long count = HibernateUtil.getCountFromQuery(query);

        if (count > 0) {
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
