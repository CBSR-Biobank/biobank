package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.study.StudyEventAttrInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetEventAttrInfoAction;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventCreatePermission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
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

public class CollectionEventSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private Integer ceventId;
    private Integer patientId;
    private Integer visitNumber;
    private Integer statusId;
    private String commentText;

    public static class SaveCEventSpecimenInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer id;
        public String inventoryId;
        public Date createdAt;
        public Integer statusId;
        public Integer specimenTypeId;
        public Integer centerId;
        public String commentText;
        public Double quantity;
    }

    public static class CEventAttrSaveInfo implements ActionResult {

        private static final long serialVersionUID = 1L;
        public Integer studyEventAttrId;
        public EventAttrTypeEnum type;
        public String value;

    }

    private Collection<SaveCEventSpecimenInfo> sourceSpecimenInfos;

    private List<CEventAttrSaveInfo> ceAttrList;

    public CollectionEventSaveAction(Integer ceventId, Integer patientId,
        Integer visitNumber, Integer statusId, String commentText,
        Collection<SaveCEventSpecimenInfo> sourceSpecs,
        List<CEventAttrSaveInfo> ceAttrList) {
        this.ceventId = ceventId;
        this.patientId = patientId;
        this.visitNumber = visitNumber;
        this.statusId = statusId;
        this.commentText = commentText;
        this.sourceSpecimenInfos = sourceSpecs;
        this.ceAttrList = ceAttrList;
    }

    public void setCeventId(Integer ceventId) {
        this.ceventId = ceventId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public void setVisitNumber(Integer visitNumber) {
        this.visitNumber = visitNumber;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setSourceSpecimenInfos(
        Collection<SaveCEventSpecimenInfo> sourceSpecimenInfos) {
        this.sourceSpecimenInfos = sourceSpecimenInfos;
    }

    public void setCeAttrList(List<CEventAttrSaveInfo> ceAttrList) {
        this.ceAttrList = ceAttrList;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Permission permission;
        if (ceventId == null) {
            permission = new CollectionEventCreatePermission(patientId);
        } else {
            permission = new CollectionEventUpdatePermission(ceventId);
        }
        return permission.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {

        check(context);

        CollectionEvent ceventToSave;
        if (ceventId == null) {
            ceventToSave = new CollectionEvent();
        } else {
            ceventToSave = context.load(CollectionEvent.class, ceventId);
        }

        // FIXME Version check?

        Patient patient = context.load(Patient.class, patientId);
        ceventToSave.setPatient(patient);
        ceventToSave.setVisitNumber(visitNumber);
        ceventToSave.setActivityStatus(context.load(ActivityStatus.class,
            statusId));

        saveComment(context, ceventToSave);
        setSourceSpecimens(context, ceventToSave);
        setEventAttrs(context, patient.getStudy(), ceventToSave);

        context.getSession().saveOrUpdate(ceventToSave);

        return new IdResult(ceventToSave.getId());
    }

    private void check(ActionContext context) {
        // Check that the visit number is unique for the patient
        List<ValueProperty<CollectionEvent>> propUple =
            new ArrayList<ValueProperty<CollectionEvent>>();
        propUple.add(new ValueProperty<CollectionEvent>(
            CollectionEventPeer.PATIENT.to(PatientPeer.ID), patientId));
        propUple.add(new ValueProperty<CollectionEvent>(
            CollectionEventPeer.VISIT_NUMBER, visitNumber));
        new UniquePreCheck<CollectionEvent>(CollectionEvent.class, ceventId,
            propUple).run(context);
    }

    private void setSourceSpecimens(ActionContext context,
        CollectionEvent ceventToSave) {
        Set<Specimen> newSsCollection = new HashSet<Specimen>();

        Set<Specimen> newAllSpecCollection = new HashSet<Specimen>();
        newAllSpecCollection.addAll(ceventToSave.getAllSpecimenCollection());

        Collection<Specimen> originalSpecimens =
            ceventToSave.getOriginalSpecimenCollection();

        if (sourceSpecimenInfos != null) {
            OriginInfo oi = null;

            for (SaveCEventSpecimenInfo specInfo : sourceSpecimenInfos) {
                Specimen specimen;
                if (specInfo.id == null) {
                    if (oi == null) {
                        oi = new OriginInfo();
                        oi.setCenter(context.load(Center.class,
                            specInfo.centerId));
                        context.getSession().saveOrUpdate(oi);
                    }
                    specimen = new Specimen();
                    specimen.setCurrentCenter(oi.getCenter());
                    specimen.setOriginInfo(oi);
                    specimen.setTopSpecimen(specimen);
                    newAllSpecCollection.add(specimen);
                } else {
                    specimen = context.load(Specimen.class, specInfo.id);

                    if (!newAllSpecCollection.contains(specimen)) {
                        throw new ActionCheckException(
                            "specimen not found in collection");
                    }
                }
                specimen.setActivityStatus(context.load(
                    ActivityStatus.class, specInfo.statusId));
                specimen.setCollectionEvent(ceventToSave);
                // cascade will save-update the specimens from this list:
                specimen.setOriginalCollectionEvent(ceventToSave);
                saveSpecimenComment(context, specimen, specInfo.commentText);
                specimen.setCreatedAt(specInfo.createdAt);
                specimen.setInventoryId(specInfo.inventoryId);
                specimen.setQuantity(specInfo.quantity);
                specimen.setSpecimenType(context.load(SpecimenType.class,
                    specInfo.specimenTypeId));
                newSsCollection.add(specimen);
            }
        }

        SetDifference<Specimen> origSpecDiff = new SetDifference<Specimen>(
            originalSpecimens, newSsCollection);
        ceventToSave.setOriginalSpecimenCollection(newSsCollection);
        newAllSpecCollection.removeAll(origSpecDiff.getRemoveSet());
        ceventToSave.setAllSpecimenCollection(newAllSpecCollection);
        for (Specimen srcSpc : origSpecDiff.getRemoveSet()) {
            context.getSession().delete(srcSpc);
        }
    }

    public void setEventAttrs(ActionContext context, Study study,
        CollectionEvent cevent) throws ActionException {
        Map<Integer, StudyEventAttrInfo> studyEventList =
            new StudyGetEventAttrInfoAction(
                study.getId()).run(context).getMap();

        Map<Integer, EventAttrInfo> ceventAttrList =
            new CollectionEventGetEventAttrInfoAction(
                ceventId).run(context).getMap();
        if (ceAttrList != null)
            for (CEventAttrSaveInfo attrInfo : ceAttrList) {
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
                        "Attribute for \"" + sAttr.getGlobalEventAttr().getLabel() //$NON-NLS-1$
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
                                        + "is invalid for label \"" + sAttr.getGlobalEventAttr().getLabel() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        } else if (type == EventAttrTypeEnum.SELECT_MULTIPLE) {
                            for (String singleVal : attrInfo.value.split(";")) { //$NON-NLS-1$
                                if (!permissibleSplit.contains(singleVal)) {
                                    throw new ActionException(
                                        "value " + singleVal + " (" //$NON-NLS-1$ //$NON-NLS-2$
                                            + attrInfo.value
                                            + ") is invalid for label \"" + sAttr.getGlobalEventAttr().getLabel() //$NON-NLS-1$
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
                    cevent.getEventAttrCollection().add(eventAttr);
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

    private void saveComment(ActionContext context, CollectionEvent ceventToSave) {
        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            ceventToSave.getCommentCollection().add(comment);
        }
    }

    private void saveSpecimenComment(ActionContext context,
        Specimen specimenToSave, String commentText) {
        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            specimenToSave.getCommentCollection().add(comment);
        }
    }

}
