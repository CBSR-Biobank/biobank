package edu.ualberta.med.biobank.action.scanprocess;


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
