/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyEventAttrWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 *
 * Code Changes -
 * 		1> Changes related to having multiple Research Groups associated with a particular Study
 * 		2> Added methods to add and remove Research Groups similar to Sites in this class
 *
 * @author OHSDEV
 *
 */
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

    //OHSDEV -->
    public List<ResearchGroupWrapper> getResearchGroupCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.RESEARCH_GROUPS);
        List<ResearchGroupWrapper> rgCollection =
            getWrapperCollection(StudyPeer.RESEARCH_GROUPS, ResearchGroupWrapper.class, sort);
        if (notCached) {
            for (ResearchGroupBaseWrapper e : rgCollection) {
                e.addToStudyCollectionInternal(Arrays.asList(this));
            }
        }
        return rgCollection;
    }

    public void addToResearchGroupCollection(List<? extends ResearchGroupBaseWrapper> rgCollection) {
        addToWrapperCollection(StudyPeer.RESEARCH_GROUPS, rgCollection);
        for (ResearchGroupBaseWrapper e : rgCollection) {
            e.addToStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void addToResearchGroupCollectionInternal(List<? extends ResearchGroupBaseWrapper> rgCollection) {
        if (isInitialized(StudyPeer.RESEARCH_GROUPS)) {
            addToWrapperCollection(StudyPeer.RESEARCH_GROUPS, rgCollection);
        } else {
            getElementQueue().add(StudyPeer.RESEARCH_GROUPS, rgCollection);
        }
    }

    public void removeFromResearchGroupCollection(List<? extends ResearchGroupBaseWrapper> rgCollection) {
        removeFromWrapperCollection(StudyPeer.RESEARCH_GROUPS, rgCollection);
        for (ResearchGroupBaseWrapper e : rgCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromResearchGroupCollectionInternal(List<? extends ResearchGroupBaseWrapper> rgCollection) {
        if (isPropertyCached(StudyPeer.RESEARCH_GROUPS)) {
            removeFromWrapperCollection(StudyPeer.RESEARCH_GROUPS, rgCollection);
        } else {
            getElementQueue().remove(StudyPeer.RESEARCH_GROUPS, rgCollection);
        }
    }

    public void removeFromResearchGroupCollectionWithCheck(List<? extends ResearchGroupBaseWrapper> rgCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.RESEARCH_GROUPS, rgCollection);
        for (ResearchGroupBaseWrapper e : rgCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromResearchGroupCollectionWithCheckInternal(List<? extends ResearchGroupBaseWrapper> rgCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.RESEARCH_GROUPS, rgCollection);
    }
    // <-- OHSDEV

    public List<ContactWrapper> getContactCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.CONTACTS);
        List<ContactWrapper> contactCollection =
            getWrapperCollection(StudyPeer.CONTACTS, ContactWrapper.class, sort);
        if (notCached) {
            for (ContactBaseWrapper e : contactCollection) {
                e.addToStudyCollectionInternal(Arrays.asList(this));
            }
        }
        return contactCollection;
    }

    public void addToContactCollection(
        List<? extends ContactBaseWrapper> contactCollection) {
        addToWrapperCollection(StudyPeer.CONTACTS, contactCollection);
        for (ContactBaseWrapper e : contactCollection) {
            e.addToStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void addToContactCollectionInternal(
        List<? extends ContactBaseWrapper> contactCollection) {
        if (isInitialized(StudyPeer.CONTACTS)) {
            addToWrapperCollection(StudyPeer.CONTACTS, contactCollection);
        } else {
            getElementQueue().add(StudyPeer.CONTACTS, contactCollection);
        }
    }

    public void removeFromContactCollection(
        List<? extends ContactBaseWrapper> contactCollection) {
        removeFromWrapperCollection(StudyPeer.CONTACTS, contactCollection);
        for (ContactBaseWrapper e : contactCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromContactCollectionInternal(
        List<? extends ContactBaseWrapper> contactCollection) {
        if (isPropertyCached(StudyPeer.CONTACTS)) {
            removeFromWrapperCollection(StudyPeer.CONTACTS, contactCollection);
        } else {
            getElementQueue().remove(StudyPeer.CONTACTS, contactCollection);
        }
    }

    public void removeFromContactCollectionWithCheck(
        List<? extends ContactBaseWrapper> contactCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.CONTACTS,
            contactCollection);
        for (ContactBaseWrapper e : contactCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromContactCollectionWithCheckInternal(
        List<? extends ContactBaseWrapper> contactCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.CONTACTS,
            contactCollection);
    }

    public List<PatientWrapper> getPatientCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.PATIENTS);
        List<PatientWrapper> patientCollection =
            getWrapperCollection(StudyPeer.PATIENTS, PatientWrapper.class, sort);
        if (notCached) {
            for (PatientBaseWrapper e : patientCollection) {
                e.setStudyInternal(this);
            }
        }
        return patientCollection;
    }

    public void addToPatientCollection(
        List<? extends PatientBaseWrapper> patientCollection) {
        addToWrapperCollection(StudyPeer.PATIENTS, patientCollection);
        for (PatientBaseWrapper e : patientCollection) {
            e.setStudyInternal(this);
        }
    }

    void addToPatientCollectionInternal(
        List<? extends PatientBaseWrapper> patientCollection) {
        if (isInitialized(StudyPeer.PATIENTS)) {
            addToWrapperCollection(StudyPeer.PATIENTS, patientCollection);
        } else {
            getElementQueue().add(StudyPeer.PATIENTS, patientCollection);
        }
    }

    public void removeFromPatientCollection(
        List<? extends PatientBaseWrapper> patientCollection) {
        removeFromWrapperCollection(StudyPeer.PATIENTS, patientCollection);
        for (PatientBaseWrapper e : patientCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromPatientCollectionInternal(
        List<? extends PatientBaseWrapper> patientCollection) {
        if (isPropertyCached(StudyPeer.PATIENTS)) {
            removeFromWrapperCollection(StudyPeer.PATIENTS, patientCollection);
        } else {
            getElementQueue().remove(StudyPeer.PATIENTS, patientCollection);
        }
    }

    public void removeFromPatientCollectionWithCheck(
        List<? extends PatientBaseWrapper> patientCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.PATIENTS,
            patientCollection);
        for (PatientBaseWrapper e : patientCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromPatientCollectionWithCheckInternal(
        List<? extends PatientBaseWrapper> patientCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.PATIENTS,
            patientCollection);
    }

    public List<CommentWrapper> getCommentCollection(boolean sort) {
        List<CommentWrapper> commentCollection =
            getWrapperCollection(StudyPeer.COMMENTS, CommentWrapper.class, sort);
        return commentCollection;
    }

    public void addToCommentCollection(
        List<? extends CommentBaseWrapper> commentCollection) {
        addToWrapperCollection(StudyPeer.COMMENTS, commentCollection);
    }

    void addToCommentCollectionInternal(
        List<? extends CommentBaseWrapper> commentCollection) {
        if (isInitialized(StudyPeer.COMMENTS)) {
            addToWrapperCollection(StudyPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().add(StudyPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollection(
        List<? extends CommentBaseWrapper> commentCollection) {
        removeFromWrapperCollection(StudyPeer.COMMENTS, commentCollection);
    }

    void removeFromCommentCollectionInternal(
        List<? extends CommentBaseWrapper> commentCollection) {
        if (isPropertyCached(StudyPeer.COMMENTS)) {
            removeFromWrapperCollection(StudyPeer.COMMENTS, commentCollection);
        } else {
            getElementQueue().remove(StudyPeer.COMMENTS, commentCollection);
        }
    }

    public void removeFromCommentCollectionWithCheck(
        List<? extends CommentBaseWrapper> commentCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.COMMENTS,
            commentCollection);
    }

    void removeFromCommentCollectionWithCheckInternal(
        List<? extends CommentBaseWrapper> commentCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.COMMENTS,
            commentCollection);
    }

    public List<SiteWrapper> getSiteCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.SITES);
        List<SiteWrapper> siteCollection =
            getWrapperCollection(StudyPeer.SITES, SiteWrapper.class, sort);
        if (notCached) {
            for (SiteBaseWrapper e : siteCollection) {
                e.addToStudyCollectionInternal(Arrays.asList(this));
            }
        }
        return siteCollection;
    }

    public void addToSiteCollection(
        List<? extends SiteBaseWrapper> siteCollection) {
        addToWrapperCollection(StudyPeer.SITES, siteCollection);
        for (SiteBaseWrapper e : siteCollection) {
            e.addToStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void addToSiteCollectionInternal(
        List<? extends SiteBaseWrapper> siteCollection) {
        if (isInitialized(StudyPeer.SITES)) {
            addToWrapperCollection(StudyPeer.SITES, siteCollection);
        } else {
            getElementQueue().add(StudyPeer.SITES, siteCollection);
        }
    }

    public void removeFromSiteCollection(
        List<? extends SiteBaseWrapper> siteCollection) {
        removeFromWrapperCollection(StudyPeer.SITES, siteCollection);
        for (SiteBaseWrapper e : siteCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromSiteCollectionInternal(
        List<? extends SiteBaseWrapper> siteCollection) {
        if (isPropertyCached(StudyPeer.SITES)) {
            removeFromWrapperCollection(StudyPeer.SITES, siteCollection);
        } else {
            getElementQueue().remove(StudyPeer.SITES, siteCollection);
        }
    }

    public void removeFromSiteCollectionWithCheck(
        List<? extends SiteBaseWrapper> siteCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.SITES, siteCollection);
        for (SiteBaseWrapper e : siteCollection) {
            e.removeFromStudyCollectionInternal(Arrays.asList(this));
        }
    }

    void removeFromSiteCollectionWithCheckInternal(
        List<? extends SiteBaseWrapper> siteCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.SITES, siteCollection);
    }

    public List<StudyEventAttrWrapper> getStudyEventAttrCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.STUDY_EVENT_ATTRS);
        List<StudyEventAttrWrapper> studyEventAttrCollection =
            getWrapperCollection(StudyPeer.STUDY_EVENT_ATTRS,
                StudyEventAttrWrapper.class, sort);
        if (notCached) {
            for (StudyEventAttrBaseWrapper e : studyEventAttrCollection) {
                e.setStudyInternal(this);
            }
        }
        return studyEventAttrCollection;
    }

    public void addToStudyEventAttrCollection(
        List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) {
        addToWrapperCollection(StudyPeer.STUDY_EVENT_ATTRS,
            studyEventAttrCollection);
        for (StudyEventAttrBaseWrapper e : studyEventAttrCollection) {
            e.setStudyInternal(this);
        }
    }

    void addToStudyEventAttrCollectionInternal(
        List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) {
        if (isInitialized(StudyPeer.STUDY_EVENT_ATTRS)) {
            addToWrapperCollection(StudyPeer.STUDY_EVENT_ATTRS,
                studyEventAttrCollection);
        } else {
            getElementQueue().add(StudyPeer.STUDY_EVENT_ATTRS,
                studyEventAttrCollection);
        }
    }

    public void removeFromStudyEventAttrCollection(
        List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) {
        removeFromWrapperCollection(StudyPeer.STUDY_EVENT_ATTRS,
            studyEventAttrCollection);
        for (StudyEventAttrBaseWrapper e : studyEventAttrCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromStudyEventAttrCollectionInternal(
        List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection) {
        if (isPropertyCached(StudyPeer.STUDY_EVENT_ATTRS)) {
            removeFromWrapperCollection(StudyPeer.STUDY_EVENT_ATTRS,
                studyEventAttrCollection);
        } else {
            getElementQueue().remove(StudyPeer.STUDY_EVENT_ATTRS,
                studyEventAttrCollection);
        }
    }

    public void removeFromStudyEventAttrCollectionWithCheck(
        List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.STUDY_EVENT_ATTRS,
            studyEventAttrCollection);
        for (StudyEventAttrBaseWrapper e : studyEventAttrCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromStudyEventAttrCollectionWithCheckInternal(
        List<? extends StudyEventAttrBaseWrapper> studyEventAttrCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.STUDY_EVENT_ATTRS,
            studyEventAttrCollection);
    }

    public ActivityStatus getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        wrappedObject.setActivityStatus(activityStatus);
    }

    public List<AliquotedSpecimenWrapper> getAliquotedSpecimenCollection(
        boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.ALIQUOTED_SPECIMENS);
        List<AliquotedSpecimenWrapper> aliquotedSpecimenCollection =
            getWrapperCollection(StudyPeer.ALIQUOTED_SPECIMENS,
                AliquotedSpecimenWrapper.class, sort);
        if (notCached) {
            for (AliquotedSpecimenBaseWrapper e : aliquotedSpecimenCollection) {
                e.setStudyInternal(this);
            }
        }
        return aliquotedSpecimenCollection;
    }

    public void addToAliquotedSpecimenCollection(
        List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) {
        addToWrapperCollection(StudyPeer.ALIQUOTED_SPECIMENS,
            aliquotedSpecimenCollection);
        for (AliquotedSpecimenBaseWrapper e : aliquotedSpecimenCollection) {
            e.setStudyInternal(this);
        }
    }

    void addToAliquotedSpecimenCollectionInternal(
        List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) {
        if (isInitialized(StudyPeer.ALIQUOTED_SPECIMENS)) {
            addToWrapperCollection(StudyPeer.ALIQUOTED_SPECIMENS,
                aliquotedSpecimenCollection);
        } else {
            getElementQueue().add(StudyPeer.ALIQUOTED_SPECIMENS,
                aliquotedSpecimenCollection);
        }
    }

    public void removeFromAliquotedSpecimenCollection(
        List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) {
        removeFromWrapperCollection(StudyPeer.ALIQUOTED_SPECIMENS,
            aliquotedSpecimenCollection);
        for (AliquotedSpecimenBaseWrapper e : aliquotedSpecimenCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromAliquotedSpecimenCollectionInternal(
        List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection) {
        if (isPropertyCached(StudyPeer.ALIQUOTED_SPECIMENS)) {
            removeFromWrapperCollection(StudyPeer.ALIQUOTED_SPECIMENS,
                aliquotedSpecimenCollection);
        } else {
            getElementQueue().remove(StudyPeer.ALIQUOTED_SPECIMENS,
                aliquotedSpecimenCollection);
        }
    }

    public void removeFromAliquotedSpecimenCollectionWithCheck(
        List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.ALIQUOTED_SPECIMENS,
            aliquotedSpecimenCollection);
        for (AliquotedSpecimenBaseWrapper e : aliquotedSpecimenCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromAliquotedSpecimenCollectionWithCheckInternal(
        List<? extends AliquotedSpecimenBaseWrapper> aliquotedSpecimenCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.ALIQUOTED_SPECIMENS,
            aliquotedSpecimenCollection);
    }

    public List<SourceSpecimenWrapper> getSourceSpecimenCollection(boolean sort) {
        boolean notCached = !isPropertyCached(StudyPeer.SOURCE_SPECIMENS);
        List<SourceSpecimenWrapper> sourceSpecimenCollection =
            getWrapperCollection(StudyPeer.SOURCE_SPECIMENS,
                SourceSpecimenWrapper.class, sort);
        if (notCached) {
            for (SourceSpecimenBaseWrapper e : sourceSpecimenCollection) {
                e.setStudyInternal(this);
            }
        }
        return sourceSpecimenCollection;
    }

    public void addToSourceSpecimenCollection(
        List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) {
        addToWrapperCollection(StudyPeer.SOURCE_SPECIMENS,
            sourceSpecimenCollection);
        for (SourceSpecimenBaseWrapper e : sourceSpecimenCollection) {
            e.setStudyInternal(this);
        }
    }

    void addToSourceSpecimenCollectionInternal(
        List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) {
        if (isInitialized(StudyPeer.SOURCE_SPECIMENS)) {
            addToWrapperCollection(StudyPeer.SOURCE_SPECIMENS,
                sourceSpecimenCollection);
        } else {
            getElementQueue().add(StudyPeer.SOURCE_SPECIMENS,
                sourceSpecimenCollection);
        }
    }

    public void removeFromSourceSpecimenCollection(
        List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) {
        removeFromWrapperCollection(StudyPeer.SOURCE_SPECIMENS,
            sourceSpecimenCollection);
        for (SourceSpecimenBaseWrapper e : sourceSpecimenCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromSourceSpecimenCollectionInternal(
        List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection) {
        if (isPropertyCached(StudyPeer.SOURCE_SPECIMENS)) {
            removeFromWrapperCollection(StudyPeer.SOURCE_SPECIMENS,
                sourceSpecimenCollection);
        } else {
            getElementQueue().remove(StudyPeer.SOURCE_SPECIMENS,
                sourceSpecimenCollection);
        }
    }

    public void removeFromSourceSpecimenCollectionWithCheck(
        List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.SOURCE_SPECIMENS,
            sourceSpecimenCollection);
        for (SourceSpecimenBaseWrapper e : sourceSpecimenCollection) {
            e.setStudyInternal(null);
        }
    }

    void removeFromSourceSpecimenCollectionWithCheckInternal(
        List<? extends SourceSpecimenBaseWrapper> sourceSpecimenCollection)
        throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(StudyPeer.SOURCE_SPECIMENS,
            sourceSpecimenCollection);
    }

}