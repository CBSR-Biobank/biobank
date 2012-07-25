package edu.ualberta.med.biobank.model;

import java.io.Serializable;

public interface HasId<T extends Serializable> {
    public T getId();

    public void setId(T id);
}
