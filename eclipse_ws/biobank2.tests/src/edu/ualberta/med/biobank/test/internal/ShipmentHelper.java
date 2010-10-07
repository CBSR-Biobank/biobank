package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class ShipmentHelper extends DbHelper {

    public static ShipmentWrapper newShipment(SiteWrapper site,
        ClinicWrapper clinic, ShippingMethodWrapper method, String waybill,
        Date dateReceived, PatientWrapper... patients) throws Exception {
        ShipmentWrapper shipment = new ShipmentWrapper(appService);
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

        shipment.setDateShipped(Utils.getRandomDate());

        if (patients != null) {
            shipment.addPatients(Arrays.asList(patients));
        }

        return shipment;
    }

    public static ShipmentWrapper newShipment(SiteWrapper site,
        ClinicWrapper clinic, ShippingMethodWrapper method) throws Exception {
        return newShipment(site, clinic, method, TestCommon.getNewWaybill(r),
            Utils.getRandomDate());
    }

    public static ShipmentWrapper addShipment(SiteWrapper site,
        ClinicWrapper clinic, ShippingMethodWrapper method,
        PatientWrapper... patients) throws Exception {
        return addShipment(site, clinic, method, TestCommon.getNewWaybill(r),
            patients);
    }

    public static ShipmentWrapper addShipment(SiteWrapper site,
        ClinicWrapper clinic, ShippingMethodWrapper method, String waybill,
        PatientWrapper... patients) throws Exception {
        ShipmentWrapper shipment = newShipment(site, clinic, method,
            waybill, Utils.getRandomDate(), patients);
        shipment.persist();
        clinic.reload();
        return shipment;
    }

    public static ShipmentWrapper addShipmentWithRandomPatient(
        SiteWrapper site, ClinicWrapper clinic, String name) throws Exception {
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();

        PatientWrapper patient = PatientHelper.addPatient(name, study);

        return addShipment(site, clinic,

        ShippingMethodWrapper.getShippingMethods(appService).get(0), patient);
    }

}
