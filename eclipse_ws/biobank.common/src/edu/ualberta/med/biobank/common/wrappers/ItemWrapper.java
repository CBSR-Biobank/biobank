package edu.ualberta.med.biobank.common.wrappers;

public interface ItemWrapper {

    public String getStateDescription();

    @Override
    public boolean equals(Object object);

    public SpecimenWrapper getSpecimen();

}
