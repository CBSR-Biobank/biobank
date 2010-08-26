package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ClinicShipmentHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DispatchInfoHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestDispatchContainer extends TestDatabase {

    @Test
    public void testDispatchContainerCascade() throws Exception {
        String name = "testDispatchContainerCascade" + r.nextInt();

        SiteWrapper sender = SiteHelper.addSite(name + "_SENDER");
        SiteWrapper receiver = SiteHelper.addSite(name + "_RECEIVER");

        StudyWrapper study = StudyHelper.addStudy(name);

        DispatchInfoHelper.addInfo(study, sender, receiver);

        PatientWrapper patient = PatientHelper.addPatient(name, study);

        ClinicWrapper clinic = ClinicHelper.addClinic(name);

        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        study.addContacts(Arrays.asList(contact));
        study.persist();

        ClinicShipmentWrapper shipment = ClinicShipmentHelper.newShipment(
            sender, clinic);
        shipment.addPatients(Arrays.asList(patient));
        shipment.persist();

        PatientVisitWrapper pv = PatientVisitHelper.addPatientVisit(patient,
            shipment, new Date(), new Date());

        AliquotWrapper aliquot = new AliquotWrapper(appService);
        aliquot.setSampleType(SampleTypeWrapper.getAllSampleTypes(appService,
            false).get(0));
        aliquot.setInventoryId(name);
        aliquot.setPatientVisit(pv);
        aliquot.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        aliquot.persist();

        DispatchShipmentWrapper ship = new DispatchShipmentWrapper(appService);
        ship.setReceiver(receiver);
        ship.setSender(sender);
        ship.persist();

        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(
            sender, name + "_shipping", name + "ship", 1, 8, 12, false);

        DispatchContainerWrapper cont = new DispatchContainerWrapper(appService);
        cont.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        cont.setContainerType(type);
        cont.setShipment(ship);
        cont.setProductBarcode(name);
        cont.persist();

        cont.addAliquot(0, 0, aliquot);
        cont.persist();

        cont.reload();
        Assert.assertEquals(1, cont.getAliquots().size());
    }

}
