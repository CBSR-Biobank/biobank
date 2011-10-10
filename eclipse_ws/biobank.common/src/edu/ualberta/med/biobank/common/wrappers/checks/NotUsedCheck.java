package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.CountUsesAction;
import edu.ualberta.med.biobank.common.wrappers.actions.UncachedAction;
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
public class NotUsedCheck<E> extends UncachedAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_MESSAGE = Messages.getString("NotUsedCheck.msg"); //$NON-NLS-1$

    private final CountUsesAction<E> countAction;
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
        this.countAction = new CountUsesAction<E>(wrapper, property,
            propertyClass);
        this.modelString = wrapper.toString();
        this.exceptionMessage = errorMessage;
    }

    @Override
    public void doUncachedAction(Session session) throws BiobankSessionException {
        Long count = countAction.doAction(session);

        if (count > 0) {
            String message = getExceptionMessage();
            throw new ModelIsUsedException(message);
        }
    }

    private String getExceptionMessage() {
        String exceptionMessage = this.exceptionMessage;

        if (exceptionMessage == null) {
            String modelClass = Format.modelClass(getModelClass());
            String propertyClass = Format.modelClass(countAction
                .getPropertyClass());

            exceptionMessage = MessageFormat.format(EXCEPTION_MESSAGE,
                modelClass, modelString, propertyClass);
        }

        return exceptionMessage;
    }
}
