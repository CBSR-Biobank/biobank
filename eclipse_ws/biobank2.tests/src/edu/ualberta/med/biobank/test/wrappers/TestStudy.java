package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudySourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.PvAttrTypeWrapper;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ClinicShipmentHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.PatientVisitHelper;
import edu.ualberta.med.biobank.test.internal.SampleStorageHelper;
import edu.ualberta.med.biobank.test.internal.SampleTypeHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceVesselHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import edu.ualberta.med.biobank.test.internal.StudySourceVesselHelper;

public class TestStudy extends TestDatabase {

    @Test
    public void testGetSiteCollection() throws Exception {
        String name = "testGetSiteCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        int sitesNber = r.nextInt(15) + 1;
        SiteHelper.addSites(name, sitesNber);

        SiteWrapper site = SiteHelper.addSite(name, false);
        List<SiteWrapper> sites = SiteWrapper.getSites(appService);
        study.addSites(sites);
        study.persist();
        study.reload();

        List<SiteWrapper> studySites = study.getSiteCollection();

        Assert.assertEquals(sites.size(), studySites.size());

        // delete a site
        sites.remove(site);
        site.delete();

        study.reload();
        studySites = study.getSiteCollection();
        Assert.assertEquals(sites.size(), studySites.size());
    }

    @Test
    public void testGetSiteCollectionSorted() throws Exception {
        String name = "testGetSiteCollectionSorted" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        SiteHelper.addSites(name, r.nextInt(15) + 5);

        List<SiteWrapper> sites = SiteWrapper.getSites(appService);
        study.addSites(sites);
        study.persist();
        study.reload();

        List<SiteWrapper> sitesSorted = study.getSiteCollection(true);
        Assert.assertTrue(sitesSorted.size() > 1);
        for (int i = 0, n = sitesSorted.size() - 1; i < n; i++) {
            SiteWrapper study1 = sitesSorted.get(i);
            SiteWrapper study2 = sitesSorted.get(i + 1);
            Assert.assertTrue(study1.compareTo(study2) <= 0);
        }
    }

    @Test
    public void testAddAndRemoveSites() throws Exception {
        String name = "testAddSites" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        SiteHelper.addSites(name, r.nextInt(15) + 1);

        List<SiteWrapper> sitesGroup1 = SiteWrapper.getSites(appService);
        int sitesCount1 = sitesGroup1.size();
        study.addSites(sitesGroup1);
        study.persist();
        study.reload();

        Assert.assertEquals(sitesCount1, study.getSiteCollection().size());

        // add more sites
        int sitesCount2 = r.nextInt(15) + 1;
        SiteHelper.addSites(name + "_G2", sitesCount2);

        List<SiteWrapper> sitesGroup2 = new ArrayList<SiteWrapper>();
        for (SiteWrapper site : SiteWrapper.getSites(appService)) {
            if (!sitesGroup1.contains(site)) {
                sitesGroup2.add(site);
            }
        }

        study.addSites(sitesGroup2);
        study.persist();
        study.reload();

        Assert.assertEquals(sitesCount1 + sitesCount2, study
            .getSiteCollection().size());

        study.removeSites(sitesGroup1);
        Assert.assertEquals(sitesCount2, study.getSiteCollection().size());

        study.removeSites(sitesGroup2);
        Assert.assertEquals(0, study.getSiteCollection().size());
    }

