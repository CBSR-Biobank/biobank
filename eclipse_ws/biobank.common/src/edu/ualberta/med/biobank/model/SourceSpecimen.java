package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

@Entity
@Table(name = "SOURCE_SPECIMEN")
public class SourceSpecimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_TYPE_ID", nullable = false)
    public SpecimenType getSpecimenType() {
        return this.specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
