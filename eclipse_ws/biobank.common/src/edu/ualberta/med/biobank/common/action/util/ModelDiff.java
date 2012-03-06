package edu.ualberta.med.biobank.common.action.util;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.Property;

public class ModelDiff<E> {
    @SuppressWarnings("unused")
    private final E oldModel, newModel;

    public ModelDiff(Session session, E newModel) {
        this.newModel = newModel;
        this.oldModel = null;
    }

    // public E loadModel()

    public <T> void persistAdded(Property<T, ? super E> property) {
    }

    public <T> void persistRemoved(Property<T, ? super E> property) {
    }

    public <T> void deleteRemoved(Property<T, ? super E> property) {
    }
}
