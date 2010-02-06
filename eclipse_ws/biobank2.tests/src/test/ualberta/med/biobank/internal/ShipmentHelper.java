package test.ualberta.med.biobank.internal;

import java.util.Arrays;
import java.util.Date;

import test.ualberta.med.biobank.Utils;
import test.ualberta.med.biobank.wrappers.TestCommon;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class ShipmentHelper extends DbHelper {

    public static ShipmentWrapper newShipment(ClinicWrapper clinic,
        String waybill, Date dateReceived, PatientWrapper... patients)
        throws Exception {
        ShipmentWrapper shipment = new ShipmentWrapper(appService);
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

    public static ShipmentWrapper newShipment(ClinicWrapper clinic)
        throws Exception {
        return newShipment(clinic, TestCommon.getNewWaybill(r), Utils
            .getRandomDate());
    }

    public static ShipmentWrapper addShipment(ClinicWrapper clinic,
        PatientWrapper... patients) throws Exception {
        return addShipment(clinic, TestCommon.getNewWaybill(r), patients);
    }

    public static ShipmentWrapper addShipment(ClinicWrapper clinic,
        String waybill, PatientWrapper... patients) throws Exception {
        ShipmentWrapper shipment = newShipment(clinic, waybill, Utils
            .getRandomDate(), patients);
        shipment.persist();
        clinic.reload();
        return shipment;
    }

    public static ShipmentWrapper addShipmentWithRandomPatient(
        ClinicWrapper clinic, String name) throws Exception {
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();

        PatientWrapper patient = PatientHelper.addPatient(name, study);

        return addShipment(clinic, patient);
    }

}
