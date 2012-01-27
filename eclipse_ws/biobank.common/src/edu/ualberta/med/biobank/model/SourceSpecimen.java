package edu.ualberta.med.biobank.model;

public class SourceSpecimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Boolean needOriginalVolume;
    private SpecimenType specimenType;
    private Study study;

    public Boolean getNeedOriginalVolume() {
        return needOriginalVolume;
    }

    public void setNeedOriginalVolume(Boolean needOriginalVolume) {
        this.needOriginalVolume = needOriginalVolume;
    }

    public SpecimenType getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
