package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.actions.BiobankSessionAction;
import edu.ualberta.med.biobank.common.wrappers.actions.IfAction;
import edu.ualberta.med.biobank.common.wrappers.actions.IfAction.Is;
import edu.ualberta.med.biobank.common.wrappers.checks.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.LegalOptionCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.NotNullPreCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.NotUsedCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.UniquePreCheck;
import edu.ualberta.med.biobank.common.wrappers.util.LazyMessage;

public class WrapperChecker<E> {
    private final ModelWrapper<E> wrapper;

    WrapperChecker(ModelWrapper<E> wrapper) {
        this.wrapper = wrapper;
    }

    public UniquePreCheck<E> unique(Property<?, ? super E> property) {
        Collection<Property<?, ? super E>> properties = new ArrayList<Property<?, ? super E>>();
        properties.add(property);

        return new UniquePreCheck<E>(wrapper, properties);
    }

    public UniquePreCheck<E> unique(
        Collection<Property<?, ? super E>> properties) {

        // make our own copy that is not exposed
        properties = new ArrayList<Property<?, ? super E>>(properties);

        return new UniquePreCheck<E>(wrapper, properties);
    }

    public NotNullPreCheck<E> notNull(Property<?, ? super E> property) {
        // TODO: check on the client and on the server?
        return new NotNullPreCheck<E>(wrapper, property);
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

    public <T> LegalOptionCheck<E> legalOption(
        Property<? extends Collection<? extends T>, ? super E> legalOptions,
        Property<? extends T, ? super E> selectedOption,
        LazyMessage exceptionMessage) {
        return new LegalOptionCheck<E>(wrapper, legalOptions, selectedOption,
            exceptionMessage);
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

        tasks.add(new CheckStringLengthsPreQueryTask<E>(wrapper));

        return tasks;
    }
}
