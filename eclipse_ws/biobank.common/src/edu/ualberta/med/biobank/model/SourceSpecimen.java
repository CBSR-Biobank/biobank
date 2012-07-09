package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

/**
 * caTissue Term - Specimen: A single unit of tissue or body fluid collected
 * from a participant as part of a specimen collection event. A new specimen can
 * be created as a derivative of an existing specimen or by dividing it into
 * small pieces.
 * 
 * NCI Term - Biospecimen: Any material sample taken from a biological entity
 * for testing, diagnostic, propagation, treatment or research purposes,
 * including a sample obtained from a living organism or taken from the
 * biological object after halting of all its life functions. Biospecimen can
 * contain one or more components including but not limited to cellular
 * molecules, cells, tissues, organs, body fluids, embryos, and body excretory
 * products.
 * 
 */
@Audited
@Entity
@Table(name = "SOURCE_SPECIMEN")
public class SourceSpecimen extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Source Specimen",
        "Source Specimens");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString NEED_ORIGINAL_VOLUME = bundle.trc(
            "model",
            "Need Original Volume").format();
    }

    private boolean needOriginalVolume = false;
    private SpecimenType specimenType;
    private Study study;

    @Column(name = "NEED_ORIGINAL_VOLUME")
    // TODO: rename to isNeedOriginalVolume
    public boolean getNeedOriginalVolume() {
        return this.needOriginalVolume;
    }

    public void setNeedOriginalVolume(boolean needOriginalVolume) {
        this.needOriginalVolume = needOriginalVolume;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.SourceSpecimen.specimenType.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false)
    public SpecimenType getSpecimenType() {
        return this.specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.SourceSpecimen.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
