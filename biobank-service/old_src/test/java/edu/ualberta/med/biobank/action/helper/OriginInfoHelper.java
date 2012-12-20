package edu.ualberta.med.biobank.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.action.IActionExecutor;

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
