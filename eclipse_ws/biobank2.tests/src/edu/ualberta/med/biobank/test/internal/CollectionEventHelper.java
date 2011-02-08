package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class CollectionEventHelper extends DbHelper {

    public static CollectionEventWrapper newCollectionEvent(SiteWrapper site,
        ClinicWrapper clinic, ShippingMethodWrapper method, String waybill,
        Date dateReceived, PatientWrapper... patients) throws Exception {
        CollectionEventWrapper shipment = new CollectionEventWrapper(appService);
        if (site != null) {
            shipment.setSite(site);
        }
        shipment.setClinic(clinic);
        shipment.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        shipment.setShippingMethod(method);
        shipment.setWaybill(waybill);
        if (dateReceived != null) {
            shipment.setDateReceived(dateReceived);
        }

        shipment.setDeparted(Utils.getRandomDate());

        if (patients != null) {
            shipment.addPatients(Arrays.asList(patients));
        }

        return shipment;
    }

    public static CollectionEventWrapper newCollectionEvent(SiteWrapper site,
        ClinicWrapper clinic, ShippingMethodWrapper method) throws Exception {
        return newCollectionEvent(site, clinic, method,
            TestCommon.getNewWaybill(r), Utils.getRandomDate());
    }

    public static CollectionEventWrapper addCollectionEvent(SiteWrapper site,
        ClinicWrapper clinic, ShippingMethodWrapper method,
        PatientWrapper... patients) throws Exception {
        return addCollectionEvent(site, clinic, method,
            TestCommon.getNewWaybill(r), patients);
    }

    public static CollectionEventWrapper addCollectionEvent(SiteWrapper site,
        ClinicWrapper clinic, ShippingMethodWrapper method, String waybill,
        PatientWrapper... patients) throws Exception {
        CollectionEventWrapper shipment = newCollectionEvent(site, clinic,
            method, waybill, Utils.getRandomDate(), patients);
        shipment.persist();
        clinic.reload();
        return shipment;
    }

    public static CollectionEventWrapper addCollectionEventWithRandomPatient(
        SiteWrapper site, ClinicWrapper clinic, String name) throws Exception {
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();

        PatientWrapper patient = PatientHelper.addPatient(name, study);

        return addCollectionEvent(site, clinic,

        ShippingMethodWrapper.getShippingMethods(appService).get(0), patient);
    }

}
