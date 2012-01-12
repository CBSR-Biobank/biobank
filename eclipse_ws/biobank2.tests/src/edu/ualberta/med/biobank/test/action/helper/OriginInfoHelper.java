package edu.ualberta.med.biobank.test.action.helper;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.action.IActionExecutor;
import edu.ualberta.med.biobank.test.Utils;

public class OriginInfoHelper extends Helper {

    public static OriginInfoSaveInfo createSaveOriginInfoSpecimenInfoRandom(
        IActionExecutor appService,
        Integer patientId, Integer siteId, Integer centerId) {
        Set<Integer> ids = new HashSet<Integer>();
        Integer id = null;
        try {
            id =
                CollectionEventHelper.createCEventWithSourceSpecimens(
                    appService,
                    patientId, centerId);
            CollectionEvent added = new CollectionEvent();
            added.setId(id);
            added =
                (CollectionEvent) appService.search(CollectionEvent.class,
                    added)
                    .get(0);
            for (Specimen spec : added.getAllSpecimenCollection()) {
                ids.add(spec.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new OriginInfoSaveInfo(r.nextInt(), siteId, id,
            Utils.getRandomString(10),
            ids, null);
    }
}
