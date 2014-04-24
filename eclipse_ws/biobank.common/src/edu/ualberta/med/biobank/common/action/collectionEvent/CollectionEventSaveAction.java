package edu.ualberta.med.biobank.common.action.collectionEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.eventattr.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.action.eventattr.EventAttrUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenMicroplateConsistentAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenMicroplateConsistentAction.SpecimenMicroplateInfo;
import edu.ualberta.med.biobank.common.action.study.StudyEventAttrInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetEventAttrInfoAction;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventCreatePermission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
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

    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr ABSENT_SPECIMEN_ERRMSG =
        bundle.tr("Specimen \"{0}\" not found in collection.");

    @SuppressWarnings("nls")
    public static final Tr LOCKED_LABEL_ERRMSG =
        bundle.tr("Attribute for label \"{0}\" is locked, changes not" +
            " permitted.");

    @SuppressWarnings("nls")
    public static final Tr STUDY_EVENT_ATTR_MISSING_ERRMSG =
        bundle.tr("Cannot find Study Event Attribute with id \"{0}\".");

    @SuppressWarnings("nls")
    public static final Tr INVALID_STUDY_EVENT_ATTR_SINGLE_VALUE_ERRMSG =
        bundle.tr("Value \"{0}\" is invalid for label \"{2}\".");

    @SuppressWarnings("nls")
    public static final Tr INVALID_STUDY_EVENT_ATTR_MULTIPLE_VALUE_ERRMSG =
        bundle.tr("Value \"{0}\" (\"{1}\") is invalid for label \"{2}\".");

    @SuppressWarnings("nls")
    public static final Tr CANNOT_PARSE_DATE_ERRMSG =
        bundle.tr("Cannot parse date \"{0}\".");

    @SuppressWarnings("nls")
    public static final Tr UNKNOWN_EVENT_ATTR_TYPE_ERRMSG =
        bundle.tr("Unknown Event Attribute Type \"{0}\".");

    public static class SaveCEventSpecimenInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public final Integer id;
        public final String inventoryId;
        public final Date createdAt;
        public final ActivityStatus activityStatus;
        public final Integer specimenTypeId;
        public final List<String> comments;
        public final BigDecimal quantity;

        public SaveCEventSpecimenInfo(Integer id, String inventoryId,
            Date createdAt, ActivityStatus activityStatus, Integer specimenTypeId,
            List<String> comments, BigDecimal quantity) {
            this.id = id;
            this.inventoryId = inventoryId;
            this.createdAt = createdAt;
            this.activityStatus = activityStatus;
            this.specimenTypeId = specimenTypeId;
            if (comments == null) {
                this.comments = new ArrayList<String>();
            } else {
                this.comments = comments;
            }
            this.quantity = quantity;
        }

        // copy but with a different id
        public SaveCEventSpecimenInfo(SaveCEventSpecimenInfo that, Integer id) {
            this(id, that.inventoryId, that.createdAt, that.activityStatus, that.specimenTypeId,
                that.comments, that.quantity);
        }
    }

    public static class CEventAttrSaveInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public final Integer studyEventAttrId;
        public final EventAttrTypeEnum type;
        public final String value;

        public CEventAttrSaveInfo(Integer studyEventAttrId, EventAttrTypeEnum type, String value) {
            this.studyEventAttrId = studyEventAttrId;
            this.type = type;
            this.value = value;
        }
    }

    private final Integer ceventId;
    private final Integer patientId;
    private final Integer visitNumber;
    private final ActivityStatus activityStatus;
    private final Integer centerId;

    private String commentText;
    private Set<SaveCEventSpecimenInfo> sourceSpecimenInfos =
        new HashSet<SaveCEventSpecimenInfo>(0);
    private List<CEventAttrSaveInfo> ceAttrList = new ArrayList<CEventAttrSaveInfo>(0);

    public CollectionEventSaveAction(Integer ceventId, Integer patientId, Integer visitNumber,
        ActivityStatus activityStatus, String commentText,
        Set<SaveCEventSpecimenInfo> sourceSpecs, List<CEventAttrSaveInfo> ceAttrList,
        Center currentWorkingCenter) {
        this.ceventId = ceventId;
        this.patientId = patientId;
        this.visitNumber = visitNumber;
        this.activityStatus = activityStatus;
        this.commentText = commentText;
        this.ceAttrList = ceAttrList;
        this.centerId = currentWorkingCenter.getId();

        if (sourceSpecs != null) {
            this.sourceSpecimenInfos = sourceSpecs;
        }
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setSourceSpecimenInfos(
        Set<SaveCEventSpecimenInfo> sourceSpecimenInfos) {
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
        List<SpecimenMicroplateInfo> specimenMicroplateInfos = new ArrayList<SpecimenMicroplateInfo>();
        for (SaveCEventSpecimenInfo si : sourceSpecimenInfos) {
            SpecimenMicroplateInfo smi = new SpecimenMicroplateInfo();
            smi.inventoryId = si.inventoryId;
            smi.containerId = null;
            smi.position = null;
            specimenMicroplateInfos.add(smi);
        }
        new SpecimenMicroplateConsistentAction(
            centerId, true, specimenMicroplateInfos).run(context);

        CollectionEvent ceventToSave;
        if (ceventId == null) {
            ceventToSave = new CollectionEvent();
        } else {
            ceventToSave = context.load(CollectionEvent.class, ceventId);
        }

        Patient patient = context.load(Patient.class, patientId);
        ceventToSave.setPatient(patient);
        ceventToSave.setVisitNumber(visitNumber);
        ceventToSave.setActivityStatus(activityStatus);

        saveComment(context, ceventToSave);
        setSourceSpecimens(context, ceventToSave);
        setEventAttrs(context, patient.getStudy(), ceventToSave);

        context.getSession().saveOrUpdate(ceventToSave);

        return new IdResult(ceventToSave.getId());
    }

    private void setSourceSpecimens(ActionContext context, CollectionEvent ceventToSave) {
        Set<Specimen> newOriginalSpecimens = new HashSet<Specimen>();
        if (sourceSpecimenInfos != null) {
            OriginInfo oi = null;

            for (SaveCEventSpecimenInfo specInfo : sourceSpecimenInfos) {
                Specimen specimen;
                if (specInfo.id == null) {
                    if (oi == null) {
                        oi = new OriginInfo();
                        oi.setCenter(context.load(Center.class, centerId));
                        context.getSession().saveOrUpdate(oi);
                    }

                    specimen = new Specimen();
                    specimen.setCurrentCenter(oi.getCenter());
                    specimen.setOriginInfo(oi);
                    specimen.setTopSpecimen(specimen);
                } else {
                    specimen = context.load(Specimen.class, specInfo.id);
                }

                specimen.setActivityStatus(specInfo.activityStatus);
                specimen.setCollectionEvent(ceventToSave);
                // cascade will save-update the specimens from this list:
                specimen.setOriginalCollectionEvent(ceventToSave);
                saveSpecimenComments(context, specimen, specInfo.comments);
                specimen.setCreatedAt(specInfo.createdAt);
                specimen.setInventoryId(specInfo.inventoryId);
                specimen.setQuantity(specInfo.quantity);
                specimen.setPlateErrors(StringUtil.EMPTY_STRING);
                specimen.setSampleErrors(StringUtil.EMPTY_STRING);
                specimen.setSpecimenType(context.load(SpecimenType.class, specInfo.specimenTypeId));
                specimen.setStudy(ceventToSave.getPatient().getStudy());

                newOriginalSpecimens.add(specimen);
            }
        }

        Set<Specimen> oldOriginalSpecimens = ceventToSave.getOriginalSpecimens();

        SetDifference<Specimen> originalSpecimensDiff =
            new SetDifference<Specimen>(oldOriginalSpecimens, newOriginalSpecimens);

        for (Specimen specimen : originalSpecimensDiff.getRemoveSet()) {
            removeOriginalSpecimen(context, specimen);
        }

        for (Specimen specimen : originalSpecimensDiff.getAddSet()) {
            updateCollectionEventOfChildren(context, specimen, ceventToSave);
        }
    }

    @SuppressWarnings("nls")
    private void removeOriginalSpecimen(ActionContext context, Specimen specimen) {
        if (specimen.getChildSpecimens().isEmpty()) {
            specimen.getCollectionEvent().getAllSpecimens().remove(specimen);
            specimen.getCollectionEvent().getOriginalSpecimens().remove(specimen);

            context.getSession().delete(specimen);
        } else {
            throw new LocalizedException(bundle.tr(
                "Specimen {0} has children and cannot be deleted. Instead, move it to a different collection event.")
                .format(specimen.getInventoryId()));
        }
    }

    private void updateCollectionEventOfChildren(ActionContext context,
        Specimen specimen, CollectionEvent collectionEvent) {
        specimen.setCollectionEvent(collectionEvent);
        collectionEvent.getAllSpecimens().add(specimen);

        for (Specimen child : specimen.getChildSpecimens()) {
            updateCollectionEventOfChildren(context, child, collectionEvent);
        }
    }

    public void setEventAttrs(ActionContext context, Study study,
        CollectionEvent cevent) throws ActionException {

        if ((ceAttrList == null) || ceAttrList.isEmpty()) return;

        Map<Integer, EventAttrInfo> ceventAttrList = new CollectionEventGetEventAttrInfoAction(
            ceventId).run(context).getMap();

        Map<Integer, StudyEventAttrInfo> studyEventList = new StudyGetEventAttrInfoAction(
            study.getId()).run(context).getMap();

        Set<EventAttr> eventAttrs = new HashSet<EventAttr>(ceAttrList.size());

        for (CEventAttrSaveInfo attrInfo : ceAttrList) {
            EventAttrInfo ceventAttrInfo = ceventAttrList.get(attrInfo.studyEventAttrId);
            StudyEventAttrInfo studyEventAttrInfo = studyEventList.get(attrInfo.studyEventAttrId);

            StudyEventAttr sAttr;

            if (ceventAttrInfo != null) {
                sAttr = ceventAttrInfo.attr.getStudyEventAttr();
            } else {
                sAttr = studyEventAttrInfo == null ? null : studyEventAttrInfo.attr;
                if (sAttr == null) {
                    throw new LocalizedException(
                        STUDY_EVENT_ATTR_MISSING_ERRMSG.format(attrInfo.studyEventAttrId));
                }
            }

            if (ActivityStatus.ACTIVE != sAttr.getActivityStatus()) {
                String label = sAttr.getGlobalEventAttr().getLabel();
                throw new LocalizedException(LOCKED_LABEL_ERRMSG.format(label));
            }

            if ((attrInfo.value != null) && !attrInfo.value.trim().isEmpty()) {
                // the following method throws an exeption if value is invalid
                EventAttrUtil.validateValue(attrInfo.type, sAttr.getGlobalEventAttr().getLabel(),
                    sAttr.getPermissible(), attrInfo.value);
            }

            EventAttr eventAttr;
            if (ceventAttrInfo == null) {
                eventAttr = new EventAttr();
                eventAttr.setCollectionEvent(cevent);
                eventAttr.setStudyEventAttr(sAttr);
            } else {
                eventAttr = ceventAttrInfo.attr;
            }
            eventAttr.setValue(attrInfo.value);
            eventAttrs.add(eventAttr);
        }

        // saving the collection this way will delete unused event attrs
        cevent.getEventAttrs().clear();
        cevent.getEventAttrs().addAll(eventAttrs);
    }

    private void saveComment(ActionContext context, CollectionEvent ceventToSave) {
        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            ceventToSave.getComments().add(comment);
        }
    }

    private void saveSpecimenComments(ActionContext context,
        Specimen specimenToSave, List<String> comments) {
        List<Comment> completedComments =
            CommentUtil.createCommentsFromList(context.getUser(), comments);
        for (Comment comment : completedComments) {
            context.getSession().save(comment);
            specimenToSave.getComments().add(comment);
        }
    }

}
