package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestCenter extends TestDatabase {

    @Test
    public void testCenter() {
        Assert
            .fail("Some tests are common to clinic, site and research group. "
                + "We might want them tested here ?");
    }

    @Test
    public void testGetCollectionEvent() throws Exception {
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

        List<CollectionEventWrapper> ceventSet1 = CollectionEventHelper
            .addCollectionEvents(site, patient1, name + "_set1");
        site.reload();
        Assert.assertEquals(ceventSet1.size(), site.getCollectionEventCount());

        List<CollectionEventWrapper> ceventSet2 = CollectionEventHelper
            .addCollectionEvents(site, patient2, name + "_set2");
        site.reload();
        Assert.assertEquals(ceventSet1.size() + ceventSet2.size(),
            site.getCollectionEventCount());

        // delete cevent set 1
        DbHelper.deleteCollectionEvents(ceventSet1);
        site.reload();
        Assert.assertEquals(ceventSet2.size(), site.getCollectionEventCount());

        // delete cevent set 2
        DbHelper.deleteCollectionEvents(ceventSet2);
        site.reload();
        Assert.assertEquals(0, site.getCollectionEventCount());
    }
}
