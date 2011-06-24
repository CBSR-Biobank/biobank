package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.Collection;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.common.wrappers.actions.PropertyCountOnSavedAction;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;

/**
 * Checks that the {@link Collection} of {@link Property}-s is unique for the
 * model object in the {@link ModelWrapper}, excluding the instance itself,
 * which <em>must be saved (persistent)</em>.
 * <em>This check should only be run on saved model objects (that have been saved to the database).</em>
 * 
 * @author jferland
 * 
 * @param <E>
 */
public class UniqueCheck<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_STRING = "There already exists a {0} with property value(s) ({1}) for ({2}), respectively. These field(s) must be unique.";

    private final Collection<Property<?, ? super E>> properties;
    private final PropertyCountOnSavedAction<E> countAction;

    /**
     * 
     * @param wrapper {@link ModelWrapper} which holds the model object
     * @param properties to ensure uniqueness on
     */
    public UniqueCheck(ModelWrapper<E> wrapper,
        Collection<Property<?, ? super E>> properties) {
        super(wrapper);
        this.properties = properties;
        this.countAction = new PropertyCountOnSavedAction<E>(wrapper, properties);
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        Long count = countAction.doAction(session);

        if (count > 1) {
            throwException();
        }

        return null;
    }

    private void throwException() throws DuplicatePropertySetException {
        String modelClass = Format.modelClass(getModelClass());
        String values = Format.propertyValues(getModel(), properties);
        String names = Format.propertyNames(properties);

        String msg = MessageFormat.format(EXCEPTION_STRING, modelClass, values,
            names);

        throw new DuplicatePropertySetException(msg);
    }
}