package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.common.VarCharLengths;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.CheckFieldLimitsException;
import edu.ualberta.med.biobank.common.wrappers.actions.IfProperty;
import edu.ualberta.med.biobank.common.wrappers.actions.IfProperty.Is;
import edu.ualberta.med.biobank.common.wrappers.checks.CheckCollectionIsEmpty;
import edu.ualberta.med.biobank.common.wrappers.checks.CheckNotNull;
import edu.ualberta.med.biobank.common.wrappers.checks.CheckNotUsed;
import edu.ualberta.med.biobank.common.wrappers.checks.CheckUnique;
import edu.ualberta.med.biobank.common.wrappers.checks.LazyMessage;
import edu.ualberta.med.biobank.common.wrappers.checks.PostCheckLegalOption;

public class WrapperChecker<E> {
    private final ModelWrapper<E> wrapper;

    WrapperChecker(ModelWrapper<E> wrapper) {
        this.wrapper = wrapper;
    }

    public CheckUnique<E> unique(Property<?, ? super E> property) {
        Collection<Property<?, ? super E>> properties = new ArrayList<Property<?, ? super E>>();
        properties.add(property);

        return new CheckUnique<E>(wrapper, properties);
    }

    public CheckUnique<E> unique(Collection<Property<?, ? super E>> properties) {

        // make our own copy that is not exposed
        properties = new ArrayList<Property<?, ? super E>>(properties);

        return new CheckUnique<E>(wrapper, properties);
    }

    public CheckNotNull<E> notNull(Property<?, ? super E> property) {
        // TODO: check on the client and on the server?
        return new CheckNotNull<E>(wrapper, property);
    }

    public TaskList uniqueAndNotNull(Property<?, ? super E> property) {
        TaskList tasks = new TaskList();

        tasks.add(notNull(property));
        tasks.add(unique(property));

        return tasks;
    }

    public CheckCollectionIsEmpty<E> empty(
        Property<? extends Collection<?>, ? super E> property) {
        return new CheckCollectionIsEmpty<E>(wrapper, property, null);
    }

    public CheckCollectionIsEmpty<E> empty(
        Property<? extends Collection<?>, ? super E> property,
        String exceptionMessage) {
        return new CheckCollectionIsEmpty<E>(wrapper, property,
            exceptionMessage);
    }

    public <T> PostCheckLegalOption<E> legalOption(
        Property<? extends Collection<? extends T>, ? super E> legalOptions,
        Property<? extends T, ? super E> selectedOption,
        LazyMessage exceptionMessage) {
        return new PostCheckLegalOption<E>(wrapper, legalOptions,
            selectedOption, exceptionMessage);
    }

    public <T> CheckNotUsed<E> notUsedBy(Class<T> propertyClass,
        Property<? super E, ? super T> property) {
        return new CheckNotUsed<E>(wrapper, property, propertyClass, null);
    }

    public <T> CheckNotUsed<E> notUsedBy(Class<T> propertyClass,
        Property<? super E, ? super T> property, String exceptionMessage) {
        return new CheckNotUsed<E>(wrapper, property, propertyClass,
            exceptionMessage);
    }

    public <T> IfProperty<E> ifProperty(Property<?, ? super E> property, Is is,
        BiobankSessionAction action) {
        return new IfProperty<E>(wrapper, property, is, action);
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
