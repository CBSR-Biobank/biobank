package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class CollectionEventHelper extends DbHelper {

    public static CollectionEventWrapper newCollectionEvent(
        CenterWrapper<?> site, ShippingMethodWrapper method, String waybill,
        Date dateReceived, SourceVesselWrapper... svs) throws Exception {
        CollectionEventWrapper cevent = new CollectionEventWrapper(appService);
        if (site != null) {
            cevent.setSourceCenter(site);
            site.addToCollectionEventCollection(Arrays.asList(cevent));
        }
        cevent.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        cevent.setShippingMethod(method);
        cevent.setWaybill(waybill);
        if (dateReceived != null) {
            cevent.setDateReceived(dateReceived);
        }

        cevent.setDeparted(Utils.getRandomDate());

        if ((svs != null) && (svs.length != 0)) {
            cevent.addToSourceVesselCollection(Arrays.asList(svs));
            for (SourceVesselWrapper sv : svs)
                sv.setCollectionEvent(cevent);
        }

        return cevent;
    }

    public static CollectionEventWrapper newCollectionEvent(
        CenterWrapper<?> site, ShippingMethodWrapper method) throws Exception {
        return newCollectionEvent(site, method, TestCommon.getNewWaybill(r),
            Utils.getRandomDate());
    }

    public static CollectionEventWrapper addCollectionEvent(
        CenterWrapper<?> site, ShippingMethodWrapper method,
        SourceVesselWrapper... svs) throws Exception {
        return addCollectionEvent(site, method, TestCommon.getNewWaybill(r),
            svs);
    }

    public static CollectionEventWrapper addCollectionEvent(
        CenterWrapper<?> site, ShippingMethodWrapper method, String waybill,
        SourceVesselWrapper... svs) throws Exception {
        CollectionEventWrapper cevent = newCollectionEvent(site, method,
            waybill, Utils.getRandomDate(), svs);
        cevent.persist();
        return cevent;
    }

    public static CollectionEventWrapper addCollectionEventWithRandomPatient(
        CenterWrapper<?> site, String name) throws Exception {
        StudyWrapper study = StudyHelper.addStudy(name);
        study.persist();

        return addCollectionEvent(site, ShippingMethodWrapper
            .getShippingMethods(appService).get(0), "wb-54");

    }
}
