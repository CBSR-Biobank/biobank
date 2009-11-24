package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.DbHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;

public class TestClinic extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);

        testGettersAndSetters(clinic);
    }

    @Test
    public void testGetSetSite() throws Exception {
        String name = "testGetSite" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        SiteWrapper site2 = SiteHelper.addSite(name + "SITE2");

        clinic.setSite(site2);
        clinic.persist();

        clinic.reload();

        Assert.assertFalse(site.equals(clinic.getSite()));

        Assert.assertEquals(site2, clinic.getSite());
    }

    @Test
    public void testGetContactCollection() throws Exception {
        String name = "testGetContactCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        int nber = r.nextInt(5) + 1;
        for (int i = 0; i < nber; i++) {
            ContactHelper.addContact(clinic, name + i);
        }
        clinic.reload();
        List<ContactWrapper> contacts = clinic.getContactCollection();
        int sizeFound = contacts.size();

        Assert.assertEquals(nber, sizeFound);
    }

    @Test
    public void testGetContactCollectionBoolean() throws Exception {
        String name = "testGetContactCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name, true);

        List<ContactWrapper> contacts = clinic.getContactCollection(true);
        if (contacts.size() > 1) {
            for (int i = 0; i < contacts.size() - 1; i++) {
                ContactWrapper contact1 = contacts.get(i);
                ContactWrapper contact2 = contacts.get(i + 1);
                Assert.assertTrue(contact1.compareTo(contact2) <= 0);
            }
        }
    }

    @Test
    public void testAddInContactCollection() throws Exception {
        String name = "testAddInContactCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        int nber = r.nextInt(5) + 1;
        for (int i = 0; i < nber; i++) {
            ContactHelper.addContact(clinic, name + i);
        }
        clinic.reload();
        List<ContactWrapper> contacts = clinic.getContactCollection();
        ContactWrapper contact = ContactHelper.newContact(clinic, name + "NEW");
        contacts.add(contact);
        clinic.setContactCollection(contacts);
        clinic.persist();

        clinic.reload();
        // one contact added
        Assert.assertEquals(nber + 1, clinic.getContactCollection().size());
    }

    @Test
    public void testGetStudyCollection() throws Exception {
        String name = "testGetStudyCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name, true);
        StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection()));
        study1.setContactCollection(contacts);
        study1.persist();

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "CLINIC2",
            true);
        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        contacts = new ArrayList<ContactWrapper>();
        contacts.add(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection()));
        contacts.add(DbHelper.chooseRandomlyInList(clinic2
            .getContactCollection()));
        study2.setContactCollection(contacts);
        study2.persist();

        clinic.reload();

        Assert.assertEquals(2, clinic.getStudyCollection(false).size());
        Assert.assertEquals(1, clinic2.getStudyCollection(false).size());
    }

    @Test
    public void testGetStudyCollectionBoolean() throws Exception {
        String name = "testGetStudyCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name, true);
        StudyWrapper study1 = StudyHelper.addStudy(site, name + "STUDY1");
        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        contacts.add(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection()));
        study1.setContactCollection(contacts);
        study1.persist();
        StudyWrapper study2 = StudyHelper.addStudy(site, name + "STUDY2");
        contacts = new ArrayList<ContactWrapper>();
        contacts.add(DbHelper.chooseRandomlyInList(clinic
            .getContactCollection()));
        study2.setContactCollection(contacts);
        study2.persist();

        clinic.reload();

        List<StudyWrapper> studies = clinic.getStudyCollection(true);
        if (studies.size() > 1) {
            for (int i = 0; i < studies.size() - 1; i++) {
                StudyWrapper s1 = studies.get(i);
                StudyWrapper s2 = studies.get(i + 1);
                Assert.assertTrue(s1.compareTo(s2) <= 0);
            }
        }
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        int oldTotal = appService.search(Clinic.class, new Clinic()).size();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicHelper.addClinic(site, name);

        int newTotal = appService.search(Clinic.class, new Clinic()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFail() throws Exception {
        String name = "testPersistFail" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic = new ClinicWrapper(appService);
        clinic.setName(name);
        clinic.setSite(site);
        try {
            clinic.persist();
            Assert.fail("Should not insert the clinic : no address");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        clinic.setCity("Vesoul");
        clinic.persist();

        clinic = ClinicHelper.newClinic(site, name);
        try {
            clinic.persist();
            Assert
                .fail("Should not insert the clinic : same name already in database for this site");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        SiteWrapper site2 = SiteHelper.addSite(name + "SITE2");
        // can insert same name in different site
        clinic = ClinicHelper.newClinic(site2, name);
        clinic.persist();
        Assert.assertTrue(true);
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);

        // object is in database
        Clinic clinicInDB = ModelUtils.getObjectWithId(appService,
            Clinic.class, clinic.getId());
        Assert.assertNotNull(clinicInDB);

        clinic.delete();

        clinicInDB = ModelUtils.getObjectWithId(appService, Clinic.class,
            clinic.getId());
        // object is not anymore in database
        Assert.assertNull(clinicInDB);
    }

    @Test
    public void testDeleteWithContacts() throws Exception {
        String name = "testDeleteWithContacts" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        int contactId = ContactHelper.addContact(clinic, name).getId();
        clinic.reload();

        clinic.delete();

        Contact contactInDB = ModelUtils.getObjectWithId(appService,
            Contact.class, contactId);
        Assert.assertNull(contactInDB);
    }

    @Test
    public void testDeleteWithContactsLinkedToStudy() throws Exception {
        String name = "testDeleteWithContacts" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(site, name);
        List<ContactWrapper> studyContacts = new ArrayList<ContactWrapper>();
        studyContacts.add(contact);
        study.setContactCollection(studyContacts);
        study.persist();

        clinic.reload();
        contact.reload();

        try {
            clinic.delete();
            Assert
                .fail("Can't remove a clinic if a study linked to one of its contacts still exists");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        clinic.reload();
        String oldName = clinic.getName();
        clinic.setName("toto");
        clinic.reset();
        Assert.assertEquals(oldName, clinic.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.newClinic(site, name);
        clinic.reset();
        Assert.assertEquals(null, clinic.getName());
    }

}
