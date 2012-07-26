package edu.ualberta.med.biobank.action.collectionEvent;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.comment.CommentUtil;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.study.StudyEventAttrInfo;
import edu.ualberta.med.biobank.action.study.StudyGetEventAttrInfoAction;
import edu.ualberta.med.biobank.action.util.SetDifference;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.type.ActivityStatus;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.collectionEvent.CollectionEventCreatePermission;
import edu.ualberta.med.biobank.permission.collectionEvent.CollectionEventUpdatePermission;

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

        public Integer id;
        public String inventoryId;
        public Date createdAt;
        public ActivityStatus activityStatus;
        public Integer specimenTypeId;
        public Integer centerId;
        public List<String> comments = new ArrayList<String>();
        public BigDecimal quantity;
    }

    public static class CEventAttrSaveInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer studyEventAttrId;
        public EventAttrTypeEnum type;
        public String value;
    }

    private Integer ceventId;
    private Integer patientId;
    private Integer visitNumber;
    private ActivityStatus activityStatus;
    private String commentText;
    private Collection<SaveCEventSpecimenInfo> sourceSpecimenInfos;
    private List<CEventAttrSaveInfo> ceAttrList =
        new ArrayList<CEventAttrSaveInfo>(0);

    public CollectionEventSaveAction(Integer ceventId, Integer patientId,
        Integer visitNumber, ActivityStatus activityStatus, String commentText,
        Collection<SaveCEventSpecimenInfo> sourceSpecs,
        List<CEventAttrSaveInfo> ceAttrList) {
        this.ceventId = ceventId;
        this.patientId = patientId;
        this.visitNumber = visitNumber;
        this.activityStatus = activityStatus;
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

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
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

    private void setSourceSpecimens(ActionContext context,
        CollectionEvent ceventToSave) {

        Set<Specimen> newOriginalSpecimens = new HashSet<Specimen>();
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
                } else {
                    specimen = context.load(Specimen.class, specInfo.id);
                }

                specimen.setActivityStatus(specInfo.activityStatus);
                specimen.setCollectionEvent(ceventToSave);
                // cascade will save-update the specimens from this list:
                specimen.setOriginalCollectionEvent(ceventToSave);
                saveSpecimenComments(context, specimen, specInfo.comments);
                specimen.setTimeCreated(specInfo.createdAt);
                specimen.setInventoryId(specInfo.inventoryId);
                specimen.setQuantity(specInfo.quantity);
                specimen.setSpecimenType(context.load(SpecimenType.class,
                    specInfo.specimenTypeId));

                newOriginalSpecimens.add(specimen);
            }
        }

        Set<Specimen> oldOriginalSpecimens =
            ceventToSave.getOriginalSpecimens();

        SetDifference<Specimen> originalSpecimensDiff =
            new SetDifference<Specimen>(oldOriginalSpecimens,
                newOriginalSpecimens);

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
            specimen.getCollectionEvent().getOriginalSpecimens()
                .remove(specimen);

            context.getSession().delete(specimen);
        } else {
            throw new LocalizedException(
                bundle
                    .tr(
                        "Specimen {0} has children and cannot be deleted. Instead, move it to a different collection event.")
                    .format(
                        specimen.getInventoryId()));
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

        Map<Integer, EventAttrInfo> ceventAttrList =
            new CollectionEventGetEventAttrInfoAction(
                ceventId).run(context).getMap();

        Map<Integer, StudyEventAttrInfo> studyEventList =
            new StudyGetEventAttrInfoAction(
                study.getId()).run(context).getMap();

        Set<EventAttr> eventAttrs = new HashSet<EventAttr>(ceAttrList.size());

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
                    throw new LocalizedException(
                        STUDY_EVENT_ATTR_MISSING_ERRMSG
                            .format(attrInfo.studyEventAttrId));
                }
            }

            if (ActivityStatus.ACTIVE != sAttr.getActivityStatus()) {
                String label = sAttr.getGlobalEventAttr().getLabel();
                throw new LocalizedException(LOCKED_LABEL_ERRMSG.format(label));
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
                            String label =
                                sAttr.getGlobalEventAttr().getLabel();
                            throw new LocalizedException(
                                INVALID_STUDY_EVENT_ATTR_SINGLE_VALUE_ERRMSG
                                    .format(attrInfo.value, label));
                        }
                    } else if (type == EventAttrTypeEnum.SELECT_MULTIPLE) {
                        for (String singleVal : attrInfo.value.split(";")) { //$NON-NLS-1$
                            if (!permissibleSplit.contains(singleVal)) {
                                String label =
                                    sAttr.getGlobalEventAttr().getLabel();
                                throw new LocalizedException(
                                    INVALID_STUDY_EVENT_ATTR_MULTIPLE_VALUE_ERRMSG
                                        .format(singleVal, attrInfo.value,
                                            label));
                            }
                        }
                    } else if (type == EventAttrTypeEnum.NUMBER) {
                        Double.parseDouble(attrInfo.value);
                    } else if (type == EventAttrTypeEnum.DATE_TIME) {
                        try {
                            DateFormatter.dateFormatter
                                .parse(attrInfo.value);
                        } catch (ParseException e) {
                            throw new LocalizedException(
                                CANNOT_PARSE_DATE_ERRMSG
                                    .format(attrInfo.value));
                        }
                    } else if (type == EventAttrTypeEnum.TEXT) {
                        // do nothing
                    } else {
                        throw new LocalizedException(
                            UNKNOWN_EVENT_ATTR_TYPE_ERRMSG
                                .format(type.getName()));
                    }
                }
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