    private static List<PatientVisitWrapper> studyAddPatientVisits(
        StudyWrapper study) throws Exception {
        String name = study.getName();
        String randStr = Utils.getRandomString(5, 10);
        SiteWrapper site = SiteHelper.addSite("SITE_" + randStr);
        ClinicWrapper clinic = ClinicHelper.addClinic(name + "CLINIC_"
            + randStr);
        ContactWrapper contact = ContactHelper.addContact(clinic, name
            + "CONTACT1");
        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact);
        study.addContacts(contacts);
        study.persist();
        study.reload();
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        ClinicShipmentWrapper shipment = ClinicShipmentHelper.addShipment(site,
            clinic,
            ShippingMethodWrapper.getShippingMethods(appService).get(0),
            patient);
        return PatientVisitHelper.addPatientVisits(patient, shipment);

    }

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        testGettersAndSetters(study);
    }

    @Test
    public void testGetContactCollection() throws Exception {
        String name = "testGetContactCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = ContactHelper.addContactsToStudy(study, site, name);

        List<ContactWrapper> contacts = study.getContactCollection();
        int sizeFound = contacts.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetContactCollectionBoolean() throws Exception {
        String name = "testGetContactCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactHelper.addContactsToStudy(study, site, name);

        List<ContactWrapper> contacts = study.getContactCollection(true);
        if (contacts.size() > 1) {
            for (int i = 0; i < contacts.size() - 1; i++) {
                ContactWrapper contact1 = contacts.get(i);
                ContactWrapper contact2 = contacts.get(i + 1);
                Assert.assertTrue(contact1.compareTo(contact2) <= 0);
            }
        }
    }

    @Test
    public void testAddContacts() throws Exception {
        String name = "testAddContacts" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = ContactHelper.addContactsToStudy(study, site, name);
        site.reload();

        // get a clinic not yet added
        List<ContactWrapper> contacts = study.getContactCollection();
        List<ClinicWrapper> clinics = ClinicWrapper.getAllClinics(appService);
        for (ContactWrapper contact : contacts) {
            clinics.remove(contact.getClinic());
        }
        ClinicWrapper clinicNotAdded = DbHelper.chooseRandomlyInList(clinics);
        ContactWrapper contactToAdd = DbHelper
            .chooseRandomlyInList(clinicNotAdded.getContactCollection());
        study.addContacts(Arrays.asList(contactToAdd));
        study.persist();

        study.reload();
        // one contact added
        Assert.assertEquals(nber + 1, study.getContactCollection().size());
    }

    @Test
    public void testRemoveContacts() throws Exception {
        String name = "testRemoveContacts" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = ContactHelper.addContactsToStudy(study, site, name);
        site.reload();

        // get a clinic not yet added
        List<ContactWrapper> contacts = study.getContactCollection();
        ContactWrapper contact = DbHelper.chooseRandomlyInList(contacts);
        // don't have to delete contact because this is a *..* relation
        study.removeContacts(Arrays.asList(contact));
        study.persist();

        study.reload();
        // one contact added
        Assert.assertEquals(nber - 1, study.getContactCollection().size());
    }

    @Test
    public void testContactsNotAssoc() throws Exception {
        String name = "testContactsNotAssoc" + r.nextInt();
        StudyWrapper study1 = StudyHelper.addStudy(name);
        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");

        int totalBefore = ContactWrapper.getAllContacts(appService).size();
        ClinicWrapper clinic = ClinicHelper.addClinic("CL1");
        int newContactsCount = ContactHelper.addContactsToClinic(clinic,
            "CL1-CT", 5, 10);
        int totalContacts = totalBefore + newContactsCount;

        Assert.assertEquals(totalContacts, study1.getContactsNotAssoc().size());

        List<ContactWrapper> contacts = clinic.getContactCollection();
        Assert.assertNotNull(contacts);

        // associate all contacts with study1
        for (int i = 0; i < newContactsCount; ++i) {
            study1.addContacts(Arrays.asList(contacts.get(i)));
            study1.persist();
            study1.reload();
            Assert.assertEquals(totalContacts - i - 1, study1
                .getContactsNotAssoc().size());
        }

        // move all contacts to study2
        for (int i = 0; i < newContactsCount; ++i) {
            study1.removeContacts(Arrays.asList(contacts.get(i)));
            study1.persist();
            study1.reload();
            study2.addContacts(Arrays.asList(contacts.get(i)));
            study2.persist();
            study2.reload();
            Assert.assertEquals(totalBefore + i + 1, study1
                .getContactsNotAssoc().size());
        }

        // remove contacts one by one
        while (contacts.size() > 0) {
            ContactWrapper contact = contacts.get(0);
            contact.reload();
            study2.removeContacts(Arrays.asList(contact));
            study2.persist();
            study2.reload();
            contact.delete();
            contacts.remove(0);
            Assert.assertEquals(totalBefore + contacts.size(), study1
                .getContactsNotAssoc().size());
            contacts = clinic.getContactCollection();
        }
    }

    @Test
    public void testGetSampleStorageCollection() throws Exception {
        String name = "testGetSampleStorageCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = SampleStorageHelper.addSampleStorages(study, site, name);

        List<SampleStorageWrapper> storages = study
            .getSampleStorageCollection();
        int sizeFound = storages.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetSampleStorageCollectionBoolean() throws Exception {
        String name = "testGetSampleStorageCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        SampleStorageHelper.addSampleStorages(study, site, name);

        List<SampleStorageWrapper> storages = study
            .getSampleStorageCollection(true);
        if (storages.size() > 1) {
            for (int i = 0; i < storages.size() - 1; i++) {
                SampleStorageWrapper storage1 = storages.get(i);
                SampleStorageWrapper storage2 = storages.get(i + 1);
                Assert.assertTrue(storage1.compareTo(storage2) <= 0);
            }
        }
    }

    @Test
    public void testAddSampleStorages() throws Exception {
        String name = "testAddSampleStorages" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = SampleStorageHelper.addSampleStorages(study, site, name);

        SampleTypeWrapper type = SampleTypeHelper.addSampleType(name);
        SampleStorageWrapper newStorage = SampleStorageHelper.newSampleStorage(
            study, type);
        study.addSampleStorage(Arrays.asList(newStorage));
        study.persist();

        study.reload();
        // one storage added
        Assert
            .assertEquals(nber + 1, study.getSampleStorageCollection().size());
    }

    @Test
    public void testRemoveSampleStorages() throws Exception {
        String name = "testRemoveSampleStorages" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = SampleStorageHelper.addSampleStorages(study, site, name);

        List<SampleStorageWrapper> storages = study
            .getSampleStorageCollection();
        SampleStorageWrapper storage = DbHelper.chooseRandomlyInList(storages);
        study.removeSampleStorages(Arrays.asList(storage));
        study.persist();

        study.reload();
        // one storage added
        Assert
            .assertEquals(nber - 1, study.getSampleStorageCollection().size());
    }

    @Test
    public void testGetStudySourceVesselCollection() throws Exception {
        String name = "testGetStudySourceVesselCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = StudySourceVesselHelper.addStudySourceVessels(study, name);

        List<StudySourceVesselWrapper> storages = study
            .getStudySourceVesselCollection();
        int sizeFound = storages.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetStudySourceVesselCollectionBoolean() throws Exception {
        String name = "testGetStudySourceVesselCollectionBoolean" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        StudySourceVesselHelper.addStudySourceVessels(study, name);

        List<StudySourceVesselWrapper> sources = study
            .getStudySourceVesselCollection(true);
        if (sources.size() > 1) {
            for (int i = 0; i < sources.size() - 1; i++) {
                StudySourceVesselWrapper source1 = sources.get(i);
                StudySourceVesselWrapper source2 = sources.get(i + 1);
                Assert.assertTrue(source1.compareTo(source2) <= 0);
            }
        }
    }

    @Test
    public void testAddStudySourceVessels() throws Exception {
        String name = "testAddStudySourceVessels" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = StudySourceVesselHelper.addStudySourceVessels(study, name);

        SourceVesselWrapper sourceVessel = SourceVesselHelper
            .addSourceVessel(name);
        study.addStudySourceVessels(Arrays.asList(StudySourceVesselHelper
            .addStudySourceVessel(study, sourceVessel)));
        study.persist();

        study.reload();
        // one storage added
        Assert.assertEquals(nber + 1, study.getStudySourceVesselCollection()
            .size());
    }

    @Test
    public void testRemoveStudySourceVessels() throws Exception {
        String name = "testRemoveStudySourceVessels" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = StudySourceVesselHelper.addStudySourceVessels(study, name);

        List<StudySourceVesselWrapper> sources = study
            .getStudySourceVesselCollection();
        StudySourceVesselWrapper source = DbHelper
            .chooseRandomlyInList(sources);
        // don't have to delete the storage thanks to
        // deleteSourceVesselDifference method
        SourceVesselHelper.createdSourceVessels.remove(source);
        study.removeStudySourceVessels(Arrays.asList(source));
        study.persist();

        study.reload();
        // one storage added
        Assert.assertEquals(nber - 1, study.getStudySourceVesselCollection()
            .size());
    }

    @Test
    public void testSetStudyPvAttr() throws Exception {
        String name = "testSetStudyPvAttr" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        Collection<String> types = PvAttrTypeWrapper.getAllPvAttrTypesMap(
            appService).keySet();
        Assert.assertTrue(types.contains("text"));
        Assert.assertTrue(types.contains("select_single"));

        study.setStudyPvAttr("Worksheet", "text");
        study.setStudyPvAttr("Visit Type", "select_single", new String[] {
            "toto", "titi", "tata" });
        study.persist();
        study.reload();

        // set non existing type, expect exception
        try {
            study.setStudyPvAttr(Utils.getRandomString(10, 15),
                Utils.getRandomString(10, 15));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        Assert.assertEquals(2, study.getStudyPvAttrLabels().length);

        study.deleteStudyPvAttr("Worksheet");
        study.persist();
        Assert.assertEquals(1, study.getStudyPvAttrLabels().length);

        study.deleteStudyPvAttr("Visit Type");
        study.persist();
        Assert.assertEquals(0, study.getStudyPvAttrLabels().length);

        // add patient visit that uses the attribute and try to delete
        study.setStudyPvAttr("Worksheet", "text");
        study.persist();
        study.reload();
        List<PatientVisitWrapper> visits = studyAddPatientVisits(study);
        PatientVisitWrapper visit = visits.get(0);
        visit.setPvAttrValue("Worksheet", Utils.getRandomString(10, 15));
        visit.persist();

        // delete non existing label, expect exception
        try {
            study.deleteStudyPvAttr("Worksheet");
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetStudyPvAttrLabels() throws Exception {
        String name = "testGetSetStudyPvAttrLabels" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        study.setStudyPvAttr("Worksheet", "text");
        study.setStudyPvAttr("Consent", "select_multiple", new String[] { "a",
            "b" });
        Assert.assertEquals(2, study.getStudyPvAttrLabels().length);

        // test still ok after persist
        study.persist();
        study.reload();
        Assert.assertEquals(2, study.getStudyPvAttrLabels().length);
    }

    @Test
    public void testGetStudyPvAttrType() throws Exception {
        String name = "testGetStudyPvAttrType" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        study.setStudyPvAttr("Worksheet", "text");
        study.setStudyPvAttr("Visit Type", "select_single", new String[] {
            "toto", "titi", "tata" });
        study.persist();

        List<String> labels = Arrays.asList(study.getStudyPvAttrLabels());
        Assert.assertEquals(2, labels.size());
        Assert.assertTrue(labels.contains("Worksheet"));
        Assert.assertTrue(labels.contains("Visit Type"));
        Assert.assertEquals("text", study.getStudyPvAttrType("Worksheet"));
        Assert.assertEquals("select_single",
            study.getStudyPvAttrType("Visit Type"));

        // get non existing label, expect exception
        try {
            study.getStudyPvAttrType(Utils.getRandomString(10, 20));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetStudyPvAttrPermissible() throws Exception {
        String name = "testGetStudyPvAttrType" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        study.setStudyPvAttr("Worksheet", "text");
        String pvInfoLabel = "Visit Type";

        for (int i = 0; i < 4; ++i) {
            String[] values;

            switch (i) {
            case 0:
                values = new String[] { "toto", "titi", "tata" };
                break;
            case 1:
                values = new String[] { "toto", "titi" };
                break;
            case 2:
                values = new String[] { "toto" };
                break;
            case 3:
            default:
                values = null;
            }
            study.setStudyPvAttr(pvInfoLabel, "select_single", values);
            study.persist();

            study.reload();
            if (values != null) {
                String[] valuesFound = study
                    .getStudyPvAttrPermissible(pvInfoLabel);
                List<String> valuesList = Arrays.asList(values);
                Assert.assertTrue(valuesFound.length == values.length);
                for (String s : valuesFound) {
                    Assert.assertTrue(valuesList.contains(s));
                }
            } else {
                try {
                    // this label should have been removed
                    study.getStudyPvAttrPermissible(pvInfoLabel);
                    Assert.fail("call should generate an exception");
                } catch (Exception e) {
                    Assert.assertTrue(true);
                }
            }
        }
    }

    @Test
    public void testGetStudyPvAttrClosed() throws Exception {
        String name = "testGetStudyPvAttrType" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        study.setStudyPvAttr("Worksheet", "text");
        study.persist();
        study.reload();

        // attributes are not locked by default
        Assert.assertEquals("Active",
            study.getStudyPvAttrActivityStatus("Worksheet").getName());

        // lock the attribute
        study.setStudyPvAttrActivityStatus("Worksheet",
            ActivityStatusWrapper.getActivityStatus(appService, "Closed"));
        Assert.assertEquals("Closed",
            study.getStudyPvAttrActivityStatus("Worksheet").getName());

        // get lock for non existing label, expect exception
        try {
            study.getStudyPvAttrActivityStatus(Utils.getRandomString(10, 20));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // set activity status for non existing label, expect exception
        try {
            study.setStudyPvAttrActivityStatus(Utils.getRandomString(10, 20),
                ActivityStatusWrapper.getActivityStatus(appService, "Active"));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        // add patient visit that uses the locked attribute
        study.setStudyPvAttr("Worksheet", "text");
        study.setStudyPvAttrActivityStatus("Worksheet",
            ActivityStatusWrapper.getActivityStatus(appService, "Closed"));
        study.persist();
        study.reload();
        List<PatientVisitWrapper> visits = studyAddPatientVisits(study);
        PatientVisitWrapper visit = visits.get(0);
        visit.reload();

        try {
            visit.setPvAttrValue("Worksheet", Utils.getRandomString(10, 15));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testRemoveStudyPvAttr() throws Exception {
        String name = "testRemoveStudyPvAttr" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);

        int sizeOrig = study.getStudyPvAttrLabels().length;
        Collection<String> types = PvAttrTypeWrapper.getAllPvAttrTypesMap(
            appService).keySet();
        if (types.size() < 2) {
            Assert.fail("Can't test without PvAttrTypes");
        }

        study.setStudyPvAttr(name, "text");
        study.setStudyPvAttr(name + "_2", "number");
        study.persist();

        study.reload();
        Assert.assertEquals(sizeOrig + 2, study.getStudyPvAttrLabels().length);
        study.deleteStudyPvAttr(name);
        Assert.assertEquals(sizeOrig + 1, study.getStudyPvAttrLabels().length);
        site.persist();

        site.reload();
        Assert.assertEquals(sizeOrig + 1, study.getStudyPvAttrLabels().length);
    }

    @Test
    public void testGetClinicCollection() throws Exception {
        String name = "testGetClinicCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = ContactHelper.addContactsToStudy(study, site, name);

        List<ClinicWrapper> clinics = study.getClinicCollection();
        int sizeFound = clinics.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetPatientCollection() throws Exception {
        String name = "testGetPatientCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = PatientHelper.addPatients(name, study);

        List<PatientWrapper> patients = study.getPatientCollection();
        int sizeFound = patients.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetPatientCollectionBoolean() throws Exception {
        String name = "testGetPatientCollectionBoolean" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        PatientHelper.addPatients(name, study);

        List<PatientWrapper> patients = study.getPatientCollection(true);
        if (patients.size() > 1) {
            for (int i = 0; i < patients.size() - 1; i++) {
                PatientWrapper patient1 = patients.get(i);
                PatientWrapper patient2 = patients.get(i + 1);
                Assert.assertTrue(patient1.compareTo(patient2) <= 0);
            }
        }
    }

    @Test
    public void testAddPatients() throws Exception {
        String name = "testAddPatients" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = PatientHelper.addPatients(name, study);

        PatientWrapper newPatient = PatientHelper.newPatient(name
            + "newPatient");
        newPatient.setStudy(study);
        study.addPatients(Arrays.asList(newPatient));
        study.persist();

        study.reload();
        // one patient added
        Assert.assertEquals(nber + 1, study.getPatientCollection().size());
    }

    @Test
    public void testHasPatients() throws Exception {
        String name = "testHasPatients" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        Assert.assertFalse(study.hasPatients());

        PatientHelper.addPatients(name, study);
        Assert.assertTrue(study.hasPatients());
    }

    @Test
    public void testGetPatientCount() throws Exception {
        String name = "testGetPatientCountForSite" + r.nextInt();

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");

        Map<StudyWrapper, List<PatientWrapper>> studyPatientsMap = new HashMap<StudyWrapper, List<PatientWrapper>>();
        studyPatientsMap.put(study1, new ArrayList<PatientWrapper>());
        studyPatientsMap.put(study2, new ArrayList<PatientWrapper>());

        studyPatientsMap.get(study1).add(
            PatientHelper.addPatient(name + "PATIENT1", study1));
        studyPatientsMap.get(study1).add(
            PatientHelper.addPatient(name + "PATIENT2", study1));

        studyPatientsMap.get(study2).add(
            PatientHelper.addPatient(name + "PATIENT3", study2));
        studyPatientsMap.get(study2).add(
            PatientHelper.addPatient(name + "PATIENT4", study2));

        study1.reload();
        study2.reload();

        PatientWrapper patient;

        for (StudyWrapper study : studyPatientsMap.keySet()) {
            List<PatientWrapper> patients = studyPatientsMap.get(study);
            while (patients.size() > 0) {
                Assert.assertEquals(patients.size(), study.getPatientCount());

                patient = patients.get(0);
                patients.remove(patient);
                patient.delete();
                study.reload();
            }
            Assert.assertEquals(0, study.getPatientCount());
        }

    }

    @Test
    public void testGetPatientCountForSite() throws Exception {
        String name = "testGetPatientCountForSite" + r.nextInt();
        SiteWrapper site1 = SiteHelper.addSite(name + "s1");
        SiteWrapper site2 = SiteHelper.addSite(name + "s2");

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact1);
        contacts.add(contact2);

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addContacts(contacts);
        study1.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient1 = PatientHelper.addPatient(name + "PATIENT1",
            study1);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site1, clinic1, method, patient1);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "PATIENT2",
            study1);
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site2, clinic2, method, patient1, patient2);
        // clinic 1 = 1 patient for study 1
        PatientVisitHelper.addPatientVisits(patient1, shipment1);
        PatientVisitHelper.addPatientVisits(patient1, shipment2);
        // clinic 2 = 2 patients for study 1
        PatientVisitHelper.addPatientVisits(patient2, shipment2);

        site1.reload();
        site2.reload();
        study1.reload();

        Assert.assertEquals(1, study1.getPatientCountForSite(site1));
        Assert.assertEquals(2, study1.getPatientCountForSite(site2));
    }

    @Test
    public void testGetPatientVisitCountForSite() throws Exception {
        String name = "testGetPatientVisitCountForSite" + r.nextInt();
        SiteWrapper site1 = SiteHelper.addSite(name + "s1");
        SiteWrapper site2 = SiteHelper.addSite(name + "s2");

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

        PatientWrapper patient1 = PatientHelper
            .addPatient(name + "_p1", study1);
        PatientWrapper patient2 = PatientHelper
            .addPatient(name + "_p2", study2);
        PatientWrapper patient3 = PatientHelper
            .addPatient(name + "_p3", study1);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site1, clinic1, method, patient1, patient3);
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site2, clinic2, method, patient1, patient2);

        // shipment1 has patient visits for patient1 and patient3
        long nber = PatientVisitHelper.addPatientVisits(patient1, shipment1)
            .size();
        long nber2 = PatientVisitHelper.addPatientVisits(patient3, shipment1)
            .size();

        // shipment 2 has patient visits for patient1 and patient2
        long nber3 = PatientVisitHelper.addPatientVisits(patient1, shipment2)
            .size();
        long nber4 = PatientVisitHelper.addPatientVisits(patient2, shipment2)
            .size();

        site1.reload();
        site2.reload();
        study1.reload();
        study2.reload();

        Assert.assertEquals(nber + nber2,
            study1.getPatientVisitCountForSite(site1));
        Assert.assertEquals(0, study2.getPatientVisitCountForSite(site1));
        Assert.assertEquals(nber3, study1.getPatientVisitCountForSite(site2));
        Assert.assertEquals(nber4, study2.getPatientVisitCountForSite(site2));
    }

    @Test
    public void testGetPatientCountForClinic() throws Exception {
        String name = "testGetPatientCountForClinic" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact1);
        contacts.add(contact2);

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addContacts(contacts);
        study1.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient1 = PatientHelper.addPatient(name + "PATIENT1",
            study1);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic1, method, patient1);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "PATIENT2",
            study1);
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site, clinic2, method, patient1, patient2);
        // clinic 1 = 1 patient for study 1
        PatientVisitHelper.addPatientVisits(patient1, shipment1);
        PatientVisitHelper.addPatientVisits(patient1, shipment2);
        // clinic 2 = 2 patients for study 1
        PatientVisitHelper.addPatientVisits(patient2, shipment2);

        study1.reload();
        clinic1.reload();
        clinic2.reload();
        Assert.assertEquals(1, study1.getPatientCountForClinic(clinic1));
        Assert.assertEquals(2, study1.getPatientCountForClinic(clinic2));
    }

    @Test
    public void testGetPatientVisitCountForClinic() throws Exception {
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

        study1.reload();
        clinic1.reload();
        clinic2.reload();

        Assert.assertEquals(nber + nber2,
            study1.getPatientVisitCountForClinic(clinic1));
        Assert.assertEquals(nber3,
            study1.getPatientVisitCountForClinic(clinic2));
        Assert.assertEquals(nber4,
            study2.getPatientVisitCountForClinic(clinic2));
    }

    @Test
    public void testGetPatientVisitCount() throws Exception {
        String name = "testGetPatientVisitCount" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(contact1);
        contacts.add(contact2);

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addContacts(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addContacts(contacts);
        study2.persist();
        PatientWrapper patient2 = PatientHelper.addPatient(name + "2", study2);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        ClinicShipmentWrapper shipment1 = ClinicShipmentHelper.addShipment(
            site, clinic1, method, patient1, patient2);
        ClinicShipmentWrapper shipment2 = ClinicShipmentHelper.addShipment(
            site, clinic2, method, patient1, patient2);
        int nber = PatientVisitHelper.addPatientVisits(patient1, shipment1)
            .size();
        int nber2 = PatientVisitHelper.addPatientVisits(patient1, shipment2)
            .size();
        PatientVisitHelper.addPatientVisits(patient2, shipment1);
        PatientVisitHelper.addPatientVisits(patient2, shipment2);

        study1.reload();
        Assert.assertEquals(nber + nber2, study1.getPatientVisitCount());
    }

    @Test
    public void testLinkedToClinic() throws Exception {
        String name = "testLinkedToClinic" + r.nextInt();
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");

        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addContacts(Arrays.asList(contact1));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addContacts(Arrays.asList(contact2));
        study2.persist();

        Assert.assertTrue(study1.isLinkedToClinic(clinic1));
        Assert.assertFalse(study1.isLinkedToClinic(clinic2));

        Assert.assertFalse(study2.isLinkedToClinic(clinic1));
        Assert.assertTrue(study2.isLinkedToClinic(clinic2));
    }

    @Test
    public void testPersist() throws Exception {
        int oldTotal = appService.search(Study.class, new Study()).size();
        String name = "testPersist" + r.nextInt();
        StudyHelper.addStudy(name);
        int newTotal = appService.search(Study.class, new Study()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFailCheckStudyNameUnique() throws Exception {
        String name = "testPersistFailCheckStudyNameUnique" + r.nextInt();
        StudyHelper.addStudy(name);

        try {
            StudyHelper.addStudy(name);
            Assert
                .fail("Should not insert the study : same name already in database");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersitCheckNameNotEmpty() throws Exception {
        StudyWrapper s1 = StudyHelper.newStudy(null);
        try {
            s1.persist();
            Assert.fail("Should not insert the study : name empty");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersitCheckNameShortNotEmpty() throws Exception {
        String name = "testPersitCheckNameShortNotEmpty" + r.nextInt();
        StudyWrapper s1 = StudyHelper.newStudy(name);
        s1.setNameShort(null);
        try {
            s1.persist();
            Assert.fail("Should not insert the study : name short empty");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersitCheckStudyShortNameUnique() throws Exception {
        String name = "testCheckStudyShortNameUnique" + r.nextInt();
        StudyWrapper s1 = StudyHelper.newStudy(name);
        s1.setNameShort(name);
        s1.persist();
        StudyHelper.createdStudies.add(s1);

        StudyWrapper s2 = StudyHelper.newStudy(name + "_2");
        s2.setNameShort(name);
        try {
            s2.persist();
            Assert
                .fail("Should not insert the study : same short name already in database");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersitCheckStudyNoActivityStatus() throws Exception {
        String name = "testCheckStudyShortNameUnique" + r.nextInt();
        StudyWrapper s1 = StudyHelper.newStudy(name);
        s1.setActivityStatus(null);

        try {
            s1.persist();
            Assert.fail("Should not insert the study : no activity status");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        s1.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        s1.persist();
        StudyHelper.createdStudies.add(s1);
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name, false);

        // object is in database
        Study studyInDB = ModelUtils.getObjectWithId(appService, Study.class,
            study.getId());
        Assert.assertNotNull(studyInDB);

        study.delete();

        studyInDB = ModelUtils.getObjectWithId(appService, Study.class,
            study.getId());
        // object is not anymore in database
        Assert.assertNull(studyInDB);
    }

    @Test
    public void testDeleteFailNoMorePatient() throws Exception {
        String name = "testDeleteFailNoMorePatient" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        PatientHelper.addPatient(name, study);
        study.reload();
        try {
            study.delete();
            Assert
                .fail("Should not delete : patients need to be removed first");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        study.reload();
        String oldName = study.getName();
        study.setName("toto");
        study.reset();
        Assert.assertEquals(oldName, study.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        StudyWrapper newStudy = new StudyWrapper(appService);
        newStudy.setName("titi");
        newStudy.reset();
        Assert.assertEquals(null, newStudy.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy("WERTY" + name);
        StudyWrapper study2 = StudyHelper.addStudy("AASDF" + name);

        Assert.assertTrue(study.compareTo(study2) > 0);
        Assert.assertTrue(study2.compareTo(study) < 0);
    }

    @Test
    public void testHasClinic() throws Exception {
        String name = "testHasClinic" + r.nextInt();
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "_2");
        ContactHelper.addContact(clinic2, name);

        StudyWrapper study = StudyHelper.addStudy(name);
        study.addContacts(Arrays.asList(contact1));
        study.persist();

        study.reload();

        Assert.assertTrue(study.hasClinic(clinic1.getNameShort()));
        Assert.assertFalse(study.hasClinic(clinic2.getNameShort()));
    }

    @Test
    public void testGetPatient() throws Exception {
        String name = "testGetPatient" + r.nextInt();

        StudyWrapper study = StudyHelper.addStudy(name);
        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        StudyWrapper study2 = StudyHelper.addStudy(name + "_2");
        PatientWrapper patient3 = PatientHelper.addPatient(name + "_3", study2);

        study.reload();
        Assert.assertEquals(patient1, study.getPatient(name + "_1"));
        Assert.assertEquals(patient2, study.getPatient(name + "_2"));
        Assert.assertEquals(patient3, study2.getPatient(name + "_3"));
        Assert.assertNull(study.getPatient(name + "_3"));
        Assert.assertNull(study2.getPatient(name + "_1"));
    }
}
