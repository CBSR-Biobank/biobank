/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ResearchGroupBaseWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ContactBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.PatientBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.MembershipBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SiteBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyEventAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.StudyEventAttrBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.ActivityStatusBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.AliquotedSpecimenBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.SourceSpecimenBaseWrapper;
import java.util.Arrays;

public class StudyBaseWrapper extends ModelWrapper<Study> {

    public StudyBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public StudyBaseWrapper(WritableApplicationService appService,
        Study wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Study> getWrappedClass() {
        return Study.class;
    }

    @Override
    public Property<Integer, ? super Study> getIdProperty() {
        return StudyPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Study>> getProperties() {
        return StudyPeer.PROPERTIES;
    }

    public String getName() {
        return getProperty(StudyPeer.NAME);
    }

    public void setName(String name) {
        String trimmed = name == null ? null : name.trim();
        setProperty(StudyPeer.NAME, trimmed);
    }

    public String getNameShort() {
        return getProperty(StudyPeer.NAME_SHORT);
    }

    public void setNameShort(String nameShort) {
        String trimmed = nameShort == null ? null : nameShort.trim();
        setProperty(StudyPeer.NAME_SHORT, trimmed);
    }

    public ResearchGroupWrapper getResearchGroup() {
        boolean notCached = !isPropertyCached(StudyPeer.RESEARCH_GROUP);
        ResearchGroupWrapper researchGroup = getWrappedProperty(StudyPeer.RESEARCH_GROUP, ResearchGroupWrapper.class);
        if (researchGroup != null && notCached) ((ResearchGroupBaseWrapper) researchGroup).setStudyInternal(this);
        return researchGroup;
    }

    public void setResearchGroup(ResearchGroupBaseWrapper researchGroup) {
        if (isInitialized(StudyPeer.RESEARCH_GROUP)) {
            ResearchGroupBaseWrapper oldResearchGroup = getResearchGroup();
            if (oldResearchGroup != null) oldResearchGroup.setStudyInternal(null);
        }
        if (researchGroup != null) researchGroup.setStudyInternal(this);
        setWrappedProperty(StudyPeer.RESEARCH_GROUP, researchGroup);
    }

    void setResearchGroupInternal(ResearchGroupBaseWrapper researchGroup) {
        setWrappedProperty(StudyPeer.RESEARCH_GROUP, researchGroup);
    }

    public List<ContactWrapper> getContactCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.CONTACT_COLLECTION);
        List<ContactWrapper> contactCollection = getWrapperCollection(StudyPeer.CONTACT_COLLECTION, ContactWrapper.class, sort);
        if (notCached) {
            for (ContactBaseWrapper e : contactCollection) {
                e.addToStudyCollectionInternal(Arrays.asList(this));
            }
        }
        return contactCollection;
    }

