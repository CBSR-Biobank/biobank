package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.helpers.SiteQuery;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrTypeWrapper;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.NullPropertyException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotedSpecimenHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceSpecimenHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

@Deprecated
public class TestStudy extends TestDatabase {
    @Test
    public void testGetSiteCollection() throws Exception {
        String name = "testGetSiteCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        int sitesNber = r.nextInt(15) + 1;
        SiteHelper.addSites(name, sitesNber);

        SiteWrapper site = SiteHelper.addSite(name, false);
        List<SiteWrapper> sites = SiteQuery.getSites(appService);
        for (SiteWrapper s : sites) {
            s.addToStudyCollection(Arrays.asList(study));
            s.persist();
        }
        study.reload();

        List<SiteWrapper> studySites = study.getSiteCollection(false);

        Assert.assertEquals(sites.size(), studySites.size());

        // delete a site
        sites.remove(site);
        site.reload(); // because stale from adding study through a different
                       // wrapper
        site.delete();
        SiteHelper.createdSites.remove(site);

        study.reload();
        studySites = study.getSiteCollection(false);
        Assert.assertEquals(sites.size(), studySites.size());
    }

    @Test
    public void testGetSiteCollectionSorted() throws Exception {
        String name = "testGetSiteCollectionSorted" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        SiteHelper.addSites(name, r.nextInt(15) + 5);

        List<SiteWrapper> sites = SiteQuery.getSites(appService);
        for (SiteWrapper s : sites) {
            s.addToStudyCollection(Arrays.asList(study));
            s.persist();
        }
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
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        testGettersAndSetters(study);
    }

