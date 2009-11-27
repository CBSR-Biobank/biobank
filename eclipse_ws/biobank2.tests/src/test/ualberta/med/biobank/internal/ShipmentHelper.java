package test.ualberta.med.biobank.internal;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import test.ualberta.med.biobank.Utils;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class ShipmentHelper extends DbHelper {

    public static ShipmentWrapper newShipmentWithShptSampleSource(
        ClinicWrapper clinic, String waybill, Date dateReceived,
        PatientWrapper patient) throws Exception {
        ShipmentWrapper shipment = new ShipmentWrapper(appService);
        shipment.setClinic(clinic);
        shipment.setWaybill(waybill);
        if (dateReceived != null) {
            shipment.setDateReceived(dateReceived);
        }

        if (patient != null) {
            ShptSampleSourceWrapper sssw = new ShptSampleSourceWrapper(
                appService);
            sssw.setDateDrawn(Utils.getRandomDate());
            sssw.setPatientCollection(Arrays
                .asList(new PatientWrapper[] { patient }));
            sssw.setShipment(shipment);
            sssw.setQuantity(r.nextInt(15));
            List<SampleSourceWrapper> sswList = SampleSourceWrapper
                .getAllSampleSources(appService);
            sssw.setSampleSource(sswList.get(r.nextInt(sswList.size())));

            shipment.setShptSampleSourceCollection(Arrays
                .asList(new ShptSampleSourceWrapper[] { sssw }));
        }

        return shipment;
    }

    public static ShipmentWrapper addShipmentWithShptSampleSource(
        ClinicWrapper clinic, PatientWrapper patient) throws Exception {
        return addShipmentWithShptSampleSource(clinic,
            Utils.getRandomString(5), patient);
    }

    public static ShipmentWrapper newShipment(ClinicWrapper clinic)
        throws Exception {
        return newShipmentWithShptSampleSource(clinic,
            Utils.getRandomString(5), Utils.getRandomDate(), null);
    }

    public static ShipmentWrapper addShipmentWithShptSampleSource(
        ClinicWrapper clinic, String waybill, PatientWrapper patient)
        throws Exception {
        ShipmentWrapper shipment = newShipmentWithShptSampleSource(clinic,
            waybill, Utils.getRandomDate(), patient);
        shipment.persist();
        return shipment;
    }

    public static ShipmentWrapper addShipmentWithRandomObjects(
        ClinicWrapper clinic, String name) throws Exception {
        StudyWrapper study = StudyHelper.addStudy(clinic.getSite(), name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
        study.persist();

        PatientWrapper patient = PatientHelper.addPatient(name, study);

        return addShipmentWithShptSampleSource(clinic, patient);
    }

}
