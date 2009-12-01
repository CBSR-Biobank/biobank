package test.ualberta.med.biobank;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContainerHelper;
import test.ualberta.med.biobank.internal.ContainerTypeHelper;
import test.ualberta.med.biobank.internal.DbHelper;
import test.ualberta.med.biobank.internal.SampleTypeHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TestSite extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGettersAndSetters"
            + r.nextInt());
        testGettersAndSetters(site);
    }

    @Test
    public void testGetStudyCollection() throws Exception {
        String name = "testGetStudyCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int studiesNber = r.nextInt(15) + 1;
        StudyHelper.addStudies(site, name, studiesNber);

        List<StudyWrapper> studies = site.getStudyCollection();
        int sizeFound = studies.size();

        Assert.assertEquals(studiesNber, sizeFound);
    }

    @Test
    public void testGetStudyCollectionBoolean() throws Exception {
        String name = "testGetStudyCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyHelper.addStudies(site, name, r.nextInt(15) + 1);

        List<StudyWrapper> studiesSorted = site.getStudyCollection(true);
        if (studiesSorted.size() > 1) {
            for (int i = 0; i < studiesSorted.size() - 1; i++) {
                StudyWrapper study1 = studiesSorted.get(i);
                StudyWrapper study2 = studiesSorted.get(i + 1);
                Assert.assertTrue(study1.compareTo(study2) <= 0);
            }
        }
    }

    @Test
    public void testAddInStudyCollection() throws Exception {
        String name = "testGetStudyCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int studiesNber = r.nextInt(15) + 1;
        StudyHelper.addStudies(site, name, studiesNber);

        List<StudyWrapper> studies = site.getStudyCollection();
        StudyWrapper study = StudyHelper.newStudy(site, name + "newStudy");
        studies.add(study);
        site.setStudyCollection(studies);
        site.persist();

        site.reload();
        // one study added
        Assert.assertEquals(studiesNber + 1, site.getStudyCollection().size());
    }

    @Test
    public void testRemoveInStudyCollection() throws Exception {
        String name = "testRemoveInStudyCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int studiesNber = r.nextInt(15) + 1;
        StudyHelper.addStudies(site, name, studiesNber);

        List<StudyWrapper> studies = site.getStudyCollection();
        StudyWrapper study = DbHelper.chooseRandomlyInList(studies);
        int idStudy = study.getId();
        studies.remove(study);
        site.setStudyCollection(studies);
        study.delete();
        site.persist();

        site.reload();
        // one study removed
        Assert.assertEquals(studiesNber - 1, site.getStudyCollection().size());

        // study should not be anymore in the study collection (removed the
        // good one)
        for (StudyWrapper s : site.getStudyCollection()) {
            Assert.assertFalse(s.getId().equals(idStudy));
        }
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
        int nber = r.nextInt(15) + 1;
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
    public void testAddInClinicCollection() throws Exception {
        String name = "testAddInClinicCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = r.nextInt(15) + 1;
        ClinicHelper.addClinics(site, name, nber);

        List<ClinicWrapper> clinics = site.getClinicCollection();
        ClinicWrapper clinic = ClinicHelper.newClinic(site, name + "newClinic");
        clinics.add(clinic);
        site.setClinicCollection(clinics);
        site.persist();

        site.reload();
        // one clinic added
        Assert.assertEquals(nber + 1, site.getClinicCollection().size());
    }

    @Test
    public void testRemoveInClinicCollection() throws Exception {
        String name = "testRemoveInClinicCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = r.nextInt(15) + 1;
        ClinicHelper.addClinics(site, name, nber);

        List<ClinicWrapper> clinics = site.getClinicCollection();
        ClinicWrapper clinic = DbHelper.chooseRandomlyInList(clinics);
        int idClinic = clinic.getId();
        clinics.remove(clinic);
        site.setClinicCollection(clinics);
        clinic.delete();
        site.persist();

        site.reload();
        // one clinic removed
        Assert.assertEquals(nber - 1, site.getClinicCollection().size());

        // clinic should not be anymore in the clinic collection (removed
        // the good one)
        for (ClinicWrapper c : site.getClinicCollection()) {
            Assert.assertFalse(c.getId().equals(idClinic));
        }
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
            r.nextInt(15) + 1);

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
    public void testAddInContainerTypeCollection() throws Exception {
        String name = "testAddInContainerTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = r.nextInt(15) + 1;
        ContainerTypeHelper.addContainerTypesRandom(site, name, nber);

        List<ContainerTypeWrapper> types = site.getContainerTypeCollection();
        ContainerTypeWrapper type = ContainerTypeHelper.newContainerType(site,
            name + "newType", name, null, 5, 4, false);
        types.add(type);
        site.setContainerTypeCollection(types);
        site.persist();

        site.reload();
        // one type added
        Assert.assertEquals(nber + 1, site.getContainerTypeCollection().size());
    }

    @Test
    public void testRemoveInContainerTypeCollection() throws Exception {
        String name = "testRemoveInContainerTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = r.nextInt(15) + 1;
        ContainerTypeHelper.addContainerTypesRandom(site, name, nber);

        List<ContainerTypeWrapper> types = site.getContainerTypeCollection();
        ContainerTypeWrapper type = DbHelper.chooseRandomlyInList(types);
        int idType = type.getId();
        types.remove(type);
        site.setContainerTypeCollection(types);
        type.delete();
        site.persist();

        site.reload();
        // one type removed
        Assert.assertEquals(nber - 1, site.getContainerTypeCollection().size());

        // type should not be anymore in the type collection (removed
        // the good one)
        for (ContainerTypeWrapper t : site.getContainerTypeCollection()) {
            Assert.assertFalse(t.getId().equals(idType));
        }
    }

    @Test
    public void testGetContainerCollection() throws Exception {
        String name = "testGetContainerCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int totalContainers = ContainerHelper.addTopContainersWithChildren(
            site, name, r.nextInt(3) + 1);

        List<ContainerWrapper> containers = site.getContainerCollection();
        int sizeFound = containers.size();

        Assert.assertEquals(totalContainers, sizeFound);
    }

    @Test
    public void testAddInContainerCollection() throws Exception {
        String name = "testAddInContainerCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int totalContainers = ContainerHelper.addTopContainersWithChildren(
            site, name, r.nextInt(3) + 1);

        List<ContainerWrapper> containers = site.getContainerCollection();
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            site, name);
        ContainerWrapper container = ContainerHelper.newContainer(null, name
            + "newContainer", null, site, type);
        containers.add(container);
        site.setContainerCollection(containers);
        site.persist();

        site.reload();
        // one container added
        Assert.assertEquals(totalContainers + 1, site.getContainerCollection()
            .size());
    }

    @Test
    public void testRemoveInContainerCollection() throws Exception {
        String name = "testRemoveInContainerCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int totalNber = ContainerHelper.addTopContainersWithChildren(site,
            name, r.nextInt(3) + 1);

        List<ContainerWrapper> containers = site.getContainerCollection();
        containers.removeAll(site.getTopContainerCollection());
        ContainerWrapper container = DbHelper.chooseRandomlyInList(containers);
        int idContainer = container.getId();
        containers.remove(container);
        site.setContainerCollection(containers);
        container.delete();
        site.persist();

        site.reload();
        // one container removed
        Assert
            .assertEquals(totalNber - 1, site.getContainerCollection().size());

        // container should not be anymore in the container collection
        // (removed the good one)
        for (ContainerWrapper c : site.getContainerCollection()) {
            Assert.assertFalse(c.getId().equals(idContainer));
        }
    }

    @Test
    public void testGetSampleTypeCollection() throws Exception {
        String name = "testGetSampleTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = SampleTypeHelper.addSampleTypes(site, name);

        List<SampleTypeWrapper> types = site.getSampleTypeCollection();
        int sizeFound = types.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetSampleTypeCollectionBoolean() throws Exception {
        String name = "testGetSampleTypeCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeHelper.addSampleTypes(site, name);

        List<SampleTypeWrapper> types = site.getSampleTypeCollection(true);
        if (types.size() > 1) {
            for (int i = 0; i < types.size() - 1; i++) {
                SampleTypeWrapper type1 = types.get(i);
                SampleTypeWrapper type2 = types.get(i + 1);
                Assert.assertTrue(type1.compareTo(type2) <= 0);
            }
        }
    }

    @Test
    public void testAddInSampleTypeCollection() throws Exception {
        String name = "testAddInSampleTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = SampleTypeHelper.addSampleTypes(site, name);

        List<SampleTypeWrapper> types = site.getSampleTypeCollection();
        SampleTypeWrapper type = SampleTypeHelper.newSampleType(site, name);
        types.add(type);
        site.setSampleTypeCollection(types);
        site.persist();

        site.reload();
        // one container added
        Assert.assertEquals(nber + 1, site.getSampleTypeCollection().size());
    }

    @Test
    public void testRemoveInSampleTypeCollection() throws Exception {
        String name = "testRemoveInSampleTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        int nber = SampleTypeHelper.addSampleTypes(site, name);

        List<SampleTypeWrapper> types = site.getSampleTypeCollection();
        SampleTypeWrapper type = DbHelper.chooseRandomlyInList(types);
        int idContainer = type.getId();
        types.remove(type);
        site.setSampleTypeCollection(types);
        type.delete();
        SampleTypeHelper.removeFromCreated(type);
        site.persist();

        site.reload();
        // one type removed
        Assert.assertEquals(nber - 1, site.getSampleTypeCollection().size());

        // type should not be anymore in the type collection
        // (removed the good one)
        for (SampleTypeWrapper t : site.getSampleTypeCollection()) {
            Assert.assertFalse(t.getId().equals(idContainer));
        }
    }

    @Test
    public void testPersist() throws Exception {
        int oldTotal = SiteWrapper.getAllSites(appService).size();
        SiteHelper.addSite("testPersist" + r.nextInt());
        int newTotal = SiteWrapper.getAllSites(appService).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFail() throws Exception {
        String name = "testPersistFail" + r.nextInt();
        SiteWrapper site = new SiteWrapper(appService);
        site.setName(name);

        try {
            site.persist();
            Assert.fail("Should not insert the site : no address");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        // really insert it this time:
        site = SiteHelper.addSite(name);
        site.persist();

        site = SiteHelper.newSite(name);
        try {
            site.persist();
            Assert
                .fail("Should not insert the site : same name already in database");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        SiteWrapper site = SiteHelper
            .addSite("testDelete" + r.nextInt(), false);

        // object is in database
        Site siteInDB = ModelUtils.getObjectWithId(appService, Site.class, site
            .getId());
        Assert.assertNotNull(siteInDB);

        site.delete();

        siteInDB = ModelUtils.getObjectWithId(appService, Site.class, site
            .getId());
        // object is not anymore in database
        Assert.assertNull(siteInDB);
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
    public void testSetPvInfoPossibleGlobal() throws Exception {
        String name = "testSetPvInfoPossibleGlobal" + r.nextInt();

        // TODO any way to get the types without a site for global ?
        SiteWrapper site = SiteHelper.addSite(name);
        String[] types = site.getPvInfoTypeNames();
        if (types.length == 0) {
            Assert.fail("Can't test without pvinfotypes");
        }

        String type = types[r.nextInt(types.length)];
        SiteWrapper.setGlobalPvInfoPossible(appService, name, type);
        // TODO any static method to get global pv info possible ?
        Assert.assertTrue(Arrays.asList(site.getPvInfoPossibleLabels())
            .contains(name));
        Assert.assertEquals(type, site.getPvInfoTypeName(name));

        String pvInfoName2 = name + "_2";
        SiteWrapper.setGlobalPvInfoPossible(appService, pvInfoName2, "toto");
        // TODO any static method to get global pv info possible ?
        Assert.assertFalse(Arrays.asList(site.getPvInfoPossibleLabels())
            .contains(pvInfoName2));
    }

    @Test
    public void testSetPvInfoPossible() throws Exception {
        String name = "testSetPvInfoPossible" + r.nextInt();

        SiteWrapper site = SiteHelper.addSite(name);

        SiteWrapper site2 = SiteHelper.addSite(name + "secondSite");
        String[] types = site2.getPvInfoTypeNames();
        if (types.length == 0) {
            Assert.fail("Can't test without pvinfotypes");
        }

        String type = types[r.nextInt(types.length)];
        site2.setPvInfoPossible(name, type);
        site2.persist();

        site2.reload();
        Assert.assertTrue(Arrays.asList(site2.getPvInfoPossibleLabels())
            .contains(name));
        Assert.assertEquals(type, site2.getPvInfoTypeName(name));
        Assert.assertFalse(Arrays.asList(site.getPvInfoPossibleLabels())
            .contains(name));
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
            r.nextInt(8) + 1);

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
        SiteHelper.addSites("testGetSites" + r.nextInt(), r.nextInt(15) + 1);

        List<SiteWrapper> siteWrappers = SiteWrapper.getSites(appService, null);
        int inDB = appService.search(Site.class, new Site()).size();
        Assert.assertEquals(inDB, siteWrappers.size());

        SiteWrapper site = DbHelper.chooseRandomlyInList(siteWrappers);
        siteWrappers = SiteWrapper.getSites(appService, site.getId());
        Assert.assertEquals(1, siteWrappers.size());

        HQLCriteria criteria = new HQLCriteria("select max(id) from "
            + Site.class.getName());
        List<Integer> max = appService.query(criteria);
        siteWrappers = SiteWrapper.getSites(appService, max.get(0) + 1000);
        Assert.assertEquals(0, siteWrappers.size());
    }
}