    @Test
    public void testGetContactCollection() throws Exception {
        String name = "testGetContactCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = ContactHelper.addContactsToStudy(study, name);

        List<ContactWrapper> contacts = study.getContactCollection(false);
        int sizeFound = contacts.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetContactCollectionBoolean() throws Exception {
        String name = "testGetContactCollectionBoolean" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactHelper.addContactsToStudy(study, name);

        List<ContactWrapper> contacts = study.getContactCollection(true);
        if (contacts.size() > 1) {
            for (int i = 0; i < (contacts.size() - 1); i++) {
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
        int nber = ContactHelper.addContactsToStudy(study, name);
        site.reload();

        // get a clinic not yet added
        List<ContactWrapper> contacts = study.getContactCollection(false);
        List<ClinicWrapper> clinics = ClinicWrapper.getAllClinics(appService);
        for (ContactWrapper contact : contacts) {
            clinics.remove(contact.getClinic());
        }
        ClinicWrapper clinicNotAdded = DbHelper.chooseRandomlyInList(clinics);
        ContactWrapper contactToAdd = DbHelper
            .chooseRandomlyInList(clinicNotAdded.getContactCollection(false));
        study.addToContactCollection(Arrays.asList(contactToAdd));
        study.persist();

        study.reload();
        // one contact added
        Assert.assertEquals(nber + 1, study.getContactCollection(false).size());
    }

    @Test
    public void testRemoveContacts() throws Exception {
        String name = "testRemoveContacts" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        int nber = ContactHelper.addContactsToStudy(study, name);
        site.reload();

        // get a clinic not yet added
        List<ContactWrapper> contacts = study.getContactCollection(false);
        ContactWrapper contact = DbHelper.chooseRandomlyInList(contacts);
        // don't have to delete contact because this is a *..* relation
        study.removeFromContactCollection(Arrays.asList(contact));
        study.persist();

        study.reload();
        // one contact added
        Assert.assertEquals(nber - 1, study.getContactCollection(false).size());
    }

    @Test
    public void testAliquotedSpecimens() throws Exception {
        String name = "testGetAliquotedSpecimens" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        List<AliquotedSpecimenWrapper> set1 = AliquotedSpecimenHelper
            .addRandAliquotedSpecimens(study, site, name + "_set1");

        Assert.assertEquals(set1.size(),
            study.getAliquotedSpecimenCollection(false).size());

        List<AliquotedSpecimenWrapper> set2 = AliquotedSpecimenHelper
            .addRandAliquotedSpecimens(study, site, name + "_set2");

        Assert.assertEquals(set1.size() + set2.size(), study
            .getAliquotedSpecimenCollection(false).size());

        for (AliquotedSpecimenWrapper aqSpc : set1) {
            aqSpc.delete();
        }

        study.reload();
        Assert.assertEquals(set2.size(),
            study.getAliquotedSpecimenCollection(false).size());

        for (AliquotedSpecimenWrapper aqSpc : set2) {
            aqSpc.delete();
        }

        study.reload();
        Assert.assertEquals(0, study.getAliquotedSpecimenCollection(false)
            .size());
    }

    @Test
    public void testGetAliquotedSpecimenCollectionBoolean() throws Exception {
        // exceed short name max length for specimen type if we use full method
        // name
        String name = "testGetASCB" + r.nextInt();

        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);
        AliquotedSpecimenHelper.addAliquotedSpecimens(study, site, name);

        List<AliquotedSpecimenWrapper> storages = study
            .getAliquotedSpecimenCollection(true);
        if (storages.size() > 1) {
            for (int i = 0; i < (storages.size() - 1); i++) {
                AliquotedSpecimenWrapper storage1 = storages.get(i);
                AliquotedSpecimenWrapper storage2 = storages.get(i + 1);
                Assert.assertTrue(storage1.compareTo(storage2) <= 0);
            }
        }
    }

    @Test
    public void testGetSourceSpecimenCollection() throws Exception {
        String name = "testGetSourceSpecimenCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        List<SourceSpecimenWrapper> set1 = SourceSpecimenHelper
            .addRandSourceSpecimens(study, name, true, true);

        Assert.assertEquals(set1.size(),
            study.getSourceSpecimenCollection(false).size());

        List<SourceSpecimenWrapper> set2 = SourceSpecimenHelper
            .addRandSourceSpecimens(study, name, true, true);

        Assert.assertEquals(set1.size() + set2.size(), study
            .getSourceSpecimenCollection(false).size());

        for (SourceSpecimenWrapper srcSpc : set1) {
            srcSpc.delete();
        }

        study.reload();
        Assert.assertEquals(set2.size(),
            study.getSourceSpecimenCollection(false).size());

        for (SourceSpecimenWrapper srcSpc : set2) {
            srcSpc.delete();
        }

        study.reload();
        Assert.assertEquals(0, study.getSourceSpecimenCollection(false).size());
    }

    @Test
    public void testGetSourceSpecimenCollectionBoolean() throws Exception {
        String name = "testGetSourceSpecimenCollectionBoolean" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        SourceSpecimenHelper.addSourceSpecimens(study, name, true, true);

        List<SourceSpecimenWrapper> sources = study
            .getSourceSpecimenCollection(true);
        if (sources.size() > 1) {
            for (int i = 0; i < (sources.size() - 1); i++) {
                SourceSpecimenWrapper source1 = sources.get(i);
                SourceSpecimenWrapper source2 = sources.get(i + 1);
                Assert.assertTrue(source1.compareTo(source2) <= 0);
            }
        }
    }

    @Test
    public void testSetStudyEventAttr() throws Exception {
        String name = "testSetStudyPvAttr" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        Collection<String> types = EventAttrTypeWrapper
            .getAllEventAttrTypesMap(appService).keySet();
        Assert.assertTrue(types.contains("text"));
        Assert.assertTrue(types.contains("select_single"));

        study.setStudyEventAttr("Patient Type 2", EventAttrTypeEnum.TEXT);
        study.setStudyEventAttr("Visit Type", EventAttrTypeEnum.SELECT_SINGLE,
            new String[] { "toto", "titi", "tata" });
        study.persist();
        study.reload();

        Assert.assertEquals(2, study.getStudyEventAttrLabels().length);

        study.deleteStudyEventAttr("Patient Type 2");
        study.persist();
        Assert.assertEquals(1, study.getStudyEventAttrLabels().length);

        study.deleteStudyEventAttr("Visit Type");
        study.persist();
        Assert.assertEquals(0, study.getStudyEventAttrLabels().length);

        // add patient visit that uses the attribute and try to delete
        study.setStudyEventAttr("Patient Type 2", EventAttrTypeEnum.TEXT);
        study.persist();

        SiteWrapper site = SiteHelper.addSite("testsite");

        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(site, PatientHelper.addPatient("testp", study),
                1);
        cevent.setEventAttrValue("Patient Type 2", Utils.getRandomString(5));
        cevent.persist();

        // delete existing label, expect exception
        try {
            study.deleteStudyEventAttr("Patient Type 2");
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetStudyEventAttrLabels() throws Exception {
        String name = "testGetSetStudyPvAttrLabels" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        study.setStudyEventAttr("Patient Type 2", EventAttrTypeEnum.TEXT);
        study.setStudyEventAttr("Consent", EventAttrTypeEnum.SELECT_MULTIPLE,
            new String[] { "a", "b" });
        Assert.assertEquals(2, study.getStudyEventAttrLabels().length);

        // test still ok after persist
        study.persist();
        study.reload();
        Assert.assertEquals(2, study.getStudyEventAttrLabels().length);
    }

    @Test
    public void testGetStudyEventAttrType() throws Exception {
        String name = "testGetStudyPvAttrType" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        study.setStudyEventAttr("Patient Type 2", EventAttrTypeEnum.TEXT);
        study.setStudyEventAttr("Visit Type", EventAttrTypeEnum.SELECT_SINGLE,
            new String[] { "toto", "titi", "tata" });
        study.persist();

        List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        Assert.assertEquals(2, labels.size());
        Assert.assertTrue(labels.contains("Patient Type 2"));
        Assert.assertTrue(labels.contains("Visit Type"));

        // get non existing label, expect exception
        try {
            study.getStudyEventAttrType(Utils.getRandomString(10, 20));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetStudyEventAttrPermissible() throws Exception {
        String name = "testGetStudyPvAttrType" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        study.setStudyEventAttr("Patient Type 2", EventAttrTypeEnum.TEXT);
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
            study.setStudyEventAttr(pvInfoLabel,
                EventAttrTypeEnum.SELECT_SINGLE, values);
            study.persist();

            study.reload();
            if (values != null) {
                String[] valuesFound = study
                    .getStudyEventAttrPermissible(pvInfoLabel);
                List<String> valuesList = Arrays.asList(values);
                Assert.assertTrue(valuesFound.length == values.length);
                for (String s : valuesFound) {
                    Assert.assertTrue(valuesList.contains(s));
                }
            } else {
                try {
                    // this label should have been removed
                    study.getStudyEventAttrPermissible(pvInfoLabel);
                    Assert.fail("call should generate an exception");
                } catch (Exception e) {
                    Assert.assertTrue(true);
                }
            }
        }
    }

    @Test
    public void testGetStudyEventAttrClosed() throws Exception {
        String name = "testGetStudyPvAttrType" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);

        study.setStudyEventAttr("Patient Type 2", EventAttrTypeEnum.TEXT);
        study.persist();
        study.reload();

        // attributes are not locked by default
        Assert.assertEquals(ActivityStatusWrapper.ACTIVE_STATUS_STRING, study
            .getStudyEventAttrActivityStatus("Patient Type 2").getName());

        // lock the attribute
        study.setStudyEventAttrActivityStatus("Patient Type 2",
            ActivityStatusWrapper.getActivityStatus(appService,
                ActivityStatusWrapper.CLOSED_STATUS_STRING));
        Assert.assertEquals(ActivityStatusWrapper.CLOSED_STATUS_STRING, study
            .getStudyEventAttrActivityStatus("Patient Type 2").getName());

        // get lock for non existing label, expect exception
        try {
            study
                .getStudyEventAttrActivityStatus(Utils.getRandomString(10, 20));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // set activity status for non existing label, expect exception
        try {
            study.setStudyEventAttrActivityStatus(
                Utils.getRandomString(10, 20),
                ActivityStatusWrapper.getActiveActivityStatus(appService));
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        // add patient visit that uses the locked attribute
        study.setStudyEventAttr("Patient Type 2", EventAttrTypeEnum.TEXT);
        study.setStudyEventAttrActivityStatus("Patient Type 2",
            ActivityStatusWrapper.getActivityStatus(appService,
                ActivityStatusWrapper.CLOSED_STATUS_STRING));
        study.persist();
        study.reload();
        SiteWrapper site = SiteHelper.addSite("testsite");

        CollectionEventWrapper visit = CollectionEventHelper
            .addCollectionEvent(site, PatientHelper.addPatient("testp", study),
                1);
        try {
            visit.setEventAttrValue("Patient Type 2", Utils.getRandomString(5));
            visit.persist();
            Assert.fail("call should generate an exception");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testRemoveStudyEventAttr() throws Exception {
        String name = "testRemoveStudyPvAttr" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        StudyWrapper study = StudyHelper.addStudy(name);

        int sizeOrig = study.getStudyEventAttrLabels().length;
        Collection<String> types = EventAttrTypeWrapper
            .getAllEventAttrTypesMap(appService).keySet();
        if (types.size() < 2) {
            Assert.fail("Can't test without PvAttrTypes");
        }

        study.setStudyEventAttr(name, EventAttrTypeEnum.TEXT);
        study.setStudyEventAttr(name + "_2", EventAttrTypeEnum.NUMBER);
        study.persist();

        study.reload();
        Assert.assertEquals(sizeOrig + 2,
            study.getStudyEventAttrLabels().length);
        study.deleteStudyEventAttr(name);
        Assert.assertEquals(sizeOrig + 1,
            study.getStudyEventAttrLabels().length);
        site.persist();

        site.reload();
        Assert.assertEquals(sizeOrig + 1,
            study.getStudyEventAttrLabels().length);
    }

    @Test
    public void testClinicCollection() throws Exception {
        String name = "testGetClinicCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        List<ClinicWrapper> set1 = ClinicHelper.addClinics(name + "_set1",
            r.nextInt(15) + 1, true);
        for (ClinicWrapper clinic : set1) {
            study.addToContactCollection(clinic.getContactCollection(false));
        }
        study.persist();
        Assert.assertEquals(set1.size(), study.getClinicCollection().size());

        List<ClinicWrapper> set2 = ClinicHelper.addClinics(name + "_set2",
            r.nextInt(15) + 1, true);
        for (ClinicWrapper clinic : set2) {
            study.addToContactCollection(clinic.getContactCollection(false));
        }
        study.persist();
        Assert.assertEquals(set1.size() + set2.size(), study
            .getClinicCollection().size());

        for (ClinicWrapper clinic : set1) {
            study.removeFromContactCollection(clinic
                .getContactCollection(false));
        }
        study.persist();
        Assert.assertEquals(set2.size(), study.getClinicCollection().size());

        for (ClinicWrapper clinic : set2) {
            study.removeFromContactCollection(clinic
                .getContactCollection(false));
        }
        study.persist();
        Assert.assertEquals(0, study.getClinicCollection().size());
    }

    @Test
    public void testGetPatientCollection() throws Exception {
        String name = "testGetPatientCollection" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name + "_set1");
        List<PatientWrapper> set1 = PatientHelper.addRandPatients(name, study);

        study.reload();
        Assert.assertEquals(set1.size(), study.getPatientCollection(false)
            .size());

        List<PatientWrapper> set2 = PatientHelper.addRandPatients(name
            + "_set2", study);
        study.reload();
        Assert.assertEquals(set1.size() + set2.size(), study
            .getPatientCollection(false).size());

        for (PatientWrapper patient : set1) {
            patient.delete();
        }
        study.reload();
        Assert.assertEquals(set2.size(), study.getPatientCollection(false)
            .size());

        for (PatientWrapper patient : set2) {
            patient.delete();
        }
        study.reload();
        Assert.assertEquals(0, study.getPatientCollection(false).size());
    }

    @Test
    public void testGetPatientCollectionBoolean() throws Exception {
        String name = "testGetPatientCollectionBoolean" + r.nextInt();
        StudyWrapper study = StudyHelper.addStudy(name);
        PatientHelper.addPatients(name, study);

        List<PatientWrapper> patients = study.getPatientCollection(true);
        if (patients.size() > 1) {
            for (int i = 0; i < (patients.size() - 1); i++) {
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
        study.addToPatientCollection(Arrays.asList(newPatient));
        study.persist();

        study.reload();
        // one patient added
        Assert.assertEquals(nber + 1, study.getPatientCollection(false).size());
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

        Map<StudyWrapper, List<PatientWrapper>> studyPatientsMap =
            new HashMap<StudyWrapper, List<PatientWrapper>>();
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
                Assert.assertEquals(patients.size(),
                    study.getPatientCount(false));
                Assert.assertEquals(patients.size(),
                    study.getPatientCount(true));

                patient = patients.get(0);
                patients.remove(patient);
                patient.delete();
                study.reload();
            }
            Assert.assertEquals(0, study.getPatientCount(true));
            Assert.assertEquals(0, study.getPatientCount(false));
        }

    }

    @Test
    public void testGetCollectionEventCount() throws Exception {
        String name = "testGetProcessingEventCount" + r.nextInt();

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
        study1.addToContactCollection(contacts);
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addToContactCollection(contacts);
        study2.persist();

        List<CollectionEventWrapper> set1_1 = CollectionEventHelper
            .addCollectionEvents(clinic1, study1, study1.getName() + "_set1");
        study1.reload();
        Assert.assertEquals(set1_1.size(), study1.getCollectionEventCount());

        List<CollectionEventWrapper> set1_2 = CollectionEventHelper
            .addCollectionEvents(clinic1, study1, study1.getName() + "_set2");
        study1.reload();
        Assert.assertEquals(set1_1.size() + set1_2.size(),
            study1.getCollectionEventCount());

        List<CollectionEventWrapper> set2_1 = CollectionEventHelper
            .addCollectionEvents(clinic2, study2, study2.getName() + "_set1");
        study2.reload();
        Assert.assertEquals(set2_1.size(), study2.getCollectionEventCount());
        // ensure count for study1 does not change
        Assert.assertEquals(set1_1.size() + set1_2.size(),
            study1.getCollectionEventCount());

        DbHelper.deleteCollectionEvents(set1_1);
        study1.reload();
        Assert.assertEquals(set1_2.size(), study1.getCollectionEventCount());

        // ensure count does not change for study2
        Assert.assertEquals(set2_1.size(), study2.getCollectionEventCount());

        DbHelper.deleteCollectionEvents(set1_2);
        study1.reload();
        Assert.assertEquals(0, study1.getCollectionEventCount());

        // ensure count does not change for study2
        Assert.assertEquals(set2_1.size(), study2.getCollectionEventCount());
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
        study1.addToContactCollection(Arrays.asList(contact1));
        study1.persist();

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addToContactCollection(Arrays.asList(contact2));
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
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPersitCheckNameNotEmpty() throws Exception {
        StudyWrapper s1 = StudyHelper.newStudy(null);
        try {
            s1.persist();
            Assert.fail("Should not insert the study : name empty");
        } catch (NullPropertyException e) {
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
        } catch (NullPropertyException e) {
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
        } catch (DuplicatePropertySetException e) {
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
        } catch (ValueNotSetException e) {
            Assert.assertTrue(true);
        }

        s1.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
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

        Integer studyId = study.getId();

        study.delete();

        studyInDB = ModelUtils
            .getObjectWithId(appService, Study.class, studyId);
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
        } catch (CollectionNotEmptyException e) {
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
