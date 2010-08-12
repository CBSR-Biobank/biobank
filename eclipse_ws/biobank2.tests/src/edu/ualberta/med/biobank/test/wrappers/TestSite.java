package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.AliquotHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentHelper;
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
    public void testGetWrappedClass() throws Exception {
        String name = "testGetWrappedClass" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        Assert.assertEquals(Site.class, site.getWrappedClass());
    }

    @Test
    public void testGetClinicCollection() throws Exception {
        String name = "testGetClinicCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int clinicsNber = r.nextInt(15) + 1;
        ClinicHelper.addClinics(site, name, clinicsNber);

        List<ClinicWrapper> clinics = site.getClinicCollection();
        int sizeFound = clinics.size();

        Assert.assertEquals(clinicsNber, sizeFound);
    }

    @Test
    public void testGetClinicCollectionBoolean() throws Exception {
        String name = "testGetClinicCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = r.nextInt(15) + 5;
        ClinicHelper.addClinics(site, name, nber);

        List<ClinicWrapper> clinics = site.getClinicCollection(true);
        if (clinics.size() > 1) {
            for (int i = 0; i < clinics.size() - 1; i++) {
                ClinicWrapper clinic1 = clinics.get(i);
                ClinicWrapper clinic2 = clinics.get(i + 1);
                Assert.assertTrue(clinic1.compareTo(clinic2) <= 0);
            }
        }
    }

    @Test
    public void testAddClinics() throws Exception {
        String name = "testAddClinics" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = r.nextInt(15) + 1;
        ClinicHelper.addClinics(site, name, nber);

        ClinicWrapper clinic = ClinicHelper.newClinic(site, name + "newClinic");
        site.addClinics(Arrays.asList(clinic));
        site.persist();

        site.reload();
        // one clinic added
        Assert.assertEquals(nber + 1, site.getClinicCollection().size());
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
    public void testGetContainerTypeCollectionBoolean() throws Exception {
        String name = "testGetContainerTypeCollectionBoolean" + r.nextInt();
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
        site.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));

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

        site.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
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
    public void testDeleteFailNoMoreClinic() throws Exception {
        int oldTotal = SiteWrapper.getSites(appService).size();
        String name = "testDeleteFailNoMoreClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name, false);

        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        site.reload();

        try {
            site.delete();
            Assert.fail("Should not delete the site : a clinic is still there");
        } catch (BiobankCheckException bce) {
            Assert.assertEquals(oldTotal + 1, SiteWrapper.getSites(appService)
                .size());
        }
        clinic.delete();
        site.reload();
        site.delete();
        Assert.assertEquals(oldTotal, SiteWrapper.getSites(appService).size());
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
        int sizeFound = containers.size();

        Assert.assertEquals(topNber, sizeFound);
    }

    @Test
    public void testGetTopContainerCollectionBoolean() throws Exception {
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
    }

    @Test
    public void testGetPatientVisitCountForSite() throws Exception {
        String name = "testGetPatientVisitCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(site, name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2");
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

        ShipmentWrapper shipment1 = ShipmentHelper.addShipment(site, clinic1,
            patient1, patient3);
        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(site, clinic2,
            patient1, patient2);

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

        ClinicWrapper clinic1 = ClinicHelper.addClinic(site, name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2");
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

        ShipmentWrapper shipment1 = ShipmentHelper.addShipment(site, clinic1,
            patient1);

        ShipmentWrapper shipment2 = ShipmentHelper.addShipment(site, clinic2,
            patient2);

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

}
