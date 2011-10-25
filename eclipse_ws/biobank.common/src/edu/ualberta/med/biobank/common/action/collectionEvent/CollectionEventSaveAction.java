package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.CollectionUtils;
import edu.ualberta.med.biobank.common.action.CommentInfo;
import edu.ualberta.med.biobank.common.action.DiffUtils;
import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.study.GetStudyEventAttrInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyEventAttrInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventSaveAction implements Action<Integer> {

    private static final long serialVersionUID = 1L;

    private Integer ceventId;
    private Integer patientId;
    private Integer visitNumber;
    private Integer statusId;
    private Collection<CommentInfo> comments;

    public static class SaveCEventSpecimenInfo implements Serializable,
        NotAProxy {
        private static final long serialVersionUID = 1L;

        public Integer id;
        public String inventoryId;
        public Date timeDrawn;
        public Integer statusId;
        public Integer specimenTypeId;
        public Collection<CommentInfo> comments;
        public Double quantity;
    }

    public static class SaveCEventAttrInfo implements Serializable, NotAProxy {

        private static final long serialVersionUID = 1L;
        public Integer studyEventAttrId;
        public EventAttrTypeEnum type;
        public String value;

    }

    private Collection<SaveCEventSpecimenInfo> sourceSpecimens;

    private Integer centerId;

    private List<SaveCEventAttrInfo> ceAttrList;

    public CollectionEventSaveAction(Integer ceventId, Integer patientId,
        Integer visitNumber, Integer statusId,
        Collection<CommentInfo> comments,
        Integer centerId, Collection<SaveCEventSpecimenInfo> sourceSpecs,
        List<SaveCEventAttrInfo> ceAttrList) {
        this.ceventId = ceventId;
        this.patientId = patientId;
        this.visitNumber = visitNumber;
        this.statusId = statusId;
        this.comments = comments;
        this.centerId = centerId;
        this.sourceSpecimens = sourceSpecs;
        this.ceAttrList = ceAttrList;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {

        check(user, session);

        CollectionEvent ceventToSave;
        if (ceventId == null) {
            ceventToSave = new CollectionEvent();
        } else {
            ceventToSave = ActionUtil.sessionGet(session,
                CollectionEvent.class, ceventId);
        }

        // FIXME Version check?
        // FIXME permission ?

        Patient patient = ActionUtil.sessionGet(session, Patient.class,
            patientId);
        ceventToSave.setPatient(patient);
        ceventToSave.setVisitNumber(visitNumber);
        ceventToSave.setActivityStatus(ActionUtil.sessionGet(session,
            ActivityStatus.class, statusId));

        Collection<Comment> commentsToSave = CollectionUtils.getCollection(
            ceventToSave,
            CollectionEventPeer.COMMENT_COLLECTION);
        CommentInfo
            .setCommentModelCollection(session, commentsToSave, comments);

        setSourceSpecimens(session, ceventToSave);

        setEventAttrs(session, user, patient.getStudy(), ceventToSave);

        session.saveOrUpdate(ceventToSave);

        return ceventToSave.getId();
    }

    private void check(User user, Session session) {
        // Check that the visit number is unique for the patient
        List<ValueProperty<CollectionEvent>> propUple = new ArrayList<ValueProperty<CollectionEvent>>();
        propUple.add(new ValueProperty<CollectionEvent>(
            CollectionEventPeer.PATIENT.to(PatientPeer.ID), patientId));
        propUple.add(new ValueProperty<CollectionEvent>(
            CollectionEventPeer.VISIT_NUMBER, visitNumber));
        new UniquePreCheck<CollectionEvent>(new ValueProperty<CollectionEvent>(
            CollectionEventPeer.ID, ceventId), CollectionEvent.class, propUple)
            .run(user, session);
    }

    private void setSourceSpecimens(Session session,
        CollectionEvent ceventToSave) {
        DiffUtils<Specimen> originalSpec = new DiffUtils<Specimen>(
            CollectionUtils.getCollection(ceventToSave,
                CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION));
        Collection<Specimen> allSpec = CollectionUtils.getCollection(
            ceventToSave, CollectionEventPeer.ALL_SPECIMEN_COLLECTION);

        if (sourceSpecimens != null) {
            OriginInfo oi = new OriginInfo();
            oi.setCenter(ActionUtil.sessionGet(session, Center.class, centerId));
            session.saveOrUpdate(oi);
            for (SaveCEventSpecimenInfo specInfo : sourceSpecimens) {
                Specimen specimen;
                if (specInfo.id == null) {
                    specimen = new Specimen();
                    specimen.setCurrentCenter(oi.getCenter());
                    specimen.setOriginInfo(oi);
                } else {
                    specimen = ActionUtil.sessionGet(session, Specimen.class,
                        specInfo.id);
                }
                specimen.setActivityStatus(ActionUtil.sessionGet(session,
                    ActivityStatus.class, specInfo.statusId));
                specimen.setCollectionEvent(ceventToSave);
                // cascade will save-update the specimens from this list:
                allSpec.add(specimen);
                specimen.setOriginalCollectionEvent(ceventToSave);
                originalSpec.add(specimen);
                Collection<Comment> commentsToSave = CollectionUtils
                    .getCollection(
                        specimen,
                        SpecimenPeer.COMMENT_COLLECTION);
                CommentInfo.setCommentModelCollection(session, commentsToSave,
                    specInfo.comments);
                specimen.setCreatedAt(specInfo.timeDrawn);
                specimen.setInventoryId(specInfo.inventoryId);
                specimen.setQuantity(specInfo.quantity);
                specimen.setSpecimenType(ActionUtil.sessionGet(session,
                    SpecimenType.class, specInfo.specimenTypeId));
            }
        }
        // need to remove from collections. the delete-orphan cascade on
        // allspecimencollection will delete orphans
        Collection<Specimen> removedSpecimens = originalSpec.pullRemoved();
        allSpec.removeAll(removedSpecimens);
        for (Specimen sp : removedSpecimens) {
            session.delete(sp);
        }
    }

    public void setEventAttrs(Session session, User user, Study study,
        CollectionEvent cevent) throws ActionException {
        Map<Integer, StudyEventAttrInfo> studyEventList = new GetStudyEventAttrInfoAction(
            study.getId()).run(user, session);

        Map<Integer, EventAttrInfo> ceventAttrList = new CollectionEventGetEventAttrInfoAction(
            ceventId).run(user, session);
        if (ceAttrList != null)
            for (SaveCEventAttrInfo attrInfo : ceAttrList) {
                EventAttrInfo ceventAttrInfo = ceventAttrList
                    .get(attrInfo.studyEventAttrId);
                StudyEventAttrInfo studyEventAttrInfo = studyEventList
                    .get(attrInfo.studyEventAttrId);

                StudyEventAttr sAttr;

                if (ceventAttrInfo != null) {
                    sAttr = ceventAttrInfo.attr.getStudyEventAttr();
                } else {
                    sAttr = studyEventAttrInfo == null ? null
                        : studyEventAttrInfo.attr;
                    if (sAttr == null) {
                        throw new ActionException(
                            "no StudyEventAttr found for id \"" //$NON-NLS-1$
                                + attrInfo.studyEventAttrId + "\""); //$NON-NLS-1$
                    }
                }

                if (!ActivityStatusEnum.ACTIVE.getId().equals(
                    sAttr.getActivityStatus().getId())) {
                    throw new ActionException(
                        "Attribute for \"" + sAttr.getLabel() //$NON-NLS-1$
                            + "\" is locked, changes not premitted"); //$NON-NLS-1$
                }

                if (attrInfo.value != null) {
                    // validate the value
                    attrInfo.value = attrInfo.value.trim();
                    if (attrInfo.value.length() > 0) {
                        EventAttrTypeEnum type = attrInfo.type;
                        List<String> permissibleSplit = null;

                        if (type == EventAttrTypeEnum.SELECT_SINGLE
                            || type == EventAttrTypeEnum.SELECT_MULTIPLE) {
                            String permissible = sAttr.getPermissible();
                            if (permissible != null) {
                                permissibleSplit = Arrays.asList(permissible
                                    .split(";")); //$NON-NLS-1$
                            }
                        }

                        if (type == EventAttrTypeEnum.SELECT_SINGLE) {
                            if (!permissibleSplit.contains(attrInfo.value)) {
                                throw new ActionException(
                                    "value " + attrInfo.value //$NON-NLS-1$
                                        + "is invalid for label \"" + sAttr.getLabel() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        } else if (type == EventAttrTypeEnum.SELECT_MULTIPLE) {
                            for (String singleVal : attrInfo.value.split(";")) { //$NON-NLS-1$
                                if (!permissibleSplit.contains(singleVal)) {
                                    throw new ActionException(
                                        "value " + singleVal + " (" //$NON-NLS-1$ //$NON-NLS-2$
                                            + attrInfo.value
                                            + ") is invalid for label \"" + sAttr.getLabel() //$NON-NLS-1$
                                            + "\""); //$NON-NLS-1$
                                }
                            }
                        } else if (type == EventAttrTypeEnum.NUMBER) {
                            Double.parseDouble(attrInfo.value);
                        } else if (type == EventAttrTypeEnum.DATE_TIME) {
                            try {
                                DateFormatter.dateFormatter
                                    .parse(attrInfo.value);
                            } catch (ParseException e) {
                                throw new ActionException(e);
                            }
                        } else if (type == EventAttrTypeEnum.TEXT) {
                            // do nothing
                        } else {
                            throw new ActionException(
                                "type \"" + type + "\" not tested"); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                }

                EventAttr eventAttr;
                if (ceventAttrInfo == null) {
                    eventAttr = new EventAttr();
                    CollectionUtils.getCollection(cevent,
                        CollectionEventPeer.EVENT_ATTR_COLLECTION).add(
                        eventAttr);
                    eventAttr.setCollectionEvent(cevent);
                    eventAttr.setStudyEventAttr(sAttr);
                } else {
                    eventAttr = ceventAttrInfo.attr;
                }
                eventAttr.setValue(attrInfo.value);

                // FIXME need to remove attributes ? when they don't exist
                // anymore
                // in study maybe ? See previous code in wrapper ?
            }
    }

}
