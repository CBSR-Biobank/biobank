package edu.ualberta.med.biobank.model;

import java.io.Serializable;

public interface IBiobankModel extends Serializable, HasId<Integer> {
    @Override
    public Integer getId();

    @Override
    public void setId(Integer id);
}
