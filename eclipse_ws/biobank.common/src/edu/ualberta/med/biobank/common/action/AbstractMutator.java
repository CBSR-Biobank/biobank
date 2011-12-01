package edu.ualberta.med.biobank.common.action;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.common.action.check.PropertySetCountAction;
import edu.ualberta.med.biobank.common.action.check.PropertyValue;
import edu.ualberta.med.biobank.common.action.check.UsageCountAction;
import edu.ualberta.med.biobank.common.action.exception.ModelIsUsedException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.IBiobankModel;

public class AbstractMutator<M extends IBiobankModel> {
    protected final ActionContext context;
    protected final Class<M> modelClass;
    protected final M model;

    protected AbstractMutator(ActionContext context, Class<M> modelClass,
        M model) {
        this.context = context;
        this.modelClass = modelClass;
        this.model = model;
    }

    protected <E> void notNull(Property<E, ? extends M> property, E value)
        throws NullPropertyException {
        if (value == null) {
            throw new NullPropertyException(modelClass, property.getName());
        }
    }

    protected void unique(PropertyValue... propertyValues) {
        Collection<PropertyValue> collection = new ArrayList<PropertyValue>();
        for (PropertyValue propertyValue : propertyValues) {
            collection.add(propertyValue);
        }
        unique(collection);
    }

    protected void unique(Collection<PropertyValue> propertyValues) {
        PropertySetCountAction count =
            new PropertySetCountAction(model, modelClass, propertyValues);
        if (count.run(context.getUser(), context.getSession()).notZero()) {

        }
    }

    protected <T> void notUsedBy(Property<? super M, T> property)
        throws ModelIsUsedException {
        UsageCountAction uses = new UsageCountAction(model, property);
        if (uses.run(context.getUser(), context.getSession()).notZero()) {
            throw new ModelIsUsedException(modelClass, model.getId(), property);
        }
    }

    protected <E> PropertyValue propertyValue(Property<E, ? super M> property,
        E value) {
        return new PropertyValue(property, value);
    }
}
