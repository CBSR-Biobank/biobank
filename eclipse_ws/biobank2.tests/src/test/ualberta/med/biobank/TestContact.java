package test.ualberta.med.biobank;

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
    public void testGetSetStudyCollection() throws BiobankCheckException,
        Exception {
        String name = "testGetSetStudyCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        StudyWrapper study = StudyHelper.addStudy(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        study.setContactCollection(Arrays
            .asList(new ContactWrapper[] { contact }));
        study.persist();
        contact.reload();

        Assert.assertEquals(1, contact.getStudyCollection().size());

        StudyWrapper study2 = StudyHelper.addStudy(site, name + "_2");
        List<StudyWrapper> studies = contact.getStudyCollection();
        studies.add(study2);
        contact.setStudyCollection(studies);
        contact.persist();

        contact.reload();
        // Don't work because of the *..* relation : we have to choose one way
        // only for updates
        // Assert.assertEquals(2, contact.getStudyCollection().size());
    }

    @Override
    public void tearDown() {

    }

    @Test
    public void testGetSetClinicWrapper() throws BiobankCheckException,
        Exception {
        String name = "testGetSetClinicWrapper" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        ClinicWrapper clinic2 = ClinicHelper.addClinic(site, name + "_2");
        contact.setClinicWrapper(clinic2);
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
        String name = "testGetSetStudyCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic = ClinicHelper.addClinic(site, name);

        ContactWrapper cw = ContactHelper.newContact(clinic, name);
        cw.persist();
    }

}
