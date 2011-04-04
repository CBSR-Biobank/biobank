package edu.ualberta.med.biobank.common.scanprocess;

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class SpecimenHierarchy {
    private SpecimenWrapper parentSpecimen;
    private SpecimenTypeWrapper aliquotedSpecimenType;

    public SpecimenHierarchy(SpecimenWrapper parentSpecimen,
        SpecimenTypeWrapper aliquotedSpecimenType) {
        this.parentSpecimen = parentSpecimen;
        this.aliquotedSpecimenType = aliquotedSpecimenType;
    }

    public SpecimenWrapper getParentSpecimen() {
        return parentSpecimen;
    }

    public SpecimenTypeWrapper getAliquotedSpecimenType() {
        return aliquotedSpecimenType;
    }
}
