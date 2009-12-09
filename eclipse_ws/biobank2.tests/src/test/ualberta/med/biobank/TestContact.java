package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import test.ualberta.med.biobank.internal.ClinicHelper;
import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Contact;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestContact extends TestDatabase {

    @Test
    public void testGetStudyCollection() throws BiobankCheckException,
        Exception {
        String name = "testGetStudyCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        contact.reload();

        Assert.assertEquals(1, contact.getStudyCollection().size());
    }

    @Test
    public void testGetStudyCollectionBoolean() throws BiobankCheckException,
        Exception {
        String name = "testGetStudyCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(site, "QWERTY" + name);
        StudyWrapper study2 = StudyHelper.addStudy(site, "ASDFG" + name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        study2.setContactCollection(Arrays.asList(contact));
        study2.persist();
        contact.reload();

        List<StudyWrapper> studiesSorted = contact.getStudyCollection(true);
        if (studiesSorted.size() > 1) {
            for (int i = 0; i < studiesSorted.size() - 1; i++) {
                StudyWrapper s1 = studiesSorted.get(i);
                StudyWrapper s2 = studiesSorted.get(i + 1);
                Assert.assertTrue(s1.compareTo(s2) <= 0);
            }
        }
    }

    @Test
    public void testGetSetClinicWrapper() throws BiobankCheckException,
        Exception {
        String name = "testGetSetClinicWrapper" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "_2");
        contact.setClinic(clinic2);
        contact.persist();

        contact.reload();
        clinic.reload();
        clinic2.reload();

        Assert.assertFalse(clinic.equals(contact.getClinic()));

        Assert.assertEquals(clinic2, contact.getClinic());
    }

    @Test
    public void testCompareTo() {
        ContactWrapper contact1 = new ContactWrapper(appService, new Contact());
        contact1.setName("stuff");
        ContactWrapper contact2 = new ContactWrapper(appService, new Contact());
        contact2.setName("stuff");
        Assert.assertTrue(contact1.compareTo(contact2) == 0);
        contact1.setName("stuff1");
        Assert.assertTrue(contact1.compareTo(contact2) > 0);
        contact1.setName("stuff");
        contact2.setName("stuff1");
        Assert.assertTrue(contact1.compareTo(contact2) < 0);
    }

    @Test(expected = ApplicationException.class)
    public void TestPersistsNullClinic() throws Exception {
        // null clinic
        ContactWrapper c = new ContactWrapper(appService);
        c.persist();
    }

    @Test
    public void testBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        String name = "testBasicGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        testGettersAndSetters(contact);
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);

        ContactWrapper cw = ContactHelper.newContact(clinic, name);
        cw.persist();
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        contact.delete();

        Contact contactDB = new Contact();
        contactDB.setId(contact.getId());
        Assert.assertEquals(0, appService.search(Contact.class, contactDB)
            .size());
    }

    @Test
    public void testDeleteFailNoMoreStudies() throws Exception {
        String name = "testDeleteFailNoMoreStudies" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        StudyWrapper study = StudyHelper.addStudy(site, name);
        study.setContactCollection(Arrays.asList(contact));
        study.persist();
        contact.reload();

        try {
            contact.delete();
            Assert.fail("one study still linked to this contact");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }

        study.setContactCollection(new ArrayList<ContactWrapper>());
        study.persist();
        contact.reload();
        contact.delete();

        Contact contactDB = new Contact();
        contactDB.setId(contact.getId());
        Assert.assertEquals(0, appService.search(Contact.class, contactDB)
            .size());
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        String oldName = contact.getName();
        contact.setName("toto");
        contact.reset();
        Assert.assertEquals(oldName, contact.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.newContact(clinic, name);
        contact.setName("titi");
        contact.reset();
        Assert.assertEquals(null, contact.getName());
    }
}
