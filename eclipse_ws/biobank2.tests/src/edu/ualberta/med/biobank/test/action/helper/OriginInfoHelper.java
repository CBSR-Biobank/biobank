package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.action.IActionExecutor;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.test.Utils;

public class OriginInfoHelper extends Helper {

    public static OriginInfoSaveInfo createSaveOriginInfoSpecimenInfoRandom(
        IActionExecutor actionExecutor, Integer patientId, Integer siteId,
        Integer centerId) throws Exception {
        Set<Integer> ids = new HashSet<Integer>();
        Integer id = CollectionEventHelper.createCEventWithSourceSpecimens(
                actionExecutor, patientId, siteId);

        CEventInfo ceventInfo =
            actionExecutor.exec(new CollectionEventGetInfoAction(id));

        for (SpecimenInfo specInfo : ceventInfo.sourceSpecimenInfos) {
            ids.add(specInfo.specimen.getId());
        }

        return new OriginInfoSaveInfo(null, siteId, centerId,
            Utils.getRandomString(10),
            ids, new HashSet<Integer>());
    }
}
