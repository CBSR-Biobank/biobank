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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.constraint.Empty;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * ET: Research conducted on a specific group of people to discover a determined
 * result; has one specific protocol
 * 
 * caTissue Term - Collection Protocol: A set of written procedures that
 * describe how a biospecimen is collected.
 * 
 * Code Changes -
 * 		1> Convert OneToOne relation to ManyToMany for getting & setting Research Groups
 * 		2> Change the class variable to accept a set of Research Groups associated to a Study
 *
 * @author OHSDEV
 *
 */
@Entity
@Table(name = "STUDY")
@Unique.List({
    @Unique(properties = "name", groups = PrePersist.class),
    @Unique(properties = "nameShort", groups = PrePersist.class)
})
@Empty(property = "patients", groups = PreDelete.class)
public class Study extends AbstractBiobankModel
    implements HasName, HasNameShort, HasActivityStatus, HasComments {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Study",
        "Studies");

    private String name;
    private String nameShort;
    private Set<AliquotedSpecimen> aliquotedSpecimens =
        new HashSet<AliquotedSpecimen>(0);
    private Set<Patient> patients = new HashSet<Patient>(0);
    private Set<Site> sites = new HashSet<Site>(0);
    private Set<Comment> comments = new HashSet<Comment>(0);
    private ActivityStatus activityStatus = ActivityStatus.ACTIVE;
    private Set<StudyEventAttr> studyEventAttrs =
        new HashSet<StudyEventAttr>(0);
    private Set<Contact> contacts = new HashSet<Contact>(0);
    private Set<ResearchGroup> researchGroups = new HashSet<ResearchGroup>(0);		//OHSDEV
    private Set<SourceSpecimen> sourceSpecimens =
        new HashSet<SourceSpecimen>(0);

    @Override
    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Study.name.NotEmpty}")
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @NotEmpty(message = "{edu.ualberta.med.biobank.model.Study.nameShort.NotEmpty}")
    @Column(name = "NAME_SHORT", unique = true, nullable = false, length = 50)
    public String getNameShort() {
        return this.nameShort;
    }

    @Override
    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<AliquotedSpecimen> getAliquotedSpecimens() {
        return this.aliquotedSpecimens;
    }

    public void setAliquotedSpecimens(Set<AliquotedSpecimen> aliquotedSpecimens) {
        this.aliquotedSpecimens = aliquotedSpecimens;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<Patient> getPatients() {
        return this.patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients = patients;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "studies")
    public Set<Site> getSites() {
        return this.sites;
    }

    public void setSites(Set<Site> sites) {
        this.sites = sites;
    }

    @Override
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "STUDY_COMMENT",
        joinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    @Override
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @Override
    @NotNull(message = "{edu.ualberta.med.biobank.model.Study.activityStatus.NotEmpty}")
    @Column(name = "ACTIVITY_STATUS_ID", nullable = false)
    @Type(type = "activityStatus")
    public ActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    @Override
    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<StudyEventAttr> getStudyEventAttrs() {
        return this.studyEventAttrs;
    }

    public void setStudyEventAttrs(Set<StudyEventAttr> studyEventAttrs) {
        this.studyEventAttrs = studyEventAttrs;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "STUDY_CONTACT",
        joinColumns = { @JoinColumn(name = "STUDY_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CONTACT_ID", nullable = false, updatable = false) })
    public Set<Contact> getContacts() {
        return this.contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    //OHSDEV -->
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "studies")
    public Set<ResearchGroup> getResearchGroups() {
        return this.researchGroups;
    }

    public void setResearchGroups(Set<ResearchGroup> researchGroups) {
        this.researchGroups = researchGroups;
    }
    // <-- OHSDEV

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<SourceSpecimen> getSourceSpecimens() {
        return this.sourceSpecimens;
    }

    public void setSourceSpecimens(Set<SourceSpecimen> sourceSpecimens) {
        this.sourceSpecimens = sourceSpecimens;
    }
}
