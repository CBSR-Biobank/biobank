package edu.ualberta.med.biobank.action.util;

import org.hibernate.Session;

@SuppressWarnings("unused")
public class ModelDiff<E> {
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
