package edu.ualberta.med.biobank.test.action.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.CEventAttrSaveInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.EventAttrInfo;
import edu.ualberta.med.biobank.common.action.info.CommentInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeSaveAction;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.IActionExecutor;

public class CollectionEventHelper extends Helper {

    /**
     * Creates a collection event specimen info object with random information.
     */
    public static SaveCEventSpecimenInfo createSaveCEventSpecimenInfoRandom(
        Integer specimenTypeId, Integer userId, Integer centerId) {
        SaveCEventSpecimenInfo info = new SaveCEventSpecimenInfo();
        if (userId != null)
            info.commentText = Utils.getRandomString(20, 30);
        info.inventoryId = Utils.getRandomString(8, 12);
        info.quantity = r.nextDouble();
        info.specimenTypeId = specimenTypeId;
        info.statusId = 1;
        info.createdAt = Utils.getRandomDate();
        info.centerId = centerId;
        return info;
    }

    /**
     * @param nber number of specimen info to create
     * @param typeId type of the specimens
     * @param userId user id is used for the comments fields, if none is
     *            provided, then no comments are added
     */
    public static Map<String, SaveCEventSpecimenInfo> createSaveCEventSpecimenInfoRandomList(
        int nber, Integer typeId, Integer userId, Integer centerId) {
        Map<String, SaveCEventSpecimenInfo> specs =
            new HashMap<String, CollectionEventSaveAction.SaveCEventSpecimenInfo>();
        for (int i = 0; i < nber; i++) {
            SaveCEventSpecimenInfo info = createSaveCEventSpecimenInfoRandom(
                typeId, userId, centerId);
            specs.put(info.inventoryId, info);
        }
        return specs;
    }

    public static CEventAttrSaveInfo createSaveCEventAttrInfo(
        Integer studyEventAttrId, EventAttrTypeEnum type, String value) {
        CEventAttrSaveInfo info = new CEventAttrSaveInfo();
        info.studyEventAttrId = studyEventAttrId;
        info.type = type;
        info.value = value;
        return info;
    }

    public static Integer createCEventWithSourceSpecimens(
        IActionExecutor actionExecutor, Integer patientId, Integer siteId)
        throws Exception {
        // add specimen type
        String name = "createCEventWithSourceSpecimens" + r.nextInt();
        final Integer typeId =
            actionExecutor.exec(new SpecimenTypeSaveAction(name, name)).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId, null, siteId);

        // Save a new cevent with specimens
        return actionExecutor.exec(new CollectionEventSaveAction(
            null, patientId, r.nextInt(20), 1, null,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null))
            .getId();

    }

    public static Set<CommentInfo> getCommentInfos(Collection<Comment> comments) {
        HashSet<CommentInfo> commentInfos = new HashSet<CommentInfo>();
        for (Comment comment : comments) {
            CommentInfo commentInfo = new CommentInfo(comment.getMessage(),
                comment.getCreatedAt(), comment.getUser().getId());
            commentInfo.id = comment.getId();
            commentInfos.add(commentInfo);
        }
        return commentInfos;
    }

    public static CollectionEventSaveAction getSaveAction(CEventInfo ceventInfo) {
        HashSet<SaveCEventSpecimenInfo> sourceSpecimens =
            new HashSet<SaveCEventSpecimenInfo>();
        List<CEventAttrSaveInfo> ceAttrList =
            new ArrayList<CEventAttrSaveInfo>();

        for (SpecimenInfo specimenInfo : ceventInfo.sourceSpecimenInfos) {
            SaveCEventSpecimenInfo saveCEventSpecimenInfo =
                new SaveCEventSpecimenInfo();
            saveCEventSpecimenInfo.id = specimenInfo.specimen.getId();
            saveCEventSpecimenInfo.inventoryId =
                specimenInfo.specimen.getInventoryId();
            saveCEventSpecimenInfo.createdAt =
                specimenInfo.specimen.getCreatedAt();
            saveCEventSpecimenInfo.statusId =
                specimenInfo.specimen.getActivityStatus().getId();
            saveCEventSpecimenInfo.specimenTypeId =
                specimenInfo.specimen.getSpecimenType().getId();
            saveCEventSpecimenInfo.centerId =
                specimenInfo.specimen.getOriginInfo().getCenter().getId();
            sourceSpecimens.add(saveCEventSpecimenInfo);
        }

        for (EventAttrInfo eventAttrInfo : ceventInfo.eventAttrs.values()) {
            CEventAttrSaveInfo cEventAttrSaveInfo = new CEventAttrSaveInfo();
            cEventAttrSaveInfo.studyEventAttrId =
                eventAttrInfo.attr.getStudyEventAttr().getId();
            cEventAttrSaveInfo.type = eventAttrInfo.type;
            cEventAttrSaveInfo.value = eventAttrInfo.attr.getValue();
            ceAttrList.add(cEventAttrSaveInfo);
        }

        CollectionEventSaveAction saveAction =
            new CollectionEventSaveAction(ceventInfo.cevent.getId(),
                ceventInfo.cevent.getPatient().getId(),
                ceventInfo.cevent.getVisitNumber(),
                ceventInfo.cevent.getActivityStatus().getId(), null,
                sourceSpecimens, ceAttrList);

        return saveAction;
    }
}
