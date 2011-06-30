package edu.ualberta.med.biobank.common.wrappers;


/**
 * Meant to hold a combination of a {@link Property} and a specific (model)
 * {@link Object} that the property acts on.
 * 
 * @author jferland
 * 
 */
public class InstanceProperty<P, M> {
    private final Property<P, ? super M> property;
    private final M model;

    public InstanceProperty(M model, Property<P, ? super M> property) {
        this.model = model;
        this.property = property;
    }

    public <W extends ModelWrapper<? extends M>> InstanceProperty(W wrapper,
        Property<P, ? super M> property) {
        this.model = wrapper.getWrappedObject();
        this.property = property;
    }

    public Property<P, ? super M> getProperty() {
        return property;
    }

    public P get() {
        return property.get(model);
    }

    public void set(P value) {
        property.set(model, value);
    }
}
