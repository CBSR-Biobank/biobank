package edu.ualberta.med.biobank.model;

import java.io.Serializable;

public interface IBiobankModel extends Serializable, HasId<Long> {
    @Override
    public Long getId();

    @Override
    public void setId(Long id);
}
