package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventAttrInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.test.Utils;

public class CollectionEventHelper extends Helper {

    public static SaveCEventSpecimenInfo createSaveCEventSpecimenInfoRandom(
        Integer specimenTypeId) {
        SaveCEventSpecimenInfo info = new SaveCEventSpecimenInfo();
        info.comment = Utils.getRandomString(8, 50);
        info.inventoryId = Utils.getRandomString(8, 12);
        info.quantity = r.nextDouble();
        info.specimenTypeId = specimenTypeId;
        info.statusId = 1;
        info.timeDrawn = Utils.getRandomDate();
        return info;
    }

    public static Map<String, SaveCEventSpecimenInfo> createSaveCEventSpecimenInfoRandomList(
        int nber, Integer typeId) {
        Map<String, SaveCEventSpecimenInfo> specs = new HashMap<String, CollectionEventSaveAction.SaveCEventSpecimenInfo>();
        for (int i = 0; i < nber; i++) {
            SaveCEventSpecimenInfo info = createSaveCEventSpecimenInfoRandom(typeId);
            specs.put(info.inventoryId, info);
        }
        return specs;
    }

    public static SaveCEventAttrInfo createSaveCEventAttrInfo(
        Integer studyEventAttrId, EventAttrTypeEnum type, String value) {
        SaveCEventAttrInfo info = new SaveCEventAttrInfo();
        info.studyEventAttrId = studyEventAttrId;
        info.type = type;
        info.value = value;
        return info;
    }
}
