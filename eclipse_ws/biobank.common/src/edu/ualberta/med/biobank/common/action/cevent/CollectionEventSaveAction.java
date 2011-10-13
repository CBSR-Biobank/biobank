package edu.ualberta.med.biobank.common.action.cevent;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.action.CollectionUtils;
import edu.ualberta.med.biobank.common.action.DiffUtils;
import edu.ualberta.med.biobank.common.action.activity.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.study.GetStudyEventAttrInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyEventAttrInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
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
    private String comments;

    public static class SaveCEventSpecimenInfo implements Serializable,
        NotAProxy {
        private static final long serialVersionUID = 1L;

        public Integer id;
        public String inventoryId;
        public Date timeDrawn;
        public Integer statusId;
        public Integer specimenTypeId;
        public String comment;
        public Double quantity;
    }

    public static class SaveCEventAttrInfo implements Serializable, NotAProxy {

        private static final long serialVersionUID = 1L;
        public Integer studyEventAttrId;
        public EventAttrTypeEnum type;
        public String value;

    }

    private List<SaveCEventSpecimenInfo> sourceSpecimens;

    private Integer centerId;

    private List<SaveCEventAttrInfo> ceAttrList;

    public CollectionEventSaveAction(Integer ceventId, Integer patientId,
        Integer visitNumber, Integer statusId, String comments,
        Integer centerId, List<SaveCEventSpecimenInfo> sourceSpecs,
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
    public Integer doAction(Session session) throws ActionException {
        CollectionEvent ceventToSave;
        if (ceventId == null) {
            ceventToSave = new CollectionEvent();
        } else {
            ceventToSave = (CollectionEvent) session.get(CollectionEvent.class,
                ceventId);
        }

        // FIXME Version check?
        // FIXME checks?
        // FIXME permission ?

        Patient patient = (Patient) session.get(Patient.class, patientId);
        ceventToSave.setPatient(patient);
        ceventToSave.setVisitNumber(visitNumber);
        ceventToSave.setActivityStatus((ActivityStatus) session.get(
            ActivityStatus.class, statusId));
        ceventToSave.setComment(comments);

        setSourceSpecimens(session, ceventToSave);

        setEventAttrs(session, patient.getStudy(), ceventToSave);

        session.saveOrUpdate(ceventToSave);

        return ceventToSave.getId();
    }

    private void setSourceSpecimens(Session session,
        CollectionEvent ceventToSave) {
        OriginInfo oi = new OriginInfo();
        oi.setCenter((Center) session.get(Center.class, centerId));
        session.saveOrUpdate(oi);
        Set<Specimen> newSourceSpecList = new HashSet<Specimen>();
        for (SaveCEventSpecimenInfo specInfo : sourceSpecimens) {
            Specimen specimen;
            if (specInfo.id == null) {
                specimen = new Specimen();
                specimen.setCurrentCenter(oi.getCenter());
                specimen.setOriginInfo(oi);
            } else {
                specimen = (Specimen) session.get(Specimen.class, specInfo.id);
            }
            specimen.setActivityStatus((ActivityStatus) session.get(
                ActivityStatus.class, specInfo.statusId));
            specimen.setCollectionEvent(ceventToSave);
            // cascade will save-update the specimens from this list:
            CollectionUtils.getCollection(ceventToSave,
                CollectionEventPeer.ALL_SPECIMEN_COLLECTION).add(specimen);
            specimen.setOriginalCollectionEvent(ceventToSave);
            CollectionUtils.getCollection(ceventToSave,
                CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION).add(specimen);
            specimen.setComment(specInfo.comment);
            specimen.setCreatedAt(specInfo.timeDrawn);
            specimen.setInventoryId(specInfo.inventoryId);
            specimen.setQuantity(specInfo.quantity);
            specimen.setSpecimenType((SpecimenType) session.get(
                SpecimenType.class, specInfo.specimenTypeId));
            newSourceSpecList.add(specimen);
        }
        Collection<Specimen> removedSpecimens = DiffUtils.getRemoved(
            ceventToSave.getOriginalSpecimenCollection(), newSourceSpecList);
        // need to remove from collections. the delete-orphan cascade on
        // allspecimencollection will delete orphans
        CollectionUtils.getCollection(ceventToSave,
            CollectionEventPeer.ALL_SPECIMEN_COLLECTION).removeAll(
            removedSpecimens);
        CollectionUtils.getCollection(ceventToSave,
            CollectionEventPeer.ORIGINAL_SPECIMEN_COLLECTION).removeAll(
            removedSpecimens);
    }

    public void setEventAttrs(Session session, Study study,
        CollectionEvent cevent) throws ActionException {
        Map<Integer, StudyEventAttrInfo> studyEventList = new GetStudyEventAttrInfoAction(
            study.getId()).doAction(session);

        Map<Integer, EventAttrInfo> ceventAttrList = new GetEventAttrInfoAction(
            ceventId).doAction(session);

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
                throw new ActionException("Attribute for \"" + sAttr.getLabel() //$NON-NLS-1$
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
                            DateFormatter.dateFormatter.parse(attrInfo.value);
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
                    CollectionEventPeer.EVENT_ATTR_COLLECTION).add(eventAttr);
                eventAttr.setCollectionEvent(cevent);
                eventAttr.setStudyEventAttr(sAttr);
            } else {
                eventAttr = ceventAttrInfo.attr;
            }
            eventAttr.setValue(attrInfo.value);

            // FIXME need to remove attributes ? when they don't exist anymore
            // in study maybe ? See previous code in wrapper ?
        }
    }

}
