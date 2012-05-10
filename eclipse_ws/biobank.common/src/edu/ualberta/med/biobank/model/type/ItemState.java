package edu.ualberta.med.biobank.model.type;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.util.NotAProxy;

public interface ItemState extends Serializable, NotAProxy {
    public Integer getId();

    public String getLabel();
}
