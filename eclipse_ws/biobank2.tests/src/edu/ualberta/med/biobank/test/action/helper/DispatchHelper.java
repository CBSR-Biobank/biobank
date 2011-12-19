package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class DispatchHelper extends Helper {

    public static Set<DispatchSpecimenInfo> createSaveDispatchSpecimenInfoRandom(
        BiobankApplicationService appService, Integer patientId,
        Integer centerId) throws Exception {
        Set<DispatchSpecimenInfo> infos = new HashSet<DispatchSpecimenInfo>();
        Integer id = null;
        id = CollectionEventHelper.createCEventWithSourceSpecimens(
            appService, patientId, centerId);
        CollectionEvent added =
            (CollectionEvent) appService.search(CollectionEvent.class, id);
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
}
