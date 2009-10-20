package test.ualberta.med.biobank;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TestSite extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGettersAndSetters");
        testGettersAndSetters(site);
    }

    @Test
    public void testGetStudyCollection() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGetStudyCollection");
        int studiesNber = r.nextInt(15) + 1;
        StudyHelper.addStudies(site, "testGetStudyCollection", studiesNber);

        List<StudyWrapper> studies = site.getStudyCollection();
        int sizeFound = studies.size();

        Assert.assertEquals(studiesNber, sizeFound);
    }

    @Test
    public void testGetStudyCollectionBoolean() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGetStudyCollectionBoolean");
        StudyHelper.addStudies(site, "testGetStudyCollectionBoolean", r
            .nextInt(15) + 1);

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
        SiteWrapper site = SiteHelper.addSite("testAddInStudyCollection");
        int studiesNber = r.nextInt(15) + 1;
        StudyHelper.addStudies(site, "testAddInStudyCollection", studiesNber);

        List<StudyWrapper> studies = site.getStudyCollection();
        StudyWrapper study = new StudyWrapper(appService);
        study.setName("testAddInStudyCollection" + r.nextInt());
        study.setSite(site);
        studies.add(study);
        site.setStudyCollection(studies);
        site.persist();

        site.reload();
        // one study added
        Assert.assertEquals(studiesNber + 1, site.getStudyCollection().size());
    }

    @Test
    public void testRemoveInStudyCollection() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testRemoveInStudyCollection");
        int studiesNber = r.nextInt(15) + 1;
        StudyHelper
            .addStudies(site, "testRemoveInStudyCollection", studiesNber);

        List<StudyWrapper> studies = site.getStudyCollection();
        StudyWrapper study = chooseRandomlyInList(studies);
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
        SiteWrapper site = SiteHelper.addSite("testGetClinicCollection");
        int clinicsNber = r.nextInt(15) + 1;
        ClinicHelper.addClinics(site, "testGetClinicCollection", clinicsNber);

        List<ClinicWrapper> clinics = site.getClinicCollection();
        int sizeFound = clinics.size();

        Assert.assertEquals(clinicsNber, sizeFound);
    }

    @Test
    public void testGetClinicCollectionBoolean() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGetClinicCollectionBoolean");
        int nber = r.nextInt(15) + 1;
        ClinicHelper.addClinics(site, "testGetClinicCollectionBoolean", nber);

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
        SiteWrapper site = SiteHelper.addSite("testAddInClinicCollection");
        int nber = r.nextInt(15) + 1;
        ClinicHelper.addClinics(site, "testAddInClinicCollection", nber);

        List<ClinicWrapper> clinics = site.getClinicCollection();
        ClinicWrapper clinic = new ClinicWrapper(appService);
        clinic.setName("testAddInClinicCollection" + r.nextInt());
        clinic.setCity("");
        clinic.setSite(site);
        clinics.add(clinic);
        site.setClinicCollection(clinics);
        site.persist();

        site.reload();
        // one clinic added
        Assert.assertEquals(nber + 1, site.getClinicCollection().size());
    }

    @Test
    public void testRemoveInClinicCollection() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testRemoveInClinicCollection");
        int nber = r.nextInt(15) + 1;
        ClinicHelper.addClinics(site, "testRemoveInClinicCollection", nber);

        List<ClinicWrapper> clinics = site.getClinicCollection();
        ClinicWrapper clinic = chooseRandomlyInList(clinics);
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
        SiteWrapper site = SiteHelper.addSite("testGetContainerTypeCollection");
        int nber = addContainerTypesRandom(site,
            "testGetContainerTypeCollection");

        List<ContainerTypeWrapper> types = site.getContainerTypeCollection();
        int sizeFound = types.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetContainerTypeCollectionBoolean() throws Exception {
        SiteWrapper site = SiteHelper
            .addSite("testGetContainerTypeCollectionBoolean");
        addContainerTypesRandom(site, "testGetContainerTypeCollectionBoolean");

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
        SiteWrapper site = SiteHelper
            .addSite("testAddInContainerTypeCollection");
        int nber = addContainerTypesRandom(site,
            "testAddInContainerTypeCollection");

        List<ContainerTypeWrapper> types = site.getContainerTypeCollection();
        ContainerTypeWrapper type = new ContainerTypeWrapper(appService);
        type.setSite(site);
        type.setName("testAddInContainerTypeCollection" + r.nextInt());
        type.setRowCapacity(5);
        type.setColCapacity(4);
        types.add(type);
        site.setContainerTypeCollection(types);
        site.persist();

        site.reload();
        // one type added
        Assert.assertEquals(nber + 1, site.getContainerTypeCollection().size());
    }

    @Test
    public void testRemoveInContainerTypeCollection() throws Exception {
        SiteWrapper site = SiteHelper
            .addSite("testRemoveInContainerTypeCollection");
        int nber = addContainerTypesRandom(site,
            "testRemoveInContainerTypeCollection");

        List<ContainerTypeWrapper> types = site.getContainerTypeCollection();
        ContainerTypeWrapper type = chooseRandomlyInList(types);
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
        SiteWrapper site = SiteHelper.addSite("testGetContainerCollection");
        int nber = addContainersRandom(site, "testGetContainerCollection");

        List<ContainerWrapper> containers = site.getContainerCollection();
        int sizeFound = containers.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testAddInContainerCollection() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testAddInContainerCollection");
        int nber = addContainersRandom(site, "testAddInContainerCollection");

        List<ContainerWrapper> containers = site.getContainerCollection();
        ContainerWrapper container = new ContainerWrapper(appService);
        container.setLabel("testAddInContainerCollection" + r.nextInt());
        ContainerTypeWrapper type = addContainerTypeRandom(site,
            "testAddInContainerCollection");
        container.setContainerType(type);
        container.setSite(site);
        containers.add(container);
        site.setContainerCollection(containers);
        site.persist();

        site.reload();
        // one container added
        Assert.assertEquals(nber + 1, site.getContainerCollection().size());
    }

    @Test
    public void testRemoveInContainerCollection() throws Exception {
        SiteWrapper site = SiteHelper
            .addSite("testRemoveInContainerCollection");
        int nber = addContainersRandom(site, "testRemoveInContainerCollection");

        List<ContainerWrapper> containers = site.getContainerCollection();
        ContainerWrapper container = chooseRandomlyInList(containers);
        int idContainer = container.getId();
        containers.remove(container);
        site.setContainerCollection(containers);
        container.delete();
        site.persist();

        site.reload();
        // one container removed
        Assert.assertEquals(nber - 1, site.getContainerCollection().size());

        // container should not be anymore in the container collection
        // (removed the good one)
        for (ContainerWrapper c : site.getContainerCollection()) {
            Assert.assertFalse(c.getId().equals(idContainer));
        }
    }

    @Test
    public void testGetSampleTypeCollection() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGetSampleTypeCollection");
        int nber = addSampleTypes(site, "testGetSampleTypeCollection");

        List<SampleTypeWrapper> types = site.getSampleTypeCollection();
        int sizeFound = types.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetSampleTypeCollectionBoolean() throws Exception {
        SiteWrapper site = SiteHelper
            .addSite("testGetSampleTypeCollectionBoolean");
        addSampleTypes(site, "testGetSampleTypeCollectionBoolean");

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
        SiteWrapper site = SiteHelper.addSite("testAddInSampleTypeCollection");
        int nber = addSampleTypes(site, "testAddInSampleTypeCollection");

        List<SampleTypeWrapper> types = site.getSampleTypeCollection();
        SampleTypeWrapper type = new SampleTypeWrapper(appService);
        type.setName("testAddInSampleTypeCollection" + r.nextInt());
        type.setSite(site);
        types.add(type);
        site.setSampleTypeCollection(types);
        site.persist();

        site.reload();
        // one container added
        Assert.assertEquals(nber + 1, site.getSampleTypeCollection().size());
    }

    private SampleTypeWrapper addSampleType(SiteWrapper site, String name)
        throws Exception {
        SampleTypeWrapper type = new SampleTypeWrapper(appService);
        type.setName(name + "Random" + r.nextInt());
        type.setSite(site);
        type.persist();
        return type;
    }

    private int addSampleTypes(SiteWrapper site, String name) throws Exception {
        int nber = r.nextInt(15);
        for (int i = 0; i < nber; i++) {
            addSampleType(site, name);
        }
        site.reload();
        return nber;
    }

    @Test
    public void testRemoveInSampleTypeCollection() throws Exception {
        SiteWrapper site = SiteHelper
            .addSite("testRemoveInSampleTypeCollection");
        int nber = addSampleTypes(site, "testRemoveInSampleTypeCollection");

        List<SampleTypeWrapper> types = site.getSampleTypeCollection();
        SampleTypeWrapper type = chooseRandomlyInList(types);
        int idContainer = type.getId();
        types.remove(type);
        site.setSampleTypeCollection(types);
        type.delete();
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
    public void testAddSite() throws Exception {
        int oldTotal = SiteWrapper.getAllSites(appService).size();
        SiteHelper.addSite("testPersist");
        int newTotal = SiteWrapper.getAllSites(appService).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testDelete() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testDelete", false);
        // object is in database
        Assert.assertNotNull(site);
        site.delete();
        Site siteInDB = ModelUtils.getObjectWithId(appService, Site.class, site
            .getId());
        // object is not anymore in database
        Assert.assertNull(siteInDB);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testResetAlreadyInDatabase");
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
    public void testSetPvInfoPossible() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testSetPvInfoPossible");
        site.reload();

        String[] types = site.getPvInfoTypes();
        if (types.length == 0) {
            Assert.fail("Can't test without pvinfotypes");
        }
        String labelGlobal = "labelGlobal" + r.nextInt();
        String type = types[r.nextInt(types.length)];
        site.setPvInfoPossible(labelGlobal, type, true);
        site.persist();

        site.reload();
        boolean labelExists = findLabel(site, labelGlobal);
        Assert.assertTrue(labelExists);

        Assert.assertEquals(type, site.getPvInfoType(labelGlobal));

        SiteWrapper site2 = SiteHelper.addSite("SetPvInfoPossible");
        types = site2.getPvInfoTypes();
        if (types.length == 0) {
            Assert.fail("Can't test without pvinfotypes");
        }
        String labelSite = "labelSite" + r.nextInt();
        type = types[r.nextInt(types.length)];
        site2.setPvInfoPossible(labelSite, type, false);
        site2.persist();

        site2.reload();
        labelExists = findLabel(site2, labelSite);
        Assert.assertTrue(labelExists);

        Assert.assertEquals(type, site2.getPvInfoType(labelGlobal));

        labelExists = findLabel(site, labelSite);
        Assert.assertFalse(labelExists);
    }

    private boolean findLabel(SiteWrapper site, String label)
        throws ApplicationException {
        String[] labels = site.getPvInfoPossibleLabels();
        for (String l : labels) {
            if (l.equals(label)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testGetTopContainerCollection() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGetTopContainerCollection");
        addContainersRandom(site, "testGetTopContainerCollection");

        List<ContainerWrapper> containers = site.getTopContainerCollection();
        int sizeFound = containers.size();

        int expected = 0;
        for (ContainerWrapper container : site.getContainerCollection()) {
            if (Boolean.TRUE.equals(container.getContainerType().getTopLevel())) {
                expected++;
            }
        }

        Assert.assertEquals(expected, sizeFound);
    }

    @Test
    public void testGetTopContainerCollectionBoolean() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testGetTopContainerCollection");
        addContainersRandom(site, "testGetTopContainerCollection");

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
        SiteHelper.addSites("testGetSites", r.nextInt(15) + 1);

        List<SiteWrapper> siteWrappers = SiteWrapper.getSites(appService, null);
        int inDB = appService.search(Site.class, new Site()).size();
        Assert.assertEquals(inDB, siteWrappers.size());

        SiteWrapper site = chooseRandomlyInList(siteWrappers);
        siteWrappers = SiteWrapper.getSites(appService, site.getId());
        Assert.assertEquals(1, siteWrappers.size());

        HQLCriteria criteria = new HQLCriteria("select max(id) from "
            + Site.class.getName());
        List<Integer> max = appService.query(criteria);
        siteWrappers = SiteWrapper.getSites(appService, max.get(0) + 1000);
        Assert.assertEquals(0, siteWrappers.size());
    }

}
