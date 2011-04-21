package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.util.ItemState;

public interface ItemWrapper {

    public String getStateDescription();

    @Override
    public boolean equals(Object object);

    public SpecimenWrapper getSpecimen();

    public ItemState getSpecimenState();

}
