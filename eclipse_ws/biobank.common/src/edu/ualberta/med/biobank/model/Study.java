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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

@Entity
@Table(name = "STUDY")
@Unique.List({
    @Unique(properties = "name", groups = PrePersist.class),
    @Unique(properties = "nameShort", groups = PrePersist.class)
})
@Empty(property = "patientCollection", groups = PreDelete.class)
public class Study extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String nameShort;
    private Set<AliquotedSpecimen> aliquotedSpecimenCollection =
        new HashSet<AliquotedSpecimen>(0);
    private Set<Patient> patientCollection = new HashSet<Patient>(0);
    private Set<Site> siteCollection = new HashSet<Site>(0);
    private Set<Comment> commentCollection = new HashSet<Comment>(0);
    private ActivityStatus activityStatus;
    private Set<Membership> membershipCollection =
        new HashSet<Membership>(0);
    private Set<StudyEventAttr> studyEventAttrCollection =
        new HashSet<StudyEventAttr>(0);
    private Set<Contact> contactCollection = new HashSet<Contact>(0);
    private ResearchGroup researchGroup;
    private Set<SourceSpecimen> sourceSpecimenCollection =
        new HashSet<SourceSpecimen>(0);

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Study.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Study.nameShort.NotEmpty}")
    @Column(name = "NAME_SHORT", unique = true, nullable = false, length = 50)
    public String getNameShort() {
        return this.nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<AliquotedSpecimen> getAliquotedSpecimenCollection() {
        return this.aliquotedSpecimenCollection;
    }

    public void setAliquotedSpecimenCollection(
        Set<AliquotedSpecimen> aliquotedSpecimenCollection) {
        this.aliquotedSpecimenCollection = aliquotedSpecimenCollection;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<Patient> getPatientCollection() {
        return this.patientCollection;
    }

    public void setPatientCollection(Set<Patient> patientCollection) {
        this.patientCollection = patientCollection;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "studyCollection")
    public Set<Site> getSiteCollection() {
        return this.siteCollection;
    }

    public void setSiteCollection(Set<Site> siteCollection) {
        this.siteCollection = siteCollection;
    }

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "STUDY_COMMENT",
        joinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getCommentCollection() {
        return this.commentCollection;
    }

    public void setCommentCollection(Set<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.Study.activityStatus.NotEmpty}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_STATUS_ID", nullable = false)
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", updatable = false)
    public Set<Membership> getMembershipCollection() {
        return this.membershipCollection;
    }

    public void setMembershipCollection(
        Set<Membership> membershipCollection) {
        this.membershipCollection = membershipCollection;
    }

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<StudyEventAttr> getStudyEventAttrCollection() {
        return this.studyEventAttrCollection;
    }

    public void setStudyEventAttrCollection(
        Set<StudyEventAttr> studyEventAttrCollection) {
        this.studyEventAttrCollection = studyEventAttrCollection;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "STUDY_CONTACT",
        joinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CONTACT_ID", nullable = false, updatable = false) })
    public Set<Contact> getContactCollection() {
        return this.contactCollection;
    }

    public void setContactCollection(Set<Contact> contactCollection) {
        this.contactCollection = contactCollection;
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "study")
    public ResearchGroup getResearchGroup() {
        return this.researchGroup;
    }

    public void setResearchGroup(ResearchGroup researchGroup) {
        this.researchGroup = researchGroup;
    }

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<SourceSpecimen> getSourceSpecimenCollection() {
        return this.sourceSpecimenCollection;
    }

    public void setSourceSpecimenCollection(
        Set<SourceSpecimen> sourceSpecimenCollection) {
        this.sourceSpecimenCollection = sourceSpecimenCollection;
    }
}
