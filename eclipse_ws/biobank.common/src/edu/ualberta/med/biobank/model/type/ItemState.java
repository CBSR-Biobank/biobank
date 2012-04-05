package edu.ualberta.med.biobank.model.type;

public interface ItemState {

    public boolean isEquals(Integer state);

    public Integer getId();

    public String getLabel();
}
