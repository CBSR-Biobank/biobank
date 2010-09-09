package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ClinicShipmentHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
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
            site.removeStudies(new ArrayList<StudyWrapper>());
            Assert.assertTrue(true);
        } catch (BiobankCheckException e) {
            Assert.fail("cannot call removeStudies with empty list");
        }

        List<StudyWrapper> studySet1 = StudyHelper.addStudies(name + "_s1_",
            r.nextInt(10) + 1);
        site.addStudies(studySet1);
        site.persist();
        site.reload();
        List<StudyWrapper> siteStudies = site.getStudyCollection();

        Assert.assertEquals(studySet1.size(), siteStudies.size());

        // add another set
        List<StudyWrapper> studySet2 = StudyHelper.addStudies(name + "_s2_",
            r.nextInt(10) + 1);
        site.addStudies(studySet2);
        site.persist();
        site.reload();
        siteStudies = site.getStudyCollection();

        Assert.assertEquals(studySet1.size() + studySet2.size(),
            siteStudies.size());

        // remove studies
        site.removeStudies(studySet1);
        site.persist();
        site.reload();
        siteStudies = site.getStudyCollection();

        Assert.assertEquals(studySet2.size(), siteStudies.size());
        Assert.assertTrue(siteStudies.containsAll(studySet2));

        // try and remove studies that were already removed
        try {
            site.removeStudies(studySet1);
            Assert
                .fail("should not be allowed to remove a study that is not associated with site");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testNonAssocStudies() throws Exception {
        String name = "testGetStudyCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        try {
            site.removeStudies(new ArrayList<StudyWrapper>());
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
        site.addStudies(studySet1);
        site.persist();
        site.reload();
        List<StudyWrapper> siteNonAssocStudies = site.getStudiesNotAssoc();

        Assert.assertEquals(studySet2.size(), siteNonAssocStudies.size());

        // remove set 1 and add set 2
        site.removeStudies(studySet1);
        site.addStudies(studySet2);
        site.persist();
        site.reload();
        siteNonAssocStudies = site.getStudiesNotAssoc();

        Assert.assertEquals(studySet1.size(), siteNonAssocStudies.size());

        // add set 1 again
        site.addStudies(studySet1);
        site.persist();
        site.reload();
        siteNonAssocStudies = site.getStudiesNotAssoc();

        Assert.assertEquals(0, siteNonAssocStudies.size());
    }

    @Test
    public void testGetStudyCollectionSorted() throws Exception {
        String name = "testGetStudyCollectionSorted" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyHelper.addStudies(name, r.nextInt(15) + 5);

        List<StudyWrapper> studies = StudyWrapper.getAllStudies(appService);
        site.addStudies(studies);
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
    public void testAddStudies() throws Exception {
        String name = "testAddStudies" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyHelper.addStudies(name, r.nextInt(15) + 1);

        List<StudyWrapper> studies = StudyWrapper.getAllStudies(appService);
        int studiesNber = studies.size();
        site.addStudies(studies);
        site.persist();
        site.reload();

        Assert.assertEquals(studiesNber, site.getStudyCollection().size());

        // add one more study
        StudyWrapper newStudy = StudyHelper.addStudy(name + "newStudy");
        site.addStudies(Arrays.asList(newStudy));
        site.persist();
        site.reload();
        Assert.assertEquals(studiesNber + 1, site.getStudyCollection().size());
    }

    @Test
    public void testRemoveStudies() throws Exception {
        String name = "testRemoveStudies" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyHelper.addStudies(name, r.nextInt(15) + 1);

        List<StudyWrapper> studies = StudyWrapper.getAllStudies(appService);
        int studiesNber = studies.size();
        site.addStudies(studies);
        site.persist();
        site.reload();

        Assert.assertEquals(studiesNber, site.getStudyCollection().size());

        // remove one study
        StudyWrapper newStudy = studies.get(0);
        site.removeStudies(Arrays.asList(newStudy));
        site.persist();
        site.reload();
        Assert.assertEquals(studiesNber - 1, site.getStudyCollection().size());
    }

    @Test
    public void testGetContainerTypeCollection() throws Exception {
        String name = "testGetContainerTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = r.nextInt(15) + 1;
        ContainerTypeHelper.addContainerTypesRandom(site, name, nber);

        List<ContainerTypeWrapper> types = site.getContainerTypeCollection();
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
            name + "newType", name, null, 5, 4, false);
        site.addContainerTypes(Arrays.asList(type));
        site.persist();

        site.reload();
        // one type added
        Assert.assertEquals(nber + 1, site.getContainerTypeCollection().size());
    }

    @Test
    public void testGetContainerCollection() throws Exception {
        String name = "testGetContainerCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        // int totalContainers = ContainerHelper.addTopContainersWithChildren(
        // site, name, r.nextInt(3) + 1);

        int totalContainers = ContainerHelper.addTopContainersWithChildren(
            site, name, 1);

        List<ContainerWrapper> containers = site.getContainerCollection();
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
        ContainerWrapper container = ContainerHelper.newContainer(null, name
            + "newContainer", null, site, type);
        site.addContainers(Arrays.asList(container));
        site.persist();

        site.reload();
        // one container added
        Assert.assertEquals(totalContainers + 1, site.getContainerCollection()
            .size());
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
        } catch (BiobankCheckException bce) {
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
        } catch (BiobankCheckException bce) {
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
        } catch (BiobankCheckException bce) {
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

        List<ContainerWrapper> containers = site.getTopContainerCollection();
        Assert.assertEquals(topNber, containers.size());

        // clear the top containers and get again
        site.clearTopContainerCollection();
        containers = site.getTopContainerCollection();
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

    private List<ClinicShipmentWrapper> createShipments(SiteWrapper site)
        throws Exception {
        String name = site.getName();

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");

        List<ClinicShipmentWrapper> shipments = new ArrayList<ClinicShipmentWrapper>();
        shipments.add(ClinicShipmentHelper.addShipmentWithRandomPatient(site,
            clinic1, name + "Study1"));
        shipments.add(ClinicShipmentHelper.addShipmentWithRandomPatient(site,
            clinic1, name + "Study2"));
        shipments.add(ClinicShipmentHelper.addShipmentWithRandomPatient(site,
            clinic2, name + "Study3"));
        shipments.add(ClinicShipmentHelper.addShipmentWithRandomPatient(site,
            clinic2, name + "Study4"));

        site.reload();
        return shipments;
    }

    @Test
    public void testGetShipmentCollectionSorted() throws Exception {
        String name = "testGetShipmentCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        List<ClinicShipmentWrapper> shipments = createShipments(site);

        List<ClinicShipmentWrapper> savedShipments = site
            .getShipmentCollection(true);
        Assert.assertTrue(savedShipments.size() > 1);
        Assert.assertEquals(shipments.size(), savedShipments.size());
        for (int i = 0, n = savedShipments.size() - 1; i < n; i++) {
            ClinicShipmentWrapper s1 = savedShipments.get(i);
            ClinicShipmentWrapper s2 = savedShipments.get(i + 1);
            Assert.assertTrue(s1.compareTo(s2) <= 0);
            Assert.assertTrue(s2.compareTo(s1) >= 0);
        }
    }

    @Test
    public void testGetShipmentCollection() throws Exception {
        String name = "testGetPatientVisitCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        List<ClinicShipmentWrapper> shipments = createShipments(site);

        List<ClinicShipmentWrapper> savedShipments = site
            .getShipmentCollection(false);
        Assert.assertTrue(savedShipments.containsAll(shipments));
    }

    @Test
    public void testGetShipmentCountForSite() throws Exception {
        String name = "testGetPatientVisitCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addContacts(Arrays.asList(contact1, contact2));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addContacts(Arrays.asList(contact2));
        study2.persist();

        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper
            .addPatient(name + "_p2", study2);
        PatientWrapper patient3 = PatientHelper
            .addPatient(name + "_p3", study1);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic1, method, patient1, patient3);
        ClinicShipmentHelper.addShipment(site, clinic2, method, patient2,
            patient3);

        site.reload();
        Assert.assertEquals(2, site.getShipmentCount().longValue());

        // delete shipment 1
        shipment1.delete();
        Assert.assertEquals(1, site.getShipmentCount().longValue());

        // add shipment again
        shipment1 = ClinicShipmentHelper.addShipment(site, clinic1, method,
            patient3);

        site.reload();
        Assert.assertEquals(2, site.getShipmentCount().longValue());
    }

    @Test
    public void testGetPatientCountForSite() throws Exception {
        String name = "testGetPatientVisitCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addContacts(Arrays.asList(contact1, contact2));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addContacts(Arrays.asList(contact2));
        study2.persist();

        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper
            .addPatient(name + "_p2", study2);
        PatientWrapper patient3 = PatientHelper
            .addPatient(name + "_p3", study1);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic1, method, patient1, patient3);
        ClinicShipmentHelper.addShipment(site, clinic2, method, patient2,
            patient3);

        site.reload();
        Assert.assertEquals(3, site.getPatientCount().longValue());

        // delete patient 1
        shipment1.delete();
        patient1.reload();
        patient1.delete();

        shipment1 = ClinicShipmentHelper.addShipment(site, clinic1, method,
            patient3);

        site.reload();
        Assert.assertEquals(2, site.getPatientCount().longValue());
    }

    @Test
    public void testGetPatientVisitCountForSite() throws Exception {
        String name = "testGetPatientVisitCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addContacts(Arrays.asList(contact1, contact2));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addContacts(Arrays.asList(contact2));
        study2.persist();

        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper
            .addPatient(name + "_p2", study2);
        PatientWrapper patient3 = PatientHelper
            .addPatient(name + "_p3", study1);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic1, method, patient1, patient3);
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site, clinic2, method, patient1, patient2);

        // shipment1 has patient visits for patient1 and patient3
        int nber = PatientVisitHelper.addPatientVisits(patient1, shipment1)
            .size();
        int nber2 = PatientVisitHelper.addPatientVisits(patient3, shipment1)
            .size();

        // shipment 2 has patient visits for patient1 and patient2
        int nber3 = PatientVisitHelper.addPatientVisits(patient1, shipment2)
            .size();
        int nber4 = PatientVisitHelper.addPatientVisits(patient2, shipment2)
            .size();

        site.reload();
        Assert.assertEquals(nber + nber2 + nber3 + nber4, site
            .getPatientVisitCount().longValue());

        // delete patient 1 visits
        patient1.reload();
        for (PatientVisitWrapper visit : patient1.getPatientVisitCollection()) {
            visit.delete();
        }
        site.reload();
        Assert.assertEquals(nber2 + nber4, site.getPatientVisitCount()
            .longValue());
    }

    @Test
    public void testGetAliquotCountForSite() throws Exception {
        String name = "testGetAliquotCountForSite" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addContacts(Arrays.asList(contact1, contact2));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addContacts(Arrays.asList(contact2));
        study2.persist();

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getAllSampleTypes(appService, true);
        ContainerTypeWrapper ctype = ContainerTypeHelper.addContainerType(site,
            "Pallet96", "P96", 2, 8, 12, true);
        ctype.addSampleTypes(allSampleTypes);
        ctype.persist();

        ContainerWrapper container = ContainerHelper.addContainer("01", "01",
            null, site, ctype);

        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper
            .addPatient(name + "_p2", study2);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic1, method, patient1);

        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site, clinic2, method, patient2);

        // shipment 1 has patient visits for patient1 and patient2
        int nber = PatientVisitHelper.addPatientVisits(patient1, shipment1, 10,
            24).size();
        int nber2 = PatientVisitHelper.addPatientVisits(patient2, shipment2,
            10, 24).size();

        // add 2 samples to each patient visit
        //
        // make sure we do not exceed 96 samples since that is all container
        // type can hold
        patient1.reload();
        patient2.reload();
        int sampleTypeCount = allSampleTypes.size();
        int sampleCount = 0;
        for (PatientWrapper patient : Arrays.asList(patient1, patient2)) {
            for (PatientVisitWrapper visit : patient
                .getPatientVisitCollection()) {
                for (int i = 0; i < 2; ++i) {
                    AliquotHelper.addAliquot(
                        allSampleTypes.get(r.nextInt(sampleTypeCount)),
                        container, visit, sampleCount / 12, sampleCount % 12);
                    ++sampleCount;
                }
            }
        }

        site.reload();
        Assert.assertEquals(2 * (nber + nber2), site.getAliquotCount()
            .longValue());

        // delete patient 1 and all it's visits and samples
        for (PatientVisitWrapper visit : patient1.getPatientVisitCollection()) {
            for (AliquotWrapper aliquot : visit.getAliquotCollection()) {
                aliquot.delete();
            }
            visit.delete();
        }
        shipment1.delete();
        patient1.delete();

        site.reload();
        Assert.assertEquals(2 * nber2, site.getAliquotCount().longValue());
    }

    @Test
    public void testAddDispatchStudies() throws Exception {
        String name = "testGetDispatchStudies" + r.nextInt();
        SiteWrapper srcSite = SiteHelper.addSite(name);
        List<SiteWrapper> destSites = SiteHelper.addSites(name + "_dest_",
            r.nextInt(5) + 1);
        List<StudyWrapper> studies = StudyHelper.addStudies(name,
            r.nextInt(5) + 1);
        srcSite.reload();
        for (StudyWrapper study : studies) {
            srcSite.addStudyDispatchSites(study, destSites);
        }
        srcSite.persist();
        srcSite.reload();

        List<StudyWrapper> siteDispatchStudies = srcSite.getDispatchStudies();
        Assert.assertNotNull(siteDispatchStudies);
        Assert.assertEquals(studies.size(), siteDispatchStudies.size());
        Assert.assertTrue(siteDispatchStudies.containsAll(studies));

        // remove studies
        for (StudyWrapper study : studies) {
            srcSite.removeStudyDispatchSites(study, destSites);
        }
        srcSite.persist();
        srcSite.reload();
        siteDispatchStudies = srcSite.getDispatchStudies();
        Assert.assertNotNull(siteDispatchStudies);
        Assert.assertEquals(0, siteDispatchStudies.size());

        // now add all studies again and remove one by one
        for (StudyWrapper study : studies) {
            srcSite.addStudyDispatchSites(study, destSites);
        }
        srcSite.persist();
        srcSite.reload();
        int count = 0;
        for (StudyWrapper study : studies) {
            srcSite.removeStudyDispatchSites(study, destSites);
            ++count;

            srcSite.persist();
            srcSite.reload();
            siteDispatchStudies = srcSite.getDispatchStudies();
            Assert.assertNotNull(siteDispatchStudies);
            Assert.assertEquals(studies.size() - count,
                siteDispatchStudies.size());
        }
    }

    @Test
    public void testAddDispatchSites() throws Exception {
        String name = "testAddDispatchSites" + r.nextInt();
        SiteWrapper srcSite = SiteHelper.addSite(name);
        List<SiteWrapper> destSitesSet1 = SiteHelper.addSites(name + "_s1_",
            r.nextInt(5) + 1);
        List<SiteWrapper> destSitesSet2 = SiteHelper.addSites(name + "_s2_",
            r.nextInt(5) + 1);

        StudyWrapper study = StudyHelper.addStudy(name);

        // add dest site set 1
        srcSite.addStudyDispatchSites(study, destSitesSet1);
        List<SiteWrapper> srcSiteDispatchSites = srcSite
            .getStudyDispachSites(study);
        srcSite.persist();
        srcSite.reload();
        srcSiteDispatchSites = srcSite.getStudyDispachSites(study);
        Assert.assertNotNull(srcSiteDispatchSites);
        Assert.assertEquals(destSitesSet1.size(), srcSiteDispatchSites.size());
        Assert.assertTrue(srcSiteDispatchSites.containsAll(destSitesSet1));

        // add dest site set 2
        srcSite.addStudyDispatchSites(study, destSitesSet2);
        srcSite.persist();
        srcSite.reload();
        srcSiteDispatchSites = srcSite.getStudyDispachSites(study);

        Assert.assertNotNull(srcSiteDispatchSites);
        Assert.assertEquals(destSitesSet1.size() + destSitesSet2.size(),
            srcSiteDispatchSites.size());
        Assert.assertTrue(srcSiteDispatchSites.containsAll(destSitesSet1));
        Assert.assertTrue(srcSiteDispatchSites.containsAll(destSitesSet2));

        // remove set 1
        srcSite.removeStudyDispatchSites(study, destSitesSet1);
        srcSite.persist();
        srcSite.reload();
        srcSiteDispatchSites = srcSite.getStudyDispachSites(study);

        Assert.assertNotNull(srcSiteDispatchSites);
        Assert.assertEquals(destSitesSet2.size(), srcSiteDispatchSites.size());
        Assert.assertTrue(srcSiteDispatchSites.containsAll(destSitesSet2));

        // remove set 2
        srcSite.removeStudyDispatchSites(study, destSitesSet2);
        srcSite.persist();
        srcSite.reload();
        srcSiteDispatchSites = srcSite.getStudyDispachSites(study);

        Assert.assertTrue((srcSiteDispatchSites == null)
            || (srcSiteDispatchSites.size() == 0));
    }

}
