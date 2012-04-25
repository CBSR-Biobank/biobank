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

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

/**
 * The specimens, derived from source specimens, that are collected for a study.
 * 
 * A study can be configured to have as many aliquoted specimens as are
 * required. The aliquoted specimen states the specimen types collected by a
 * study, the number of tubes and the required volume in each tube.
 */
@Entity
@Table(name = "ALIQUOTED_SPECIMEN")
public class AliquotedSpecimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Aliquoted Specimen",
        "Aliquoted Specimens");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString QUANTITY = bundle.trc(
            "model",
            "Quantity").format();
        public static final LString VOLUME = bundle.trc(
            "model",
            "Volume (ml)").format();
    }

    private SpecimenType specimenType;
    private Integer quantity;
    private BigDecimal volume;
    private Study study;
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;

    /**
     * @brief The number of aliquoted tubes to be collected of this specimen
     *        type.
     */
    @Column(name = "QUANTITY")
    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @brief The volume to be collected in each tube.
     */
    @Digits(integer = 10, fraction = 10, message = "{edu.ualberta.med.biobank.model.AliquotedSpecimen.volume.Digits}")
    @Column(name = "VOLUME", precision = 10, scale = 10)
    public BigDecimal getVolume() {
        return this.volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    /**
     * @brief The specimen type that has to be collected for the study.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false)
    public SpecimenType getSpecimenType() {
        return this.specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    /**
     * The study that this aliquoted specimen belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    /**
     * If activity status is ACTIVE then this type of specimen has to be
     * collected. If the activity status is closed then this specimen type is no
     * longer being collected for this study.
     */
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
