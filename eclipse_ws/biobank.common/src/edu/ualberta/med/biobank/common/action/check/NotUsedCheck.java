package edu.ualberta.med.biobank.common.action.check;

import java.text.MessageFormat;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.IBiobankModel;
import edu.ualberta.med.biobank.model.User;
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
public class NotUsedCheck<E extends IBiobankModel> extends ActionCheck<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_MESSAGE =
        "{0} {1} is still in use by {2}."; //$NON-NLS-1$

    private final UsageCountAction countAction;
    private final String modelString;
    private final String exceptionMessage;

    /**
     * 
     * @param exceptionMessage the message in the {@link ModelIsUsedException}
     *            thrown if this model object is used.
     */
    public <T> NotUsedCheck(E object, Property<? super E, ? super T> property,
        Class<T> propertyClass, String modelString, String errorMessage) {
        super(null, null);
        this.countAction = new UsageCountAction(object, property);
        this.modelString = modelString;
        this.exceptionMessage = errorMessage;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        Long count = countAction.run(user, session).getCount();

        if (count > 0) {
            String message = getExceptionMessage();
            throw new ModelIsUsedException(message);
        }

        return new EmptyResult();
    }

    private String getExceptionMessage() {
        String exceptionMessage = this.exceptionMessage;

        if (exceptionMessage == null) {
            String modelClass = Format.modelClass(getModelClass());
            String propertyClass = Format.modelClass(countAction
                .getProperty().getClass());

            exceptionMessage = MessageFormat.format(EXCEPTION_MESSAGE,
                modelClass, modelString, propertyClass);
        }

        return exceptionMessage;
    }

}
