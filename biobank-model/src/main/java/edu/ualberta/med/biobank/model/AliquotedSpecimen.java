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

import org.hibernate.envers.Audited;

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
@Audited
@Entity
@Table(name = "ALIQUOTED_SPECIMEN")
public class AliquotedSpecimen extends AbstractModel {
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

    private Study study;
    private SpecimenType specimenType;
    private Integer quantity;
    private BigDecimal volume;
    private Boolean enabled;

    @NotNull(message = "{AliquotedSpecimen.study.NotNull")
    @Column(name = "STUDY_ID")
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

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
    @Digits(integer = 10, fraction = 10, message = "{AliquotedSpecimen.volume.Digits}")
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
    @NotNull(message = "{AliquotedSpecimen.specimenType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false)
    public SpecimenType getSpecimenType() {
        return this.specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    /**
     * If this {@link AliquotedSpecimen#isEnabled()}, then this processing step
     * is stilling being done, otherwise, it is kept as a record, but is not
     * currently performed.
     */
    @NotNull(message = "{AliquotedSpecimen.enabled.NotNull}")
    @Column(name = "IS_ENABLED", nullable = false)
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
