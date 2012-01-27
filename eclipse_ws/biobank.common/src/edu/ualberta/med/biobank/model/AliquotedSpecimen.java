package edu.ualberta.med.biobank.model;

import org.hibernate.validator.Min;
import org.hibernate.validator.NotNull;

public class AliquotedSpecimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer quantity;
    private Double volume;
    private SpecimenType specimenType;
    private Study study;
    private ActivityStatus activityStatus;

    @NotNull
    @Min(value = 1)
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @NotNull
    @Min(value = 0)
    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    @NotNull
    public SpecimenType getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    @NotNull
    public Study getStudy() {
        return study;
    }

    // TODO: make bi-di
    public void setStudy(Study study) {
        this.study = study;
    }

    @NotNull
    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }
}
