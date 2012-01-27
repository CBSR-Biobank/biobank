package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.validator.NotEmpty;

public class Study extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String nameShort;
    private Collection<AliquotedSpecimen> aliquotedSpecimenCollection =
        new HashSet<AliquotedSpecimen>();
    private Collection<Patient> patientCollection = new HashSet<Patient>();
    private Collection<Site> siteCollection = new HashSet<Site>();
    private Collection<Comment> commentCollection = new HashSet<Comment>();
    private ActivityStatus activityStatus;
    private Collection<Membership> membershipCollection =
        new HashSet<Membership>();
    private Collection<StudyEventAttr> studyEventAttrCollection =
        new HashSet<StudyEventAttr>();
    private Collection<Contact> contactCollection = new HashSet<Contact>();
    private ResearchGroup researchGroup;
    private Collection<SourceSpecimen> sourceSpecimenCollection =
        new HashSet<SourceSpecimen>();

    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty
    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public Collection<AliquotedSpecimen> getAliquotedSpecimenCollection() {
        return aliquotedSpecimenCollection;
    }

    public void setAliquotedSpecimenCollection(
        Collection<AliquotedSpecimen> aliquotedSpecimenCollection) {
        this.aliquotedSpecimenCollection = aliquotedSpecimenCollection;
    }

    public Collection<Patient> getPatientCollection() {
        return patientCollection;
    }

    public void setPatientCollection(Collection<Patient> patientCollection) {
        this.patientCollection = patientCollection;
    }

    public Collection<Site> getSiteCollection() {
        return siteCollection;
    }

    public void setSiteCollection(Collection<Site> siteCollection) {
        this.siteCollection = siteCollection;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Collection<Membership> getMembershipCollection() {
        return membershipCollection;
    }

    public void setMembershipCollection(
        Collection<Membership> membershipCollection) {
        this.membershipCollection = membershipCollection;
    }

    public Collection<StudyEventAttr> getStudyEventAttrCollection() {
        return studyEventAttrCollection;
    }

    public void setStudyEventAttrCollection(
        Collection<StudyEventAttr> studyEventAttrCollection) {
        this.studyEventAttrCollection = studyEventAttrCollection;
    }

    public Collection<Contact> getContactCollection() {
        return contactCollection;
    }

    public void setContactCollection(Collection<Contact> contactCollection) {
        this.contactCollection = contactCollection;
    }

    public ResearchGroup getResearchGroup() {
        return researchGroup;
    }

    public void setResearchGroup(ResearchGroup researchGroup) {
        this.researchGroup = researchGroup;
    }

    public Collection<SourceSpecimen> getSourceSpecimenCollection() {
        return sourceSpecimenCollection;
    }

    public void setSourceSpecimenCollection(
        Collection<SourceSpecimen> sourceSpecimenCollection) {
        this.sourceSpecimenCollection = sourceSpecimenCollection;
    }
}