    public void addToContactCollection(List<? extends ContactBaseWrapper> contactCollection) {
        addToWrapperCollection(StudyPeer.CONTACT_COLLECTION, contactCollection);
        for (ContactBaseWrapper e : contactCollection) {
            e.addToStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void addToContactCollectionInternal(List<? extends ContactBaseWrapper> contactCollection) {
        if (isInitialized(StudyPeer.CONTACT_COLLECTION)) {
            addToWrapperCollection(StudyPeer.CONTACT_COLLECTION, contactCollection);
        } else {
            getElementQueue().add(StudyPeer.CONTACT_COLLECTION, contactCollection);
        }
    }

    public void removeFromContactCollection(List<? extends ContactBaseWrapper> contactCollection) {
        removeFromWrapperCollection(StudyPeer.CONTACT_COLLECTION, contactCollection);
        for (ContactBaseWrapper e : contactCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromContactCollectionInternal(List<? extends ContactBaseWrapper> contactCollection) {
        if (isPropertyCached(StudyPeer.CONTACT_COLLECTION)) {
            removeFromWrapperCollection(StudyPeer.CONTACT_COLLECTION, contactCollection);
        } else {
            getElementQueue().remove(StudyPeer.CONTACT_COLLECTION, contactCollection);
        }
    }

    public void removeFromContactCollectionWithCheck(List<? extends ContactBaseWrapper> contactCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.CONTACT_COLLECTION, contactCollection);
        for (ContactBaseWrapper e : contactCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromContactCollectionWithCheckInternal(List<? extends ContactBaseWrapper> contactCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.CONTACT_COLLECTION, contactCollection);
    }

    public List<PatientWrapper> getPatientCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.PATIENT_COLLECTION);
        List<PatientWrapper> patientCollection = getWrapperCollection(StudyPeer.PATIENT_COLLECTION, PatientWrapper.class, sort);
        if (notCached) {
            for (PatientBaseWrapper e : patientCollection) {
                e.setStudyInternal(this);
            }
        }
        return patientCollection;
    }

    public void addToPatientCollection(List<? extends PatientBaseWrapper> patientCollection) {
        addToWrapperCollection(StudyPeer.PATIENT_COLLECTION, patientCollection);
        for (PatientBaseWrapper e : patientCollection) {
            e.setStudyInternal(this);
        }
    }

    void addToPatientCollectionInternal(List<? extends PatientBaseWrapper> patientCollection) {
        if (isInitialized(StudyPeer.PATIENT_COLLECTION)) {
            addToWrapperCollection(StudyPeer.PATIENT_COLLECTION, patientCollection);
        } else {
            getElementQueue().add(StudyPeer.PATIENT_COLLECTION, patientCollection);
        }
    }

    public void removeFromPatientCollection(List<? extends PatientBaseWrapper> patientCollection) {
        removeFromWrapperCollection(StudyPeer.PATIENT_COLLECTION, patientCollection);
        for (PatientBaseWrapper e : patientCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromPatientCollectionInternal(List<? extends PatientBaseWrapper> patientCollection) {
        if (isPropertyCached(StudyPeer.PATIENT_COLLECTION)) {
            removeFromWrapperCollection(StudyPeer.PATIENT_COLLECTION, patientCollection);
        } else {
            getElementQueue().remove(StudyPeer.PATIENT_COLLECTION, patientCollection);
        }
    }

    public void removeFromPatientCollectionWithCheck(List<? extends PatientBaseWrapper> patientCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.PATIENT_COLLECTION, patientCollection);
        for (PatientBaseWrapper e : patientCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromPatientCollectionWithCheckInternal(List<? extends PatientBaseWrapper> patientCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.PATIENT_COLLECTION, patientCollection);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection = getWrapperCollection(StudyPeer.COMMENT_COLLECTION, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(StudyPeer.COMMENT_COLLECTION, commentCollection);
    }

    void addToCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(StudyPeer.COMMENT_COLLECTION)) {
            addToWrapperCollection(StudyPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().add(StudyPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollection(List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(StudyPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionInternal(List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(StudyPeer.COMMENT_COLLECTION)) {
            removeFromWrapperCollection(StudyPeer.COMMENT_COLLECTION, commentCollection);
        } else {
            getElementQueue().remove(StudyPeer.COMMENT_COLLECTION, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.COMMENT_COLLECTION, commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(List<? extends CommentBaseWrapper> commentCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.COMMENT_COLLECTION, commentCollection);
    }

    public List<MembershipWrapper> getMembershipCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.MEMBERSHIP_COLLECTION);
        List<MembershipWrapper> membershipCollection = getWrapperCollection(StudyPeer.MEMBERSHIP_COLLECTION, MembershipWrapper.class, sort);
        if (notCached) {
            for (MembershipBaseWrapper e : membershipCollection) {
                e.setStudyInternal(this);
            }
        }
        return membershipCollection;
    }

    public void addToMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        addToWrapperCollection(StudyPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setStudyInternal(this);
        }
    }

    void addToMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isInitialized(StudyPeer.MEMBERSHIP_COLLECTION)) {
            addToWrapperCollection(StudyPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        } else {
            getElementQueue().add(StudyPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        }
    }

    public void removeFromMembershipCollection(List<? extends MembershipBaseWrapper> membershipCollection) {
        removeFromWrapperCollection(StudyPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromMembershipCollectionInternal(List<? extends MembershipBaseWrapper> membershipCollection) {
        if (isPropertyCached(StudyPeer.MEMBERSHIP_COLLECTION)) {
            removeFromWrapperCollection(StudyPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        } else {
            getElementQueue().remove(StudyPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        }
    }

    public void removeFromMembershipCollectionWithCheck(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.MEMBERSHIP_COLLECTION, membershipCollection);
        for (MembershipBaseWrapper e : membershipCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromMembershipCollectionWithCheckInternal(List<? extends MembershipBaseWrapper> membershipCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.MEMBERSHIP_COLLECTION, membershipCollection);
    }

    public List<SiteWrapper> getSiteCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.SITE_COLLECTION);
        List<SiteWrapper> siteCollection = getWrapperCollection(StudyPeer.SITE_COLLECTION, SiteWrapper.class, sort);
        if (notCached) {
            for (SiteBaseWrapper e : siteCollection) {
                e.addToStudyCollectionInternal(Arrays.asList(this));
            }
        }
        return siteCollection;
    }

    public void addToSiteCollection(List<? extends SiteBaseWrapper> siteCollection) {
        addToWrapperCollection(StudyPeer.SITE_COLLECTION, siteCollection);
        for (SiteBaseWrapper e : siteCollection) {
            e.addToStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void addToSiteCollectionInternal(List<? extends SiteBaseWrapper> siteCollection) {
        if (isInitialized(StudyPeer.SITE_COLLECTION)) {
            addToWrapperCollection(StudyPeer.SITE_COLLECTION, siteCollection);
        } else {
            getElementQueue().add(StudyPeer.SITE_COLLECTION, siteCollection);
        }
    }

    public void removeFromSiteCollection(List<? extends SiteBaseWrapper> siteCollection) {
        removeFromWrapperCollection(StudyPeer.SITE_COLLECTION, siteCollection);
        for (SiteBaseWrapper e : siteCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromSiteCollectionInternal(List<? extends SiteBaseWrapper> siteCollection) {
        if (isPropertyCached(StudyPeer.SITE_COLLECTION)) {
            removeFromWrapperCollection(StudyPeer.SITE_COLLECTION, siteCollection);
        } else {
            getElementQueue().remove(StudyPeer.SITE_COLLECTION, siteCollection);
        }
    }

    public void removeFromSiteCollectionWithCheck(List<? extends SiteBaseWrapper> siteCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.SITE_COLLECTION, siteCollection);
        for (SiteBaseWrapper e : siteCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromSiteCollectionWithCheckInternal(List<? extends SiteBaseWrapper> siteCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.SITE_COLLECTION, siteCollection);
    }

    public List<StudyEventAttrWrapper> getStudyEventAttrCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.STUDY_EVENT_ATTR_COLLECTION);
        List<StudyEventAttrWrapper> studyEventAttrCollection = getWrapperCollection(StudyPeer.STUDY_EVENT_ATTR_COLLECTION, StudyEventAttrWrapper.class, sort);
        if (notCached) {
            for (StudyEventAttrBaseWrapper e : studyEventAttrCollection) {
                e.setStudyInternal(this);
            }
        }
        return studyEventAttrCollection;
    }

    public void addToStudyEventAttrCollection(List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) {
        addToWrapperCollection(StudyPeer.STUDY_EVENT_ATTR_COLLECTION, studyEventAttrCollection);
        for (StudyEventAttrBaseWrapper e : studyEventAttrCollection) {
            e.setStudyInternal(this);
        }
    }

    void addToStudyEventAttrCollectionInternal(List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) {
        if (isInitialized(StudyPeer.STUDY_EVENT_ATTR_COLLECTION)) {
            addToWrapperCollection(StudyPeer.STUDY_EVENT_ATTR_COLLECTION, studyEventAttrCollection);
        } else {
            getElementQueue().add(StudyPeer.STUDY_EVENT_ATTR_COLLECTION, studyEventAttrCollection);
        }
    }

    public void removeFromStudyEventAttrCollection(List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) {
        removeFromWrapperCollection(StudyPeer.STUDY_EVENT_ATTR_COLLECTION, studyEventAttrCollection);
        for (StudyEventAttrBaseWrapper e : studyEventAttrCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromStudyEventAttrCollectionInternal(List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) {
        if (isPropertyCached(StudyPeer.STUDY_EVENT_ATTR_COLLECTION)) {
            removeFromWrapperCollection(StudyPeer.STUDY_EVENT_ATTR_COLLECTION, studyEventAttrCollection);
        } else {
            getElementQueue().remove(StudyPeer.STUDY_EVENT_ATTR_COLLECTION, studyEventAttrCollection);
        }
    }

    public void removeFromStudyEventAttrCollectionWithCheck(List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.STUDY_EVENT_ATTR_COLLECTION, studyEventAttrCollection);
        for (StudyEventAttrBaseWrapper e : studyEventAttrCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromStudyEventAttrCollectionWithCheckInternal(List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.STUDY_EVENT_ATTR_COLLECTION, studyEventAttrCollection);
    }

    public ActivityStatusWrapper getActivityStatus() {
        ActivityStatusWrapper activityStatus = getWrappedProperty(StudyPeer.ACTIVITY_STATUS, ActivityStatusWrapper.class);
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatusBaseWrapper activityStatus) {
        setWrappedProperty(StudyPeer.ACTIVITY_STATUS, activityStatus);
    }

    void setActivityStatusInternal(ActivityStatusBaseWrapper activityStatus) {
        setWrappedProperty(StudyPeer.ACTIVITY_STATUS, activityStatus);
    }

    public List<AliquotedSpecimenWrapper> getAliquotedSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION);
        List<AliquotedSpecimenWrapper> aliquotedSpecimenCollection = getWrapperCollection(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION, AliquotedSpecimenWrapper.class, sort);
        if (notCached) {
            for (AliquotedSpecimenBaseWrapper e : aliquotedSpecimenCollection) {
                e.setStudyInternal(this);
            }
        }
        return aliquotedSpecimenCollection;
    }

    public void addToAliquotedSpecimenCollection(List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) {
        addToWrapperCollection(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION, aliquotedSpecimenCollection);
        for (AliquotedSpecimenBaseWrapper e : aliquotedSpecimenCollection) {
            e.setStudyInternal(this);
        }
    }

    void addToAliquotedSpecimenCollectionInternal(List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) {
        if (isInitialized(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION)) {
            addToWrapperCollection(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION, aliquotedSpecimenCollection);
        } else {
            getElementQueue().add(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION, aliquotedSpecimenCollection);
        }
    }

    public void removeFromAliquotedSpecimenCollection(List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) {
        removeFromWrapperCollection(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION, aliquotedSpecimenCollection);
        for (AliquotedSpecimenBaseWrapper e : aliquotedSpecimenCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromAliquotedSpecimenCollectionInternal(List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) {
        if (isPropertyCached(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION)) {
            removeFromWrapperCollection(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION, aliquotedSpecimenCollection);
        } else {
            getElementQueue().remove(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION, aliquotedSpecimenCollection);
        }
    }

    public void removeFromAliquotedSpecimenCollectionWithCheck(List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION, aliquotedSpecimenCollection);
        for (AliquotedSpecimenBaseWrapper e : aliquotedSpecimenCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromAliquotedSpecimenCollectionWithCheckInternal(List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.ALIQUOTED_SPECIMEN_COLLECTION, aliquotedSpecimenCollection);
    }

    public List<SourceSpecimenWrapper> getSourceSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.SOURCE_SPECIMEN_COLLECTION);
        List<SourceSpecimenWrapper> sourceSpecimenCollection = getWrapperCollection(StudyPeer.SOURCE_SPECIMEN_COLLECTION, SourceSpecimenWrapper.class, sort);
        if (notCached) {
            for (SourceSpecimenBaseWrapper e : sourceSpecimenCollection) {
                e.setStudyInternal(this);
            }
        }
        return sourceSpecimenCollection;
    }

    public void addToSourceSpecimenCollection(List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) {
        addToWrapperCollection(StudyPeer.SOURCE_SPECIMEN_COLLECTION, sourceSpecimenCollection);
        for (SourceSpecimenBaseWrapper e : sourceSpecimenCollection) {
            e.setStudyInternal(this);
        }
    }

    void addToSourceSpecimenCollectionInternal(List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) {
        if (isInitialized(StudyPeer.SOURCE_SPECIMEN_COLLECTION)) {
            addToWrapperCollection(StudyPeer.SOURCE_SPECIMEN_COLLECTION, sourceSpecimenCollection);
        } else {
            getElementQueue().add(StudyPeer.SOURCE_SPECIMEN_COLLECTION, sourceSpecimenCollection);
        }
    }

    public void removeFromSourceSpecimenCollection(List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) {
        removeFromWrapperCollection(StudyPeer.SOURCE_SPECIMEN_COLLECTION, sourceSpecimenCollection);
        for (SourceSpecimenBaseWrapper e : sourceSpecimenCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromSourceSpecimenCollectionInternal(List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) {
        if (isPropertyCached(StudyPeer.SOURCE_SPECIMEN_COLLECTION)) {
            removeFromWrapperCollection(StudyPeer.SOURCE_SPECIMEN_COLLECTION, sourceSpecimenCollection);
        } else {
            getElementQueue().remove(StudyPeer.SOURCE_SPECIMEN_COLLECTION, sourceSpecimenCollection);
        }
    }

    public void removeFromSourceSpecimenCollectionWithCheck(List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.SOURCE_SPECIMEN_COLLECTION, sourceSpecimenCollection);
        for (SourceSpecimenBaseWrapper e : sourceSpecimenCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromSourceSpecimenCollectionWithCheckInternal(List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.SOURCE_SPECIMEN_COLLECTION, sourceSpecimenCollection);
    }

}
