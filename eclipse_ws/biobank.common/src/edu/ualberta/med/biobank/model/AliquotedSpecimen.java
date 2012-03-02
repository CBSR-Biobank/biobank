package edu.ualberta.med.biobank.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

/**
 * @brief The specimen types, derived from source specimens, that are collected
 *        for a particular study.
 * 
 */
@Entity
@Table(name = "ALIQUOTED_SPECIMEN")
public class AliquotedSpecimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    /**
     * The number of aliquoted tubes to be collected.
     */
    private int quantity;
    private BigDecimal volume;
    private SpecimenType specimenType;
    private Study study;
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;

    @Column(name = "QUANTITY")
    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Digits(integer = 10, fraction = 10, message = "{edu.ualberta.med.biobank.model.AliquotedSpecimen.volume.Digits}")
    @Column(name = "VOLUME", precision = 10, scale = 10)
    public BigDecimal getVolume() {
        return this.volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false)
    public SpecimenType getSpecimenType() {
        return this.specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.AliquotedSpecimen.activityStatus.NotNull}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }
}
