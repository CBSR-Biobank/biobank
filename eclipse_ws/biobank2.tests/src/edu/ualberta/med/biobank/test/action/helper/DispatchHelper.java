package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.IActionExecutor;

public class DispatchHelper extends Helper {

    public static Set<DispatchSpecimenInfo> createSaveDispatchSpecimenInfoRandom(
        IActionExecutor actionExecutor, Integer patientId,
        Integer centerId) throws Exception {
        Set<DispatchSpecimenInfo> infos = new HashSet<DispatchSpecimenInfo>();
        Integer id = null;
        id = CollectionEventHelper.createCEventWithSourceSpecimens(
            actionExecutor, patientId, centerId);

        CEventInfo ceventInfo =
            actionExecutor.exec(new CollectionEventGetInfoAction(id));

        for (SpecimenInfo specInfo : ceventInfo.sourceSpecimenInfos) {
            infos.add(new DispatchSpecimenInfo(null, specInfo.specimen.getId(),
                DispatchSpecimenState.NONE.getId()));
        }

        return infos;
    }

    public static DispatchSaveInfo createSaveDispatchInfoRandom(Integer siteId,
        Integer centerId,
        Integer state, String comment) {
        return new DispatchSaveInfo(null, siteId, centerId, state, comment);
    }

    public static Integer createDispatch(IActionExecutor actionExecutor,
        Integer srcCenterId, Integer dstCenterId, Integer patientId)
        throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(dstCenterId,
                srcCenterId, DispatchState.CREATION.getId(),
                Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(actionExecutor,
                patientId, srcCenterId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(actionExecutor);
        return actionExecutor.exec(new DispatchSaveAction(d, specs, shipsave))
            .getId();
    }
}
