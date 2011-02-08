package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class CollectionEventHelper extends DbHelper {

    public static CollectionEventWrapper newCollectionEvent(CenterWrapper site,
        ShippingMethodWrapper method, String waybill, Date dateReceived,
        SourceVesselWrapper... svs) throws Exception {
        CollectionEventWrapper shipment = new CollectionEventWrapper(appService);
        if (site != null) {
            shipment.setSourceCenter(site);
        }
        shipment.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        shipment.setShippingMethod(method);
        shipment.setWaybill(waybill);
        if (dateReceived != null) {
            shipment.setDateReceived(dateReceived);
        }

        shipment.setDeparted(Utils.getRandomDate());

        if (svs != null) {
            shipment.addSourceVessels(Arrays.asList(svs));
        }

        return shipment;
    }

    public static CollectionEventWrapper newCollectionEvent(CenterWrapper site,
        ShippingMethodWrapper method) throws Exception {
        return newCollectionEvent(site, method, TestCommon.getNewWaybill(r),
            Utils.getRandomDate());
    }

    public static CollectionEventWrapper addCollectionEvent(CenterWrapper site,
        ShippingMethodWrapper method, SourceVesselWrapper... svs)
        throws Exception {
        return addCollectionEvent(site, method, TestCommon.getNewWaybill(r),
            svs);
    }

    public static CollectionEventWrapper addCollectionEvent(CenterWrapper site,
        ShippingMethodWrapper method, String waybill,
        SourceVesselWrapper... svs) throws Exception {
        CollectionEventWrapper shipment = newCollectionEvent(site, method,
            waybill, Utils.getRandomDate(), svs);
        shipment.persist();
        return shipment;
    }

    public static CollectionEventWrapper addCollectionEventWithRandomPatient(
        CenterWrapper site, ClinicWrapper clinic, String name) throws Exception {
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();

        PatientWrapper patient = PatientHelper.addPatient(name, study);

        return addCollectionEvent(site, ShippingMethodWrapper
            .getShippingMethods(appService).get(0), "wb-54",
            SourceVesselHelper.newSourceVessel("testSV", patient,
                Utils.getRandomDate(), 0.1));

    }
}
