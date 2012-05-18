package edu.ualberta.med.biobank.common.action.scanprocess;

import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class SpecimenHierarchyInfo {
    private SpecimenWrapper parentSpecimen;
    private AliquotedSpecimenWrapper aliquotedSpecimenType;

    public SpecimenHierarchyInfo(SpecimenWrapper parentSpecimen,
        AliquotedSpecimenWrapper aliquotedSpecimenType) {
        this.parentSpecimen = parentSpecimen;
        this.aliquotedSpecimenType = aliquotedSpecimenType;
    }

    public SpecimenWrapper getParentSpecimen() {
        return parentSpecimen;
    }

    public AliquotedSpecimenWrapper getAliquotedSpecimenType() {
        return aliquotedSpecimenType;
    }
}
