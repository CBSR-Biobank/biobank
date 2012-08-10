package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * Note: since this application will be used for inventory control of non human
 * participants, this class should be renamed to Participant.
 * 
 * caTissue Term - Participant: An individual from which a biospecimen is
 * collected.
 * 
 * NCI Term - Patient: A person who receives medical attention, care, or
 * treatment, or who is registered with medical professional or institution with
 * the purpose to receive medical care when necessary.
 * 
 */
@Audited
@Entity
@Table(name = "PATIENT",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "STUDY_ID", "PNUMBER" }) })
@Unique(properties = { "study", "pnumber" }, groups = PrePersist.class)
@NotUsed.List({
    @NotUsed(by = Specimen.class, property = "collectionEvent.patient", groups = PreDelete.class),
    @NotUsed(by = CollectionEvent.class, property = "patient", groups = PreDelete.class)
})
public class Patient extends AbstractModel
    implements HasComments {
    private static final long serialVersionUID = 1L;

    private Study study;
    private String pnumber;
    private Set<Comment> comments = new HashSet<Comment>(0);

    @NotNull(message = "{Patient.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @NotEmpty(message = "{Patient.pnumber.NotEmpty}")
    @Column(name = "PNUMBER", nullable = false, length = 100)
    public String getPnumber() {
        return this.pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    @Override
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "PATIENT_COMMENT",
        joinColumns = { @JoinColumn(name = "PATIENT_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
