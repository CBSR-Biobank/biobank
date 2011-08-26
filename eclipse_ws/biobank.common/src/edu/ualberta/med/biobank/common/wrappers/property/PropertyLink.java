package edu.ualberta.med.biobank.common.wrappers.property;

import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.Property.Accessor;

public class PropertyLink<A, P, M> implements Accessor<A, M> {
    private static final long serialVersionUID = 1L;

    private final Property<P, M> from;
    private final Property<A, ? super P> to;

    public PropertyLink(Property<P, M> from, Property<A, ? super P> to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public A get(M model) {
        P association = from.get(model);
        return association == null ? null : to.get(association);
    }

    @Override
    public void set(M model, A value) {
        P association = from.get(model);
        if (association != null) {
            to.set(association, value);
        }
    }

    public Property<P, M> getFrom() {
        return from;
    }

    public Property<A, ? super P> getTo() {
        return to;
    }
}