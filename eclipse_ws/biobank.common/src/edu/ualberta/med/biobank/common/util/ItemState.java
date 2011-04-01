package edu.ualberta.med.biobank.common.util;

public interface ItemState {

    public boolean isEquals(Integer state);

    public Integer getId();

    public String getLabel();
}
