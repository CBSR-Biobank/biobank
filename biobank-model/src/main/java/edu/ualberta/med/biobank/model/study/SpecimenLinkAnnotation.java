package edu.ualberta.med.biobank.model.study;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;


@Audited
@Entity
@Table(name = "SPECIMEN_LINK_ANNOTATION")
public class SpecimenLinkAnnotation
    extends Annotation<SpecimenLinkAnnotationType> {
    private static final long serialVersionUID = 1L;

    private SpecimenLink link;

    @NotNull(message = "{SpecimenLinkAnnotation.link.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_LINK_ID")
    public SpecimenLink getLink() {
        return link;
    }

    public void setLink(SpecimenLink link) {
        this.link = link;
    }
}
