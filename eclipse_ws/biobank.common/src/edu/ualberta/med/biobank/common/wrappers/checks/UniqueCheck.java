package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.Collection;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.PropertyCountAction;
import edu.ualberta.med.biobank.common.wrappers.actions.UncachedAction;
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
public class UniqueCheck<E> extends UncachedAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String EXCEPTION_STRING = "There already exists a {0} with property value(s) ({1}) for ({2}), respectively. These field(s) must be unique.";

    private final Collection<Property<?, ? super E>> properties;
    private final PropertyCountAction<E> countAction;

    /**
     * 
     * @param wrapper {@link ModelWrapper} which holds the model object
     * @param properties to ensure uniqueness on
     */
    public UniqueCheck(ModelWrapper<E> wrapper,
        Collection<Property<?, ? super E>> properties) {
        super(wrapper);
        this.properties = properties;
        this.countAction = new PropertyCountAction<E>(wrapper, properties);
    }

    @Override
    public void doUncachedAction(Session session) throws BiobankSessionException {
        Long count = countAction.doAction(session);

        if (count > 1) {
            throwException();
        }
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