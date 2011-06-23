package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.common.VarCharLengths;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.CheckFieldLimitsException;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankSessionAction;
import edu.ualberta.med.biobank.common.wrappers.actions.IfAction;
import edu.ualberta.med.biobank.common.wrappers.actions.IfAction.Is;
import edu.ualberta.med.biobank.common.wrappers.checks.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.NotNullCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.NotUsedCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.UniqueCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.LegalOptionOnSavedCheck;
import edu.ualberta.med.biobank.common.wrappers.util.LazyMessage;

public class WrapperChecker<E> {
    private final ModelWrapper<E> wrapper;

    WrapperChecker(ModelWrapper<E> wrapper) {
        this.wrapper = wrapper;
    }

    public UniqueCheck<E> unique(Property<?, ? super E> property) {
        Collection<Property<?, ? super E>> properties = new ArrayList<Property<?, ? super E>>();
        properties.add(property);

        return new UniqueCheck<E>(wrapper, properties);
    }

    public UniqueCheck<E> unique(Collection<Property<?, ? super E>> properties) {

        // make our own copy that is not exposed
        properties = new ArrayList<Property<?, ? super E>>(properties);

        return new UniqueCheck<E>(wrapper, properties);
    }

    public NotNullCheck<E> notNull(Property<?, ? super E> property) {
        // TODO: check on the client and on the server?
        return new NotNullCheck<E>(wrapper, property);
    }

    public TaskList uniqueAndNotNull(Property<?, ? super E> property) {
        TaskList tasks = new TaskList();

        tasks.add(notNull(property));
        tasks.add(unique(property));

        return tasks;
    }

    public CollectionIsEmptyCheck<E> empty(
        Property<? extends Collection<?>, ? super E> property) {
        return new CollectionIsEmptyCheck<E>(wrapper, property, null);
    }

    public CollectionIsEmptyCheck<E> empty(
        Property<? extends Collection<?>, ? super E> property,
        String exceptionMessage) {
        return new CollectionIsEmptyCheck<E>(wrapper, property,
            exceptionMessage);
    }

    public <T> LegalOptionOnSavedCheck<E> legalOption(
        Property<? extends Collection<? extends T>, ? super E> legalOptions,
        Property<? extends T, ? super E> selectedOption,
        LazyMessage exceptionMessage) {
        return new LegalOptionOnSavedCheck<E>(wrapper, legalOptions,
            selectedOption, exceptionMessage);
    }

    public <T> NotUsedCheck<E> notUsedBy(Class<T> propertyClass,
        Property<? super E, ? super T> property) {
        return new NotUsedCheck<E>(wrapper, property, propertyClass, null);
    }

    public <T> NotUsedCheck<E> notUsedBy(Class<T> propertyClass,
        Property<? super E, ? super T> property, String exceptionMessage) {
        return new NotUsedCheck<E>(wrapper, property, propertyClass,
            exceptionMessage);
    }

    public <T> IfAction<E> ifProperty(Property<?, ? super E> property, Is is,
        BiobankSessionAction action) {
        return new IfAction<E>(wrapper, property, is, action);
    }

    public TaskList stringLengths() {
        TaskList tasks = new TaskList();

        tasks.add(new CheckStringLengths<E>(wrapper));

        return tasks;
    }

    /**
     * Needs to access protected members of {@code ModelWrapper} so is in the
     * same package.
     * 
     * @author jferland
     * 
     * @param <E>
     */
    private static class CheckStringLengths<E> implements PreQueryTask {
        private final ModelWrapper<E> modelWrapper;

        public CheckStringLengths(ModelWrapper<E> modelWrapper) {
            this.modelWrapper = modelWrapper;
        }

        @Override
        public void beforeExecute() throws BiobankException {
            E model = modelWrapper.getWrappedObject();
            Class<E> modelClass = modelWrapper.getWrappedClass();

            for (Property<?, ? super E> property : modelWrapper.getProperties()) {
                String field = property.getName();

                Integer max = VarCharLengths.getMaxSize(modelClass, field);
                if (max == null)
                    continue;

                // TODO: does this work???
                if (property.getElementClass().equals(String.class)) {
                    String value = (String) property.get(model);
                    if ((value != null) && (value.length() > max)) {
                        throw new CheckFieldLimitsException(field, max, value);
                    }
                }
            }
        }
    }
}
