package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.ClassUtils;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyPvAttrWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestActivityStatus extends TestDatabase {

    private List<ActivityStatusWrapper> addedstatus = new ArrayList<ActivityStatusWrapper>();

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        for (ActivityStatusWrapper a : addedstatus) {
            a.delete();
        }
    }

    @Test
    public void testConstructors() throws Exception {
        new ActivityStatusWrapper(appService);
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
        new ActivityStatusWrapper(appService, activeAs.getWrappedObject());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(name);
        as.persist();
        int before = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        as.delete();
        List<ActivityStatusWrapper> allActivityStatuses = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        int after = allActivityStatuses.size();
        Assert.assertEquals(before - 1, after);
        Assert.assertFalse(allActivityStatuses.contains(as));

    }

    @Test
    public void testDeleteFail() throws Exception {
        String name = "testDeleteFail" + r.nextInt();

        // should not be allowed to remove an activity status that is used
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name, false, false);
        ContainerWrapper topContainer = ContainerHelper.addTopContainerRandom(
            site, name, 2, 2);
        ContainerTypeWrapper topContainerType = topContainer.getContainerType();
        topContainerType.addSampleTypes(SampleTypeWrapper.getAllSampleTypes(
            appService, false));
        topContainerType.persist();

        study.setStudyPvAttr("worksheet", "text");
        study.persist();

        StudyPvAttrWrapper spa = StudyPvAttrWrapper.getStudyPvAttrCollection(
            study).get(0);

        SampleTypeWrapper sampleType = SampleTypeWrapper.getAllSampleTypes(
            appService, false).get(0);

        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addContacts(Arrays.asList(contact));
        study.persist();
        study.reload();

        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ClinicShipmentWrapper shipment = ShipmentHelper.addShipment(site,
            clinic, patient);
        PatientVisitWrapper visit = PatientVisitHelper.addPatientVisit(patient,
            shipment, Utils.getRandomDate(), Utils.getRandomDate());

        AliquotWrapper aliquot = AliquotHelper.addAliquot(sampleType,
            topContainer, visit, 0, 0);

        ModelWrapper<?>[] wrappers = new ModelWrapper<?>[] { aliquot, spa,
            topContainer, topContainerType };

        for (ModelWrapper<?> wrapper : wrappers) {
            testDeleteFail(wrapper,
                name + ClassUtils.getClassName(wrapper.getClass()), null);
        }

        // , clinic, study, site
        testDeleteFail(clinic,
            name + ClassUtils.getClassName(clinic.getClass()),
            new ModelWrapper<?>[] { visit, shipment, patient, study, contact });
    }

    private void testDeleteFail(ModelWrapper<?> wrapper, String asName,
        ModelWrapper<?>[] deleteWrappers) throws Exception {
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(asName);
        as.persist();
        as.reload();

        if (wrapper instanceof AliquotWrapper) {
            ((AliquotWrapper) wrapper).setActivityStatus(as);
        } else if (wrapper instanceof StudyPvAttrWrapper) {
            ((StudyPvAttrWrapper) wrapper).setActivityStatus(as);
        } else if (wrapper instanceof ContainerWrapper) {
            ((ContainerWrapper) wrapper).setActivityStatus(as);
        } else if (wrapper instanceof ContainerTypeWrapper) {
            ((ContainerTypeWrapper) wrapper).setActivityStatus(as);
        } else if (wrapper instanceof ClinicWrapper) {
            ((ClinicWrapper) wrapper).setActivityStatus(as);
        } else if (wrapper instanceof StudyWrapper) {
            ((StudyWrapper) wrapper).setActivityStatus(as);
        } else if (wrapper instanceof SiteWrapper) {
            ((SiteWrapper) wrapper).setActivityStatus(as);
        } else {
            Assert.fail("invalid wrapper class: "
                + wrapper.getClass().getName());
        }

        wrapper.persist();
        wrapper.reload();

        try {
            as.delete();
            Assert.fail("should not be allowed to delete activity status");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        if (deleteWrappers != null) {
            for (ModelWrapper<?> delWrapper : deleteWrappers) {
                delWrapper.delete();
            }
        }

        wrapper.delete();

        try {
            as.delete();
        } catch (Exception e) {
            Assert
                .fail("object deleted, should be allowed to delete activity status");
        }
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
        Assert.assertEquals(ActivityStatus.class, activeAs.getWrappedClass());
    }

    @Test
    public void testPersist() throws Exception {
        int before = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        String name = "testPersist" + r.nextInt();
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(name);
        as.persist();
        addedstatus.add(as);

        List<ActivityStatusWrapper> statuses = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        int after = statuses.size();
        Assert.assertEquals(before + 1, after);
        Assert.assertTrue(statuses.contains(as));

        // add 5 activity status that will eventually be deleted
        before = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        List<ActivityStatusWrapper> toDelete = new ArrayList<ActivityStatusWrapper>();
        for (int i = 0; i < 5; ++i) {
            name = "testPersist" + i + r.nextInt();
            as = new ActivityStatusWrapper(appService);
            as.setName(name);
            as.persist();
            as.reload();
            toDelete.add(as);
        }

        statuses = ActivityStatusWrapper.getAllActivityStatuses(appService);
        after = statuses.size();
        Assert.assertEquals(before + 5, after);
        Assert.assertTrue(statuses.containsAll(toDelete));

        // create 3 new activity statuses
        before = after;
        List<ActivityStatusWrapper> toAdd = new ArrayList<ActivityStatusWrapper>();
        for (int i = 0; i < 3; ++i) {
            name = "testPersist" + i + r.nextInt();
            as = new ActivityStatusWrapper(appService);
            as.setName(name);
            toAdd.add(as);
        }

        ActivityStatusWrapper.persistActivityStatuses(toAdd, toDelete);

        // now delete the ones previously added and add the new ones
        statuses = ActivityStatusWrapper.getAllActivityStatuses(appService);
        after = statuses.size();
        Assert.assertEquals(before - 5 + 3, after);
        Assert.assertTrue(statuses.containsAll(toAdd));
    }

    @Test
    public void testPersistFail() throws Exception {
        String name = "testPersistFail" + r.nextInt();
        int before = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(name);
        as.persist();
        addedstatus.add(as);
        int after = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        Assert.assertEquals(before + 1, after);

        ActivityStatusWrapper newAs = new ActivityStatusWrapper(appService);
        newAs.setName(name);
        try {
            newAs.persist();
            Assert.fail("Cannot have 2 statuses with same name");
            addedstatus.add(newAs);
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetName() throws Exception {
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
        Assert.assertEquals("Active", activeAs.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
        ActivityStatusWrapper closedAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Closed");

        Assert.assertTrue(activeAs.compareTo(closedAs) < 0);
        Assert.assertTrue(closedAs.compareTo(activeAs) > 0);
    }

    @Test
    public void testGetAllActivityStatuses() throws Exception {
        Collection<ActivityStatusWrapper> list = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        Assert.assertTrue(list.size() >= 4);

        List<String> names = new ArrayList<String>();
        for (ActivityStatusWrapper as : list) {
            names.add(as.getName());
        }
        Assert.assertTrue(names.contains("Active"));
        Assert.assertTrue(names.contains("Closed"));
        Assert.assertTrue(names.contains("Disabled"));
        Assert.assertTrue(names.contains("Flagged"));

        // invoke one more time to make sure a database access is not made
        list = ActivityStatusWrapper.getAllActivityStatuses(appService);
    }

    @Test
    public void testGetActivityStatus() throws Exception {
        ActivityStatusWrapper.getActivityStatus(appService, "Active");

        try {
            ActivityStatusWrapper.getActivityStatus(appService,
                Utils.getRandomString(15, 20));
            Assert
                .fail("should not be allowed to retreive invalid activity status");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testIsActive() throws Exception {
        ActivityStatusWrapper active = ActivityStatusWrapper.getActivityStatus(
            appService, "Active");
        Assert.assertTrue(active.isActive());

        ActivityStatusWrapper closed = ActivityStatusWrapper.getActivityStatus(
            appService, "Closed");
        Assert.assertFalse(closed.isActive());
    }
}
