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
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.IActionExecutor;

public class DispatchHelper extends Helper {

    public static Set<DispatchSpecimenInfo> createSaveDispatchSpecimenInfoRandom(
        IActionExecutor actionExecutor, Integer patientId, Center center) throws Exception {
        Set<DispatchSpecimenInfo> infos = new HashSet<DispatchSpecimenInfo>();
        Integer id = null;
        id = CollectionEventHelper.createCEventWithSourceSpecimens(
            actionExecutor, patientId, center);

        CEventInfo ceventInfo =
            actionExecutor.exec(new CollectionEventGetInfoAction(id));

        for (SpecimenInfo specInfo : ceventInfo.sourceSpecimenInfos) {
            infos.add(new DispatchSpecimenInfo(null, specInfo.specimen.getId(),
                DispatchSpecimenState.NONE));
        }

        return infos;
    }

    public static DispatchSaveInfo createSaveDispatchInfoRandom(Center receivingCenter,
        Center sendingCenter, DispatchState state, String comment) {
        return new DispatchSaveInfo(null, receivingCenter, sendingCenter, state, comment);
    }

    public static Integer createDispatch(IActionExecutor actionExecutor,
        Center srcCenter, Center dstCenter, Integer patientId)
            throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(dstCenter,
                srcCenter, DispatchState.CREATION,
                Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(actionExecutor,
                patientId, srcCenter);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(actionExecutor);
        return actionExecutor.exec(new DispatchSaveAction(d, specs, shipsave))
            .getId();
    }
}
