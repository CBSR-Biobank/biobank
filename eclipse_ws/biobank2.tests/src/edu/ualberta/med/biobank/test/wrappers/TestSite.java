package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.DuplicateEntryException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.OriginInfoHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TestSite extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGettersAndSetters"
            + r.nextInt());
        testGettersAndSetters(site);
    }

    @Test
    public void testAddress() throws Exception {
        SiteWrapper site = new SiteWrapper(appService);
        Assert.assertEquals(null, site.getStreet1());
        Assert.assertEquals(null, site.getStreet2());
        Assert.assertEquals(null, site.getCity());
        Assert.assertEquals(null, site.getProvince());
        Assert.assertEquals(null, site.getPostalCode());

        site.setStreet1("testNullAddress1");
        site.setStreet2("testNullAddress2");
        site.setCity("testNullAddress3");
        site.setProvince("testNullAddress4");
        site.setPostalCode("testNullAddress5");

        Assert.assertNotNull(site.getStreet1());
        Assert.assertNotNull(site.getStreet2());
        Assert.assertNotNull(site.getCity());
        Assert.assertNotNull(site.getProvince());
        Assert.assertNotNull(site.getPostalCode());
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        String name = "testGetWrappedClass" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        Assert.assertEquals(Site.class, site.getWrappedClass());
    }

    @Test
    public void testGetStudyCollection() throws Exception {
        String name = "testGetStudyCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        try {
            site.removeFromStudyCollectionWithCheck(new ArrayList<StudyWrapper>());
            Assert.assertTrue(true);
        } catch (BiobankCheckException e) {
            Assert.fail("cannot call removeStudies with empty list");
        }

        List<StudyWrapper> studySet1 = StudyHelper.addStudies(name + "_s1_",
            r.nextInt(10) + 1);
        site.addToStudyCollection(studySet1);
        site.persist();
        site.reload();
        List<StudyWrapper> siteStudies = site.getStudyCollection(false);

        Assert.assertEquals(studySet1.size(), siteStudies.size());

        // add another set
        List<StudyWrapper> studySet2 = StudyHelper.addStudies(name + "_s2_",
            r.nextInt(10) + 1);
        site.addToStudyCollection(studySet2);
        site.persist();
        site.reload();
        siteStudies = site.getStudyCollection(false);

        Assert.assertEquals(studySet1.size() + studySet2.size(),
            siteStudies.size());

        // remove studies
        site.removeFromStudyCollection(studySet1);
        site.persist();
        site.reload();
        siteStudies = site.getStudyCollection(false);

        Assert.assertEquals(studySet2.size(), siteStudies.size());
        Assert.assertTrue(siteStudies.containsAll(studySet2));

        // try and remove studies that were already removed
        try {
            site.removeFromStudyCollectionWithCheck(studySet1);
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetStudyCollectionSorted() throws Exception {
        String name = "testGetStudyCollectionSorted" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyHelper.addStudies(name, r.nextInt(15) + 5);

        List<StudyWrapper> studies = StudyWrapper.getAllStudies(appService);
        site.addToStudyCollection(studies);
        site.persist();
        site.reload();

        List<StudyWrapper> studiesSorted = site.getStudyCollection(true);
        Assert.assertTrue(studiesSorted.size() > 1);
        for (int i = 0, n = studiesSorted.size() - 1; i < n; i++) {
            StudyWrapper study1 = studiesSorted.get(i);
            StudyWrapper study2 = studiesSorted.get(i + 1);
            Assert.assertTrue(study1.compareTo(study2) <= 0);
        }
    }

    @Test
    public void testNonAssocStudies() throws Exception {
        String name = "testNonAssocStudies" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        try {
            site.removeFromStudyCollectionWithCheck(new ArrayList<StudyWrapper>());
            Assert.assertTrue(true);
        } catch (BiobankCheckException e) {
            Assert.fail("cannot call removeStudies with empty list");
        }

        // create 2 sets
        List<StudyWrapper> studySet1 = StudyHelper.addStudies(name + "_s1_",
            r.nextInt(10) + 1);
        List<StudyWrapper> studySet2 = StudyHelper.addStudies(name + "_s2_",
            r.nextInt(10) + 1);

        // add set 1
        site.addToStudyCollection(studySet1);
        site.persist();
        site.reload();
        List<StudyWrapper> siteNonAssocStudies = site.getStudiesNotAssoc();

        Assert.assertEquals(studySet2.size(), siteNonAssocStudies.size());

        // remove set 1 and add set 2
        site.removeFromStudyCollection(studySet1);
        site.addToStudyCollection(studySet2);
        site.persist();
        site.reload();
        siteNonAssocStudies = site.getStudiesNotAssoc();

        Assert.assertEquals(studySet1.size(), siteNonAssocStudies.size());

        // add set 1 again
        site.addToStudyCollection(studySet1);
        site.persist();
        site.reload();
        siteNonAssocStudies = site.getStudiesNotAssoc();

        Assert.assertEquals(0, siteNonAssocStudies.size());
    }

    @Test
    public void testAddStudies() throws Exception {
        String name = "testAddStudies" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyHelper.addStudies(name, r.nextInt(15) + 1);

        List<StudyWrapper> studies = StudyWrapper.getAllStudies(appService);
        int studiesNber = studies.size();
        site.addToStudyCollection(studies);
        site.persist();
        site.reload();

        Assert.assertEquals(studiesNber, site.getStudyCollection(false).size());

        // add one more study
        StudyWrapper newStudy = StudyHelper.addStudy(name + "newStudy");
        site.addToStudyCollection(Arrays.asList(newStudy));
        site.persist();
        site.reload();
        Assert.assertEquals(studiesNber + 1, site.getStudyCollection(false)
            .size());
    }

    @Test
    public void testRemoveStudies() throws Exception {
        String name = "testRemoveStudies" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyHelper.addStudies(name, r.nextInt(15) + 1);

        List<StudyWrapper> studies = StudyWrapper.getAllStudies(appService);
        int studiesNber = studies.size();
        site.addToStudyCollection(studies);
        site.persist();
        site.reload();

        Assert.assertEquals(studiesNber, site.getStudyCollection(false).size());

        // remove one study
        StudyWrapper newStudy = studies.get(0);
        site.removeFromStudyCollection(Arrays.asList(newStudy));
        site.persist();
        site.reload();
        Assert.assertEquals(studiesNber - 1, site.getStudyCollection(false)
            .size());
    }

    @Test
    public void testGetContainerTypeCollection() throws Exception {
        String name = "testGetContainerTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = r.nextInt(15) + 1;
        ContainerTypeHelper.addContainerTypesRandom(site, name, nber);

        List<ContainerTypeWrapper> types = site
            .getContainerTypeCollection(false);
        int sizeFound = types.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetContainerTypeCollectionSorted() throws Exception {
        String name = "testGetContainerTypeCollectionSorted" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ContainerTypeHelper.addContainerTypesRandom(site, name,
            r.nextInt(15) + 5);

        List<ContainerTypeWrapper> types = site
            .getContainerTypeCollection(true);
        if (types.size() > 1) {
            for (int i = 0; i < types.size() - 1; i++) {
                ContainerTypeWrapper type1 = types.get(i);
                ContainerTypeWrapper type2 = types.get(i + 1);
                Assert.assertTrue(type1.compareTo(type2) <= 0);
            }
        }
    }

    @Test
    public void testAddContainerTypes() throws Exception {
        String name = "testAddContainerTypes" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = r.nextInt(15) + 1;
        ContainerTypeHelper.addContainerTypesRandom(site, name, nber);

        ContainerTypeWrapper type = ContainerTypeHelper.newContainerType(site,
            name + "newType", name, 1, 5, 4, false);
        site.addToContainerTypeCollection(Arrays.asList(type));
        site.persist();

        site.reload();
        // one type added
        Assert.assertEquals(nber + 1, site.getContainerTypeCollection(false)
            .size());
    }

    @Test
    public void testGetContainerCollection() throws Exception {
        String name = "testGetContainerCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        // int totalContainers = ContainerHelper.addTopContainersWithChildren(
        // site, name, r.nextInt(3) + 1);

        int totalContainers = ContainerHelper.addTopContainersWithChildren(
            site, name, 1);

        List<ContainerWrapper> containers = site.getContainerCollection(false);
        int sizeFound = containers.size();

        Assert.assertEquals(totalContainers, sizeFound);
    }

    @Test
    public void testAddContainer() throws Exception {
        String name = "testAddContainer" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int totalContainers = ContainerHelper.addTopContainersWithChildren(
            site, name, r.nextInt(3) + 1);

        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            site, name);
        ContainerWrapper container = ContainerHelper.newContainer(
            String.valueOf(r.nextInt()), name + "newContainer", null, site,
            type);
        site.addToContainerCollection(Arrays.asList(container));
        site.persist();

        site.reload();
        // one container added
        Assert.assertEquals(totalContainers + 1,
            site.getContainerCollection(false).size());
    }

    @Test
    public void testPersist() throws Exception {
        int oldTotal = SiteWrapper.getSites(appService).size();
        SiteHelper.addSite("testPersist" + r.nextInt());
        int newTotal = SiteWrapper.getSites(appService).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
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
    public void testPersistFailNameUnique() throws Exception {
        int oldTotal = SiteWrapper.getSites(appService).size();
        String name = "testPersistFailNameUnique" + r.nextInt();
        SiteWrapper site = SiteHelper.newSite(name);
        site = SiteHelper.addSite(name);
        site.persist();

        SiteWrapper site2 = SiteHelper.newSite(name);
        try {
            site2.persist();
            Assert
                .fail("Should not insert the site : same name already in database");
        } catch (DuplicateEntryException e) {
            Assert.assertTrue(true);
        }

        site.setName("Other Name" + r.nextInt());
        site.persist();
        int newTotal = SiteWrapper.getSites(appService).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
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
    public void testDeleteFailNoMoreContainerType() throws Exception {
        int oldTotal = SiteWrapper.getSites(appService).size();
        String name = "testDeleteFailNoMoreContainerType" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name, false);

        ContainerTypeWrapper type = ContainerTypeHelper.addContainerType(site,
            name, name, 1, 2, 3, false);
        site.reload();

        try {
            site.delete();
            Assert
                .fail("Should not delete the site : a container type is still there");
        } catch (BiobankCheckException bce) {
            Assert.assertEquals(oldTotal + 1, SiteWrapper.getSites(appService)
                .size());
        }
        type.delete();
        site.reload();
        site.delete();
        Assert.assertEquals(oldTotal, SiteWrapper.getSites(appService).size());
    }

    @Test
    public void testDeleteFailNoMoreContainer() throws Exception {
        int oldTotal = SiteWrapper.getSites(appService).size();
        String name = "testDeleteFailNoMoreContainer" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name, false);

        ContainerWrapper container = ContainerHelper.addContainerRandom(site,
            name, null);
        site.reload();

        try {
            site.delete();
            Assert
                .fail("Should not delete the site : a container and a container type is still there");
        } catch (BiobankCheckException bce) {
            Assert.assertEquals(oldTotal + 1, SiteWrapper.getSites(appService)
                .size());
        }
        ContainerTypeWrapper type = container.getContainerType();
        container.delete();
        type.delete();
        site.reload();
        site.delete();
        Assert.assertEquals(oldTotal, SiteWrapper.getSites(appService).size());
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
    public void testResetNew() throws Exception {
        SiteWrapper newSite = new SiteWrapper(appService);
        newSite.setName("titi");
        newSite.reset();
        Assert.assertEquals(null, newSite.getName());
    }

    @Test
    public void testGetTopContainerCollection() throws Exception {
        String name = "testGetTopContainerCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int topNber = r.nextInt(8) + 1;
        ContainerHelper.addTopContainersWithChildren(site, name, topNber);

        List<ContainerWrapper> containers = site
            .getTopContainerCollection(false);
        Assert.assertEquals(topNber, containers.size());

        // clear the top containers and get again
        site.clearTopContainerCollection();
        containers = site.getTopContainerCollection(false);
        Assert.assertEquals(topNber, containers.size());
    }

    @Test
    public void testGetTopContainerCollectionSorted() throws Exception {
        String name = "testGetTopContainerCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ContainerHelper.addTopContainersWithChildren(site, name,
            r.nextInt(8) + 5);

        List<ContainerWrapper> containers = site
            .getTopContainerCollection(true);
        if (containers.size() > 1) {
            for (int i = 0; i < containers.size() - 1; i++) {
                ContainerWrapper container1 = containers.get(i);
                ContainerWrapper containter2 = containers.get(i + 1);
                Assert.assertTrue(container1.compareTo(containter2) <= 0);
            }
        }
    }

    @Test
    public void testGetSites() throws Exception {
        List<Site> sitesDBBefore = appService.search(Site.class, new Site());
        int nberSite = r.nextInt(15) + 1;
        SiteHelper.addSites("testGetSites" + r.nextInt(), nberSite);

        List<SiteWrapper> siteWrappers = SiteWrapper.getSites(appService);
        List<Site> sitesDB = appService.search(Site.class, new Site());
        int nberAddedInDB = sitesDB.size() - sitesDBBefore.size();
        Assert.assertEquals(nberSite, nberAddedInDB);
        Assert.assertEquals(siteWrappers.size(), siteWrappers.size());

        Site site = DbHelper.chooseRandomlyInList(sitesDB);
        siteWrappers = SiteWrapper.getSites(appService, site.getId());
        Assert.assertEquals(1, siteWrappers.size());

        HQLCriteria criteria = new HQLCriteria("select max(id) from "
            + Site.class.getName());
        List<Integer> max = appService.query(criteria);
        siteWrappers = SiteWrapper.getSites(appService, max.get(0) + 1000);
        Assert.assertEquals(0, siteWrappers.size());
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
    public void testAddCollectiotnEvents() throws Exception {
        String name = "testAddCollectionEvents" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study2);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);

        List<SourceVesselWrapper> svs = new ArrayList<SourceVesselWrapper>();
        for (PatientWrapper patient : new PatientWrapper[] { patient1, patient2 }) {
            svs.add(SourceVesselHelper.newSourceVessel(patient,
                Utils.getRandomDate(), 0.1));
            svs.add(SourceVesselHelper.newSourceVessel(patient,
                Utils.getRandomDate(), 0.1));
        }

        // FIXME
        // CollectionEventHelper.addCollectionEvent(site, method, svs.get(0),
        // svs.get(1));

        // make sure site has 2 collection events
        site.reload();
        // FIXME
        // Assert.assertEquals(1,
        // site.getCollectionEventCollection(false).size());
        //
        // CollectionEventHelper.addCollectionEvent(site, method, svs.get(2));
        // CollectionEventHelper.addCollectionEvent(site, method, svs.get(3));
        //
        // // make sure site has 2 more collection events
        // site.reload();
        // Assert.assertEquals(3,
        // site.getCollectionEventCollection(false).size());
    }

    private List<CollectionEventWrapper> createCollectionEvents(SiteWrapper site)
        throws Exception {
        String name = site.getName();

        List<CollectionEventWrapper> cEvents = new ArrayList<CollectionEventWrapper>();
        cEvents.add(CollectionEventHelper.addCollectionEventWithRandomPatient(
            site, name + "Study1", 1));
        cEvents.add(CollectionEventHelper.addCollectionEventWithRandomPatient(
            site, name + "Study2", 2));
        cEvents.add(CollectionEventHelper.addCollectionEventWithRandomPatient(
            site, name + "Study3", 3));
        cEvents.add(CollectionEventHelper.addCollectionEventWithRandomPatient(
            site, name + "Study4", 4));

        site.reload();
        return cEvents;
    }

    @Test
    public void testGetCollectionEventCollectionSorted() throws Exception {
        String name = "testGetShipmentCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        List<CollectionEventWrapper> shipments = createCollectionEvents(site);

        // FIXME
        // List<CollectionEventWrapper> savedShipments = site
        // .getCollectionEventCollection(true);
        // Assert.assertTrue(savedShipments.size() > 1);
        // Assert.assertEquals(shipments.size(), savedShipments.size());
        // for (int i = 0, n = savedShipments.size() - 1; i < n; i++) {
        // CollectionEventWrapper s1 = savedShipments.get(i);
        // CollectionEventWrapper s2 = savedShipments.get(i + 1);
        // Assert.assertTrue(s1.compareTo(s2) <= 0);
        // Assert.assertTrue(s2.compareTo(s1) >= 0);
        // }
    }

    @Test
    public void testGetCollectionEventCollection() throws Exception {
        String name = "testGetProcessingEventCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        List<CollectionEventWrapper> shipments = createCollectionEvents(site);

        // FIXME
        // List<CollectionEventWrapper> savedShipments = site
        // .getCollectionEventCollection(false);
        // Assert.assertTrue(savedShipments.containsAll(shipments));
    }

    @Test
    public void testGetCollectionEventCountForSite() throws Exception {
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
        PatientWrapper patient3 = PatientHelper
            .addPatient(name + "_p3", study1);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        // FIXME
        // CollectionEventWrapper cevent1 = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // method,
        // SourceVesselHelper.newSourceVessel(patient1,
        // Utils.getRandomDate(), 0.1),
        // SourceVesselHelper.newSourceVessel(patient3,
        // Utils.getRandomDate(), 0.1));
        // CollectionEventHelper.addCollectionEvent(site, method,
        // SourceVesselHelper.newSourceVessel(patient2, Utils.getRandomDate(),
        // 0.1), SourceVesselHelper.newSourceVessel(patient3,
        // Utils.getRandomDate(), 0.1));
        //
        // site.reload();
        // Assert.assertEquals(2, site.getCollectionEventCount());
        //
        // // delete cevent 1
        // DbHelper.deleteFromList(cevent1.getSourceVesselCollection(false));
        // cevent1.delete();
        // site.reload();
        // Assert.assertEquals(1, site.getCollectionEventCount());
        //
        // // add cevent again
        // cevent1 = CollectionEventHelper.addCollectionEvent(site, method,
        // SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
        // 0.1), SourceVesselHelper.newSourceVessel(patient3,
        // Utils.getRandomDate(), 0.1));
        //
        // site.reload();
        // Assert.assertEquals(2, site.getCollectionEventCount());
    }

    @Test
    public void testGetPatientCountForSite() throws Exception {
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
        PatientWrapper patient3 = PatientHelper
            .addPatient(name + "_p3", study1);

        List<CollectionEventWrapper> cevents = new ArrayList<CollectionEventWrapper>();
        List<ProcessingEventWrapper> pevents = new ArrayList<ProcessingEventWrapper>();
        for (PatientWrapper p : Arrays.asList(patient1, patient2, patient3)) {
            CollectionEventWrapper cevent = CollectionEventHelper
                .addCollectionEvent(site, p, 1, OriginInfoHelper
                    .addOriginInfo(site), SpecimenHelper
                    .newSpecimen(SpecimenTypeHelper.addSpecimenType(Utils
                        .getRandomNumericString(10))), SpecimenHelper
                    .newSpecimen(SpecimenTypeHelper.addSpecimenType(Utils
                        .getRandomNumericString(10))));
            cevents.add(cevent);

            ProcessingEventWrapper pevent = ProcessingEventHelper
                .addProcessingEvent(site, p, Utils.getRandomDate());
            pevent.addToSpecimenCollection(cevent
                .getOriginalSpecimenCollection(false));
            pevent.persist();
            pevents.add(pevent);
        }

        site.reload();
        Assert.assertEquals(3, site.getPatientCount().longValue());

        // delete patient 1
        patient1.reload();
        for (CollectionEventWrapper ce : patient1
            .getCollectionEventCollection(false)) {
            for (SpecimenWrapper sp : ce.getOriginalSpecimenCollection(false))
                sp.delete();
        }
        DbHelper.deleteFromList(patient1.getProcessingEventCollection());
        DbHelper.deleteFromList(patient1.getCollectionEventCollection(false));
        patient1.delete();

        site.reload();
        Assert.assertEquals(2, site.getPatientCount().longValue());
    }

    @Test
    public void testGetProcessingEventCountForSite() throws Exception {
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
        PatientWrapper patient3 = PatientHelper
            .addPatient(name + "_p3", study1);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        // FIXME
        // CollectionEventHelper.addCollectionEvent(site, method,
        // SourceVesselHelper.newSourceVessel(patient1, Utils.getRandomDate(),
        // 0.1), SourceVesselHelper.newSourceVessel(patient2,
        // Utils.getRandomDate(), 0.1));

        // FIXME
        // cevent1 has processing events for patient1 and patient3
        // int nber = ProcessingEventHelper.addProcessingEvents(site, patient1)
        // .size();
        // int nber2 = ProcessingEventHelper.addProcessingEvents(site, patient3)
        // .size();
        //
        // // cevent 2 has processing events for patient1 and patient2
        // int nber3 = ProcessingEventHelper.addProcessingEvents(site, patient1)
        // .size();
        // int nber4 = ProcessingEventHelper.addProcessingEvents(site, patient2)
        // .size();
        //
        // site.reload();
        // Assert.assertEquals(nber + nber2 + nber3 + nber4,
        // site.getProcessingEventCount());
        //
        // // delete patient 1 visits
        // patient1.reload();
        // for (ProcessingEventWrapper visit : patient1
        // .getProcessingEventCollection(false)) {
        // visit.delete();
        // }
        // site.reload();
        // Assert.assertEquals(nber2 + nber4, site.getProcessingEventCount());
    }

    @Test
    public void testGetSpecimenCountForSite() throws Exception {
        String name = "testGetSpecimenCountForSite" + r.nextInt();
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

        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        ContainerTypeWrapper ctype = ContainerTypeHelper.addContainerType(site,
            "Pallet96", "P96", 2, 8, 12, true);
        ctype.addToSpecimenTypeCollection(allSampleTypes);
        ctype.persist();

        ContainerWrapper container = ContainerHelper.addContainer("01", "01",
            null, site, ctype);

        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper
            .addPatient(name + "_p2", study2);

        // FIXME
        // cevent 1 has processing events for patient1 and patient2
        // int nber = ProcessingEventHelper.addProcessingEvents(site, patient1,
        // 10, 24, false).size();
        // int nber2 = ProcessingEventHelper.addProcessingEvents(site, patient2,
        // 10, 24, false).size();
        //
        // // add 2 samples to each processing event
        // //
        // // make sure we do not exceed 96 samples since that is all container
        // // type can hold
        // patient1.reload();
        // patient2.reload();
        // int sampleTypeCount = allSampleTypes.size();
        // int sampleCount = 0;
        // for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
        // for (ProcessingEventWrapper visit : patient
        // .getProcessingEventCollection(false)) {
        // for (int i = 0; i < 2; ++i) {
        // SpecimenHelper.addAliquot(
        // allSampleTypes.get(r.nextInt(sampleTypeCount)),
        // container, visit, sampleCount / 12, sampleCount % 12);
        // ++sampleCount;
        // }
        // }
        // }
        //
        // site.reload();
        // Assert.assertEquals(2 * (nber + nber2), site.getSpecimenCount()
        // .longValue());
        //
        // // delete patient 1 and all it's visits and samples
        // for (ProcessingEventWrapper visit : patient1
        // .getProcessingEventCollection(false)) {
        // for (SpecimenWrapper aliquot : visit.getSpecimenCollection(false)) {
        // aliquot.delete();
        // }
        // visit.delete();
        // }
        // patient1.delete();
        //
        // site.reload();
        // Assert.assertEquals(2 * nber2, site.getSpecimenCount().longValue());
    }
}
