package edu.ualberta.med.biobank.test.action.helper;

import java.math.BigDecimal;
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
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.IActionExecutor;

public class CollectionEventHelper extends Helper {

    /**
     * Creates a collection event specimen info object with random information.
     */
    public static SaveCEventSpecimenInfo createSaveCEventSpecimenInfoRandom(
        Integer specimenTypeId, Integer userId) {
        List<String> comments = new ArrayList<String>();
        if (userId != null) {
            comments.add(Utils.getRandomString(20, 30));
        }
        return new SaveCEventSpecimenInfo(null, Utils.getRandomString(8, 12),
            Utils.getRandomDate(), ActivityStatus.ACTIVE, specimenTypeId, comments,
            new BigDecimal(r.nextInt(10) + 1));
    }

    /**
     * @param nber number of specimen info to create
     * @param typeId type of the specimens
     * @param userId user id is used for the comments fields, if none is
     *            provided, then no comments are added
     */
    public static Map<String, SaveCEventSpecimenInfo> createSaveCEventSpecimenInfoRandomList(
        int nber, Integer typeId, Integer userId) {
        Map<String, SaveCEventSpecimenInfo> specs =
            new HashMap<String, CollectionEventSaveAction.SaveCEventSpecimenInfo>();
        for (int i = 0; i < nber; i++) {
            SaveCEventSpecimenInfo info = createSaveCEventSpecimenInfoRandom(
                typeId, userId);
            specs.put(info.inventoryId, info);
        }
        return specs;
    }

    public static Integer createCEventWithSourceSpecimens(
        IActionExecutor actionExecutor, Integer patientId, Center center)
            throws Exception {
        // add specimen type
        String name = "createCEventWithSourceSpecimens" + r.nextInt();
        final Integer typeId =
            actionExecutor.exec(new SpecimenTypeSaveAction(name, name)).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId, null);

        // Save a new cevent with specimens
        return actionExecutor.exec(new CollectionEventSaveAction(
            null, patientId, r.nextInt(20) + 1, ActivityStatus.ACTIVE, null,
            new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null, center))
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

    public static CollectionEventSaveAction getSaveAction(CEventInfo ceventInfo,
        Center center) {
        HashSet<SaveCEventSpecimenInfo> sourceSpecimens =
            new HashSet<SaveCEventSpecimenInfo>();
        List<CEventAttrSaveInfo> ceAttrList =
            new ArrayList<CEventAttrSaveInfo>();

        for (SpecimenInfo specimenInfo : ceventInfo.sourceSpecimenInfos) {
            sourceSpecimens.add(new SaveCEventSpecimenInfo(specimenInfo.specimen.getId(),
                specimenInfo.specimen.getInventoryId(), specimenInfo.specimen.getCreatedAt(),
                specimenInfo.specimen.getActivityStatus(),
                specimenInfo.specimen.getSpecimenType().getId(), null, new BigDecimal(0)));
        }

        for (EventAttrInfo eventAttrInfo : ceventInfo.eventAttrs.values()) {
            ceAttrList.add(new CEventAttrSaveInfo(eventAttrInfo.attr.getStudyEventAttr().getId(),
                eventAttrInfo.type, eventAttrInfo.attr.getValue()));
        }

        CollectionEventSaveAction saveAction =
            new CollectionEventSaveAction(ceventInfo.cevent.getId(),
                ceventInfo.cevent.getPatient().getId(),
                ceventInfo.cevent.getVisitNumber(),
                ceventInfo.cevent.getActivityStatus(), null,
                sourceSpecimens, ceAttrList, center);

        return saveAction;
    }
}
