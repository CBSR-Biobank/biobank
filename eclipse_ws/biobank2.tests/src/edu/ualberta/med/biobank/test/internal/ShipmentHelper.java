package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;
import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class ShipmentHelper extends DbHelper {

    public static ShipmentWrapper newShipment(SiteWrapper site,
        ClinicWrapper clinic, String waybill, Date dateReceived,
        PatientWrapper... patients) throws Exception {
        ShipmentWrapper shipment = new ShipmentWrapper(appService);
        if (site != null) {
            shipment.setSite(site);
        }
        shipment.setClinic(clinic);
        shipment.setWaybill(waybill);
        if (dateReceived != null) {
            shipment.setDateReceived(dateReceived);
        }

        if (patients != null) {
            shipment.addPatients(Arrays.asList(patients));
        }

        return shipment;
    }

    public static ShipmentWrapper newShipment(SiteWrapper site,
        ClinicWrapper clinic) throws Exception {
        return newShipment(site, clinic, TestCommon.getNewWaybill(r),
            Utils.getRandomDate());
    }

    public static ShipmentWrapper addShipment(SiteWrapper site,
        ClinicWrapper clinic, PatientWrapper... patients) throws Exception {
        return addShipment(site, clinic, TestCommon.getNewWaybill(r), patients);
    }

    public static ShipmentWrapper addShipment(SiteWrapper site,
        ClinicWrapper clinic, String waybill, PatientWrapper... patients)
        throws Exception {
        ShipmentWrapper shipment = newShipment(site, clinic, waybill,
            Utils.getRandomDate(), patients);
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

        return addShipment(site, clinic, patient);
    }

}
