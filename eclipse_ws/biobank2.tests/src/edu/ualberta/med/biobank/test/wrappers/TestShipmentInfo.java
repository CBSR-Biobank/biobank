package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestShipmentInfo extends TestDatabase {
    @Test
    public void testPersistFailWaybillNull() throws Exception {
        String name = "testPersistFailWaybillNull" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        ClinicWrapper clinic = ClinicHelper.addClinicWithShipments(name);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        SpecimenWrapper spc = SpecimenHelper.newSpecimen(name);

        CollectionEventWrapper cevent = CollectionEventHelper
            .newCollectionEvent(clinic, patient, 1, spc);

        try {
            cevent.persist();
            Assert.fail("cevent with waybill null");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersistFailNoNeedWaybill() throws Exception {
        String name = "testPersistFailNoNeedWaybill" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name);

        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        // FIXME
        // CollectionEventWrapper cevent = CollectionEventHelper
        // .newCollectionEvent(clinic, ShippingMethodWrapper
        // .getShippingMethods(appService).get(0), TestCommon
        // .getNewWaybill(r), Utils.getRandomDate(), SpecimenHelper
        // .newSpecimen(patient, Utils.getRandomDate(), 0.1));
        //
        // try {
        // cevent.persist();
        // Assert.fail("cevent should not have a waybill");
        // } catch (BiobankCheckException bce) {
        // Assert.assertTrue(true);
        // }
        //
        // // should not have any waybill
        // cevent.setWaybill(null);
        // cevent.persist();
    }

    @Test
    public void testPersistFailWaybillExists() throws Exception {
        String name = "testPersistFailWaybillExists" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinicWithShipments(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        // FIXME
        // CollectionEventWrapper cevent = CollectionEventHelper
        // .newCollectionEvent(clinic, method, name, Utils.getRandomDate(),
        // SpecimenHelper.newSpecimen(patient, Utils.getRandomDate(), 0.1));
        // cevent.persist();
        //
        // CollectionEventWrapper cevent2 = CollectionEventHelper
        // .newCollectionEvent(clinic, method, name, Utils.getRandomDate(),
        // SpecimenHelper.newSpecimen(patient, Utils.getRandomDate(), 0.1));
        // try {
        // cevent2.persist();
        // Assert.fail("cevent with waybill '" + name
        // + "' already exists. An exception should be thrown.");
        // } catch (BiobankCheckException bce) {
        // Assert.assertTrue(true);
        // }
    }

}
