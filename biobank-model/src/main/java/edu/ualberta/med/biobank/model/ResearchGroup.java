package edu.ualberta.med.biobank.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.group.PreDelete;

@Audited
@Entity
@DiscriminatorValue("ResearchGroup")
@Empty(property = "requests", groups = PreDelete.class)
public class ResearchGroup extends Center {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Research Group",
        "Research Groups");

    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", unique = true)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
