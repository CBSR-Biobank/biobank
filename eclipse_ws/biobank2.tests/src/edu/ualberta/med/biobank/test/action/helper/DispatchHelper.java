package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.test.Utils;

public class DispatchHelper extends Helper {

    public static Set<DispatchSpecimenInfo> createSaveDispatchSpecimenInfoRandom(
        BiobankApplicationService appService, Integer patientId,
        Integer centerId) throws Exception {
        Set<DispatchSpecimenInfo> infos = new HashSet<DispatchSpecimenInfo>();
        Integer id = CollectionEventHelper.createCEventWithSourceSpecimens(
            appService, patientId, centerId);
        CollectionEvent added = new CollectionEvent();
        added.setId(id);
        List<CollectionEvent> rs =
            appService.search(CollectionEvent.class, added);
        added = rs.get(0);
        for (Specimen spec : added.getAllSpecimenCollection()) {
            infos.add(new DispatchSpecimenInfo(null, spec.getId(),
                DispatchSpecimenState.NONE.getId()));
        }

        return infos;
    }

    public static DispatchSaveInfo createSaveDispatchInfoRandom(
        BiobankApplicationService appService,
        Integer siteId, Integer centerId, Integer state, String comment) {
        return new DispatchSaveInfo(null, siteId, centerId, state, comment);
    }

    public static Integer createDispatch(BiobankApplicationService appService,
        Integer srcCenterId, Integer dstCenterId,
        Integer patientId) throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(appService,
                dstCenterId, srcCenterId, DispatchState.CREATION.getId(),
                Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(appService,
                patientId, srcCenterId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(appService);
        return appService.doAction(new DispatchSaveAction(d, specs, shipsave))
            .getId();
    }
}
