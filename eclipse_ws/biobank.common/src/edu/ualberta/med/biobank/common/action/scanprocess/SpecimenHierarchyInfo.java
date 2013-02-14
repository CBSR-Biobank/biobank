package edu.ualberta.med.biobank.common.action.scanprocess;

import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenHierarchyInfo {
    private final Specimen parentSpecimen;
    private final AliquotedSpecimen aliquotedSpecimenType;

    public SpecimenHierarchyInfo(Specimen parentSpecimen, AliquotedSpecimen aliquotedSpecimenType) {
        this.parentSpecimen = parentSpecimen;
        this.aliquotedSpecimenType = aliquotedSpecimenType;
    }

    public Specimen getParentSpecimen() {
        return parentSpecimen;
    }

    public AliquotedSpecimen getAliquotedSpecimenType() {
        return aliquotedSpecimenType;
    }
}
