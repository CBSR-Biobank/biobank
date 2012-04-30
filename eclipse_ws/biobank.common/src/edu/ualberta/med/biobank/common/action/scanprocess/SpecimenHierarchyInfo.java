package edu.ualberta.med.biobank.common.action.scanprocess;

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class SpecimenHierarchyInfo {
    private SpecimenWrapper parentSpecimen;
    private SpecimenTypeWrapper aliquotedSpecimenType;

    public SpecimenHierarchyInfo(SpecimenWrapper parentSpecimen,
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
