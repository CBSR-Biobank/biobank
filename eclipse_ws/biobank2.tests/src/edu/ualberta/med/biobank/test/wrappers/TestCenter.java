package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.DispatchHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.RequestHelper;
import edu.ualberta.med.biobank.test.internal.ResearchGroupHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestCenter extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGettersAndSetters"
            + r.nextInt());
        testGettersAndSetters(site);
    }

    @Test
    public void testGetRequest() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testRequest");
        StudyWrapper testStudy = StudyHelper.addStudy("testStudy");
        CollectionEventWrapper ce = CollectionEventHelper.addCollectionEvent(
            site, PatientHelper.addPatient("testP", testStudy), 0,
            SpecimenHelper.newSpecimen(
                SpecimenTypeHelper.addSpecimenType("testTypeRequest"),
                "Active", new Date()));
        RequestHelper.addRequest(testStudy, true,
            ce.getAllSpecimenCollection(false)
                .toArray(new SpecimenWrapper[] {}));
        site.reload();
        Assert.assertEquals(1,
            CenterWrapper.getRequestCollection(appService, site).size());
    }

    @Test
    public void testGetProcessingEvents() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testProcessingEvents");
        ProcessingEventHelper.addProcessingEvent(site,
            PatientWrapper.getPatient(appService, "testP"), new Date());
        ProcessingEventHelper.addProcessingEvent(site,
            PatientWrapper.getPatient(appService, "testP"), new Date());
        ProcessingEventHelper.addProcessingEvent(site,
            PatientWrapper.getPatient(appService, "testP"), new Date());

        site.reload();
        Assert.assertEquals(3, site.getProcessingEventCount());
        Assert.assertEquals(3, site.getProcessingEventCount(true));
    }

    @Test
    public void testGetAliquotedSpecimens() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testAliquots" + r.nextInt());
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site,
                PatientWrapper.getPatient(appService, "testP"), new Date());
        SpecimenTypeWrapper testtype = SpecimenTypeHelper
            .addSpecimenType("testTypeAliquoted");
        SpecimenWrapper parent = SpecimenHelper.newSpecimen(testtype, "Active",
            new Date());
        CollectionEventWrapper ce = CollectionEventHelper.addCollectionEvent(
            site, PatientHelper.addPatient("testP",
                StudyHelper.addStudy("testStudy")), 0, parent);
        parent.reload();
        ce.reload();
        pevent.reload();
        site.reload();

        SpecimenHelper.addSpecimen(
            ce.getOriginalSpecimenCollection(false).get(0), testtype, ce,
            pevent);

        SpecimenHelper.addSpecimen(
            ce.getOriginalSpecimenCollection(false).get(0), testtype, ce,
            pevent);
        SpecimenHelper.addSpecimen(
            ce.getOriginalSpecimenCollection(false).get(0), testtype, ce,
            pevent);

        site.reload();
        Assert.assertEquals(3, site.getAliquotedSpecimenCount().intValue());
    }

    @Test
    public void testGetStudyCollection() throws Exception {
        ResearchGroupWrapper rg = ResearchGroupHelper.addResearchGroup(
            "testGetStudyDefault", true);
        Assert.assertTrue(rg.getStudyCollection().size() == 0);
    }

    @Test
    public void testGetCenters() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testCentersSite" + r.nextInt());
        ResearchGroupWrapper rg = ResearchGroupHelper.addResearchGroup(
            "testCentersRG", true);
        ClinicWrapper clinic = ClinicHelper.addClinic("testCentersClinic");
        Assert.assertEquals(3, CenterWrapper.getCenters(appService).size());
        Assert.assertEquals(rg.getId().intValue(), CenterWrapper
            .getCenterFromId(appService, rg.getId()).getId().intValue());
        Assert.assertEquals(2, CenterWrapper.getOtherCenters(appService, site)
            .size());
    }

    @Test
    public void testDispatches() throws Exception {
        ClinicWrapper clinic = ClinicHelper.addClinic("testDispatchClinic");
        SiteWrapper site = SiteHelper.addSite("TestDispatchSite");
        DispatchWrapper dispatch = DispatchHelper.addDispatch(clinic, site,
            ShippingMethodHelper.addShippingMethod("blah"));
        dispatch.setState(DispatchState.CREATION);
        dispatch.persist();
        clinic.reload();
        site.reload();
        Assert.assertEquals(1,
            CenterWrapper.getInCreationDispatchCollection(clinic).size());
        Assert.assertEquals(0,
            CenterWrapper.getInCreationDispatchCollection(site).size());

        dispatch.setState(DispatchState.IN_TRANSIT);
        dispatch.persist();
        clinic.reload();
        site.reload();
        Assert.assertEquals(1, CenterWrapper
            .getInTransitSentDispatchCollection(clinic).size());
        Assert.assertEquals(1, CenterWrapper
            .getInTransitReceiveDispatchCollection(site).size());

        dispatch.setState(DispatchState.RECEIVED);
        dispatch.persist();
        clinic.reload();
        site.reload();

        Assert.assertEquals(1, clinic.getSrcDispatchCollection(false).size());
        Assert.assertEquals(1, site.getDstDispatchCollection(false).size());

        Assert.assertEquals(0, CenterWrapper
            .getReceivingNoErrorsDispatchCollection(clinic).size());
        Assert.assertEquals(1, CenterWrapper
            .getReceivingNoErrorsDispatchCollection(site).size());

        CollectionEventWrapper ce = CollectionEventHelper.addCollectionEvent(
            clinic, PatientHelper.addPatient("testP",
                StudyHelper.addStudy("testStudy")), 0, SpecimenHelper
                .newSpecimen(SpecimenTypeHelper.addSpecimenType("testType"),
                    "Active", new Date()));

        dispatch.addSpecimens(ce.getAllSpecimenCollection(false),
            DispatchSpecimenState.EXTRA);
        dispatch.persist();
        clinic.reload();
        site.reload();

        Assert.assertEquals(0, CenterWrapper
            .getReceivingWithErrorsDispatchCollection(clinic).size());
        Assert.assertEquals(1, CenterWrapper
            .getReceivingWithErrorsDispatchCollection(site).size());

    }

    @Test
    public void testPersistFailNoAcivityStatus() throws Exception {
        int oldTotal = SiteWrapper.getSites(appService).size();
        String name = "testPersistFailNoAddress" + r.nextInt();
        SiteWrapper site = new SiteWrapper(appService);
        site.setName(name);
        site.setNameShort(name);
        site.setCity("Vesoul");

        try {
            site.persist();
            Assert.fail("Should not insert the site : no activity status");
        } catch (ValueNotSetException e) {
            Assert.assertTrue(true);
        }

        site.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        SiteHelper.createdSites.add(site);
        site.persist();
        int newTotal = SiteWrapper.getSites(appService).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testResetNew() throws Exception {
        SiteWrapper newSite = new SiteWrapper(appService);
        newSite.setName("titi");
        newSite.reset();
        Assert.assertEquals(null, newSite.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite("QWERTY" + name);
        SiteWrapper site2 = SiteHelper.addSite("ASDFG" + name);

        Assert.assertTrue(site.compareTo(site2) > 0);
        Assert.assertTrue(site2.compareTo(site) < 0);
        Assert.assertTrue(site.compareTo(site) == 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testResetAlreadyInDatabase"
            + r.nextInt());
        site.reload();
        String oldName = site.getName();
        site.setName("toto");
        site.reset();
        Assert.assertEquals(oldName, site.getName());
    }

    @Test
    public void testDelete() throws Exception {
        SiteWrapper site = SiteHelper
            .addSite("testDelete" + r.nextInt(), false);

        // object is in database
        Site siteInDB = ModelUtils.getObjectWithId(appService, Site.class,
            site.getId());
        Assert.assertNotNull(siteInDB);

        site.delete();

        siteInDB = ModelUtils.getObjectWithId(appService, Site.class,
            site.getId());
        // object is not anymore in database
        Assert.assertNull(siteInDB);
    }

    @Test
    public void testPersistFailNoAddress() throws Exception {
        int oldTotal = SiteWrapper.getSites(appService).size();
        String name = "testPersistFailNoAddress" + r.nextInt();
        SiteWrapper site = new SiteWrapper(appService);
        site.setName(name);
        site.setNameShort(name);
        site.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));

        try {
            site.persist();
            Assert.fail("Should not insert the site : no address");
        } catch (ValueNotSetException e) {
            Assert.assertTrue(true);
        }

        site.setCity("Vesoul");
        SiteHelper.createdSites.add(site);
        site.persist();
        int newTotal = SiteWrapper.getSites(appService).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testGetCollectionEvent() throws Exception {
        String name = "testGetProcessingEventCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addToContactCollection(Arrays.asList(contact1, contact2));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addToContactCollection(Arrays.asList(contact2));
        study2.persist();

        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper
            .addPatient(name + "_p2", study2);

        List<CollectionEventWrapper> ceventSet1 = CollectionEventHelper
            .addCollectionEvents(site, patient1, name + "_set1");
        site.reload();
        Assert.assertEquals(ceventSet1.size(), site.getCollectionEventCount());

        List<CollectionEventWrapper> ceventSet2 = CollectionEventHelper
            .addCollectionEvents(site, patient2, name + "_set2");
        site.reload();
        Assert.assertEquals(ceventSet1.size() + ceventSet2.size(),
            site.getCollectionEventCount());

        // delete cevent set 1
        DbHelper.deleteCollectionEvents(ceventSet1);
        site.reload();
        Assert.assertEquals(ceventSet2.size(), site.getCollectionEventCount());

        // delete cevent set 2
        DbHelper.deleteCollectionEvents(ceventSet2);
        site.reload();
        Assert.assertEquals(0, site.getCollectionEventCount());
    }
}
