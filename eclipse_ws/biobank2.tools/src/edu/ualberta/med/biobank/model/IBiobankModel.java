package edu.ualberta.med.biobank.model;

import java.io.Serializable;

public interface IBiobankModel extends Serializable {

    public Serializable getId();

    public void setId(Serializable id);
}
