package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrTypeWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.DbHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestCollectionEvent extends TestDatabase {

    private StudyWrapper study;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        study = StudyHelper.addStudy("Study - Processing Event Test "
            + Utils.getRandomString(10));
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        StudyWrapper study = StudyHelper.addStudy(name);
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        SpecimenWrapper spc = SpecimenHelper.newSpecimen(name);

        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(site, patient, 1, spc);

        testGettersAndSetters(cevent);
    }

    @Test
    public void testGetPatient() throws Exception {
        String name = "testGetPatient" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "CLINIC1");
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name
            + "CONTACT1");
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "CLINIC2");
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name
            + "CONTACT2");

        List<ContactWrapper> contacts = Arrays.asList(contact1, contact2);

        StudyWrapper study1 = StudyHelper.addStudy(name + "STUDY1");
        study1.addToContactCollection(contacts);
        study1.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study1);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study1);

        StudyWrapper study2 = StudyHelper.addStudy(name + "STUDY2");
        study2.addToContactCollection(contacts);
        study2.persist();
        PatientWrapper patient3 = PatientHelper.addPatient(name + "_3", study2);

        for (PatientWrapper patient : new PatientWrapper[] { patient1,
            patient2, patient3 }) {
            SpecimenWrapper spc = SpecimenHelper.newSpecimen(patient
                .getPnumber());
            OriginInfoWrapper originInfo = new OriginInfoWrapper(appService);
            originInfo.setCenter(site);
            originInfo.persist();
            CollectionEventWrapper cevent = CollectionEventHelper
                .addCollectionEvent(site, patient, 1, spc);
            cevent.reload();
            Assert.assertEquals(patient, cevent.getPatient());
        }
    }

    @Test
    public void testRemoveSpecimens() throws Exception {
        // FIXME: issue 1180
        String name = "testRemoveSpecimens" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy("study" + name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        SpecimenTypeWrapper type = SpecimenTypeWrapper.getAllSpecimenTypes(
            appService, false).get(0);
        SpecimenWrapper[] newSpecs = new SpecimenWrapper[r.nextInt(10) + 3];
        for (int i = 0; i < newSpecs.length; i++) {
            newSpecs[i] = SpecimenHelper.newSpecimen(type);
        }
        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(clinic, patient1, 1, newSpecs);

        Assert
            .assertEquals(newSpecs.length, cevent.getAllSpecimensCount(false));

        SpecimenWrapper spec = DbHelper.chooseRandomlyInList(cevent
            .getAllSpecimenCollection(false));
        cevent.removeFromAllSpecimenCollection(Arrays.asList(spec));
        cevent.persist();

        // one specimen removed
        Assert.assertEquals(newSpecs.length - 1,
            cevent.getAllSpecimensCount(false));
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy("study" + name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();

        List<CollectionEventWrapper> ceSet1 = CollectionEventHelper
            .addCollectionEvents(clinic, study, name);
        int count = appService.search(CollectionEvent.class,
            new CollectionEvent()).size();
        Assert.assertEquals(ceSet1.size(), count);

        List<CollectionEventWrapper> ceSet2 = CollectionEventHelper
            .addCollectionEvents(clinic, study, name);
        count = appService.search(CollectionEvent.class, new CollectionEvent())
            .size();
        Assert.assertEquals(ceSet1.size() + ceSet2.size(), count);

        DbHelper.deleteCollectionEvents(ceSet1);
        count = appService.search(CollectionEvent.class, new CollectionEvent())
            .size();
        Assert.assertEquals(ceSet2.size(), count);

        DbHelper.deleteCollectionEvents(ceSet2);
        count = appService.search(CollectionEvent.class, new CollectionEvent())
            .size();
        Assert.assertEquals(0, count);
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy("study" + name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);

        CollectionEventWrapper cevent1 = CollectionEventHelper
            .addCollectionEvent(clinic, patient1, 1, SpecimenHelper
                .newSpecimen(DbHelper.chooseRandomlyInList(types)));
        CollectionEventWrapper cevent2 = CollectionEventHelper
            .addCollectionEvent(clinic, patient1, 2, SpecimenHelper
                .newSpecimen(DbHelper.chooseRandomlyInList(types)));

        Assert.assertTrue(cevent1.compareTo(cevent2) < 0);
        Assert.assertTrue(cevent2.compareTo(cevent1) > 0);

        Assert.assertTrue(cevent1.compareTo(null) == 0);
        Assert.assertTrue(cevent2.compareTo(null) == 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy("study" + name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);

        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false);

        Integer visitNumber = 33;

        CollectionEventWrapper cevent1 = CollectionEventHelper
            .addCollectionEvent(clinic, patient1, visitNumber, SpecimenHelper
                .newSpecimen(DbHelper.chooseRandomlyInList(types)));
        cevent1.setVisitNumber(visitNumber + 1);
        cevent1.reset();
        Assert.assertEquals(visitNumber, cevent1.getVisitNumber());
    }

    @Test
    public void testResetNew() throws Exception {
        CollectionEventWrapper cevent = new CollectionEventWrapper(appService);
        cevent.setVisitNumber(120);
        cevent.reset();
        Assert.assertEquals(null, cevent.getVisitNumber());
    }

    @Test
    public void testGetSetEventAttrLabels() throws Exception {
        String name = "testGetSetEventAttrLabels" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name + "CLINIC1");
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        addEventAttrs(study);
        List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        Assert.assertEquals(5, labels.size());

        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(clinic, patient, 1);

        Assert.assertEquals(0, cevent.getEventAttrLabels().length);

        cevent.setEventAttrValue("PMBC Count", "-0.543");
        cevent.setEventAttrValue("Worksheet", "abcdefghi");
        cevent.setEventAttrValue("Date", "1999-12-31 23:59");
        cevent.setEventAttrValue("Consent", "c1;c2;c3");
        cevent.setEventAttrValue("Visit", "v1");
        cevent.persist();

        labels = Arrays.asList(cevent.getEventAttrLabels());
        Assert.assertEquals(5, labels.size());
        Assert.assertTrue(labels.containsAll(Arrays.asList("PMBC Count",
            "Worksheet", "Date", "Consent", "Visit")));

        // set an invalid label
        try {
            cevent.setEventAttrValue("xyz", "abcdef");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testEmptyGetEventAttr() throws Exception {
        String name = "testEmptyGetEventAttr" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name + "CLINIC1");
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        addEventAttrs(study);
        CollectionEventWrapper pevent = CollectionEventHelper
            .addCollectionEvent(clinic, patient, 1);
        pevent.reload();
        List<String> pvAttr = Arrays.asList(pevent.getEventAttrLabels());
        Assert.assertEquals(0, pvAttr.size());
    }

    @Test
    public void testGetEventAttr() throws Exception {
        String name = "testGetEventAttr" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name + "CLINIC1");
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        addEventAttrs(study);
        CollectionEventWrapper pevent = CollectionEventHelper
            .addCollectionEvent(clinic, patient, 1);
        pevent.reload();

        // no values have been set yet, they should return null
        Assert.assertEquals(null, pevent.getEventAttrValue("PMBC Count"));
        Assert.assertEquals(null, pevent.getEventAttrValue("Worksheet"));
        Assert.assertEquals(null, pevent.getEventAttrValue("Date"));
        Assert.assertEquals(null, pevent.getEventAttrValue("Consent"));
        Assert.assertEquals(null, pevent.getEventAttrValue("Visit"));

        // select an invalid EventAttr label
        try {
            pevent.getEventAttrValue("abcdef");
            Assert.fail("should not be query an invalid label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // assign EventAttrs correctly
        String worksheetValue = Utils.getRandomString(10, 20);
        pevent.setEventAttrValue("PMBC Count", "-0.543");
        pevent.setEventAttrValue("Worksheet", worksheetValue);
        pevent.setEventAttrValue("Date", "1999-12-31 23:59");
        pevent.setEventAttrValue("Consent", "c1;c2;c3");
        pevent.setEventAttrValue("Visit", "v1");
        pevent.persist();

        pevent.reload();
        List<String> pvAttr = Arrays.asList(pevent.getEventAttrLabels());
        Assert.assertEquals(5, pvAttr.size());
        Assert.assertEquals("-0.543", pevent.getEventAttrValue("PMBC Count"));
        Assert.assertEquals(worksheetValue,
            pevent.getEventAttrValue("Worksheet"));
        Assert.assertEquals("1999-12-31 23:59",
            pevent.getEventAttrValue("Date"));
        Assert.assertEquals("c1;c2;c3", pevent.getEventAttrValue("Consent"));
        Assert.assertEquals("v1", pevent.getEventAttrValue("Visit"));

        // select an invalid value for a number EventAttr
        try {
            pevent.setEventAttrValue("PMBC Count", "abcdef");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // select an invalid value for a date_time EventAttr
        try {
            pevent.setEventAttrValue("PMBC Count", "1999-12-31 2300:59");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // select an invalid value for a select_multiple EventAttr
        try {
            pevent.setEventAttrValue("Consent", "c2;c99");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // select an invalid value for a select_single EventAttr
        try {
            pevent.setEventAttrValue("Visit", "abcdef");
            Assert.fail("should not be allowed to assign invalid value");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetEventAttrTypeName() throws Exception {
        String name = "testGetEventAttrTypeName" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name + "CLINIC1");
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        addEventAttrs(study);
        List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        Assert.assertEquals(5, labels.size());

        CollectionEventWrapper pevent = CollectionEventHelper
            .addCollectionEvent(clinic, patient, 1);
        pevent.reload();

        // get types before they are assigned on Processing Event
        Assert
            .assertEquals("number", pevent.getEventAttrTypeName("PMBC Count"));
        Assert.assertEquals("text", pevent.getEventAttrTypeName("Worksheet"));
        Assert.assertEquals("date_time", pevent.getEventAttrTypeName("Date"));
        Assert.assertEquals("select_multiple",
            pevent.getEventAttrTypeName("Consent"));
        Assert.assertEquals("select_single",
            pevent.getEventAttrTypeName("Visit"));

        // select an invalid label
        try {
            pevent.getEventAttrTypeName("xyz");
            Assert.fail("should not be allowed get type for invalid label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        pevent.setEventAttrValue("PMBC Count", "-0.543");
        pevent.setEventAttrValue("Worksheet", "abcdefghi");
        pevent.setEventAttrValue("Date", "1999-12-31 23:59");
        pevent.setEventAttrValue("Consent", "c1;c2;c3");
        pevent.setEventAttrValue("Visit", "v1");
        pevent.persist();

        // set value to null
        pevent.setEventAttrValue("PMBC Count", null);
        pevent.persist();

        // get types after they are assigned on Processing Event
        Assert
            .assertEquals("number", pevent.getEventAttrTypeName("PMBC Count"));
        Assert.assertEquals("text", pevent.getEventAttrTypeName("Worksheet"));
        Assert.assertEquals("date_time", pevent.getEventAttrTypeName("Date"));
        Assert.assertEquals("select_multiple",
            pevent.getEventAttrTypeName("Consent"));
        Assert.assertEquals("select_single",
            pevent.getEventAttrTypeName("Visit"));
    }

    @Test
    public void testEventAttrPermissible() throws Exception {
        String name = "testEventAttrPermissible" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name + "CLINIC1");
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        addEventAttrs(study);
        List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        Assert.assertEquals(5, labels.size());

        CollectionEventWrapper pevent = CollectionEventHelper
            .addCollectionEvent(clinic, patient, 1);
        pevent.reload();

        pevent.setEventAttrValue("PMBC Count", "-0.543");
        pevent.setEventAttrValue("Worksheet", "abcdefghi");
        pevent.setEventAttrValue("Date", "1999-12-31 23:59");
        pevent.setEventAttrValue("Consent", "c1;c2;c3");
        pevent.setEventAttrValue("Visit", "v1");
        pevent.persist();
        pevent.reload();

        Assert.assertNull(pevent.getEventAttrPermissible("PMBC Count"));
        Assert.assertNull(pevent.getEventAttrPermissible("Worksheet"));
        Assert.assertNull(pevent.getEventAttrPermissible("Date"));

        List<String> permissibles = Arrays.asList(pevent
            .getEventAttrPermissible("Consent"));
        Assert.assertEquals(3, permissibles.size());
        Assert.assertTrue(permissibles.containsAll(Arrays.asList("c1", "c2",
            "c3")));

        permissibles = Arrays.asList(pevent.getEventAttrPermissible("Visit"));
        Assert.assertEquals(4, permissibles.size());
        Assert.assertTrue(permissibles.containsAll(Arrays.asList("v1", "v2",
            "v3", "v4")));

        // select an invalid label
        try {
            pevent.getEventAttrPermissible("xyz");
            Assert
                .fail("should not be allowed get permissible for invalid label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetSetEventAttrActivityStatus() throws Exception {
        String name = "testGetSetEventAttrActivityStatus" + r.nextInt();
        ClinicWrapper clinic = ClinicHelper.addClinic(name + "CLINIC1");
        PatientWrapper patient = PatientHelper.addPatient(name, study);
        addEventAttrs(study);
        List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        Assert.assertEquals(5, labels.size());

        CollectionEventWrapper pevent = CollectionEventHelper
            .addCollectionEvent(clinic, patient, 1);
        pevent.reload();

        // lock an attribute
        study.setStudyEventAttrActivityStatus("Worksheet",
            ActivityStatusWrapper.getActivityStatus(appService,
                ActivityStatusWrapper.CLOSED_STATUS_STRING));
        study.persist();
        pevent.reload();
        try {
            pevent.setEventAttrValue("Worksheet", "xyz");
            Assert.fail("should not be allowed set value for locked label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        // unlock the attribute
        study.setStudyEventAttrActivityStatus("Worksheet",
            ActivityStatusWrapper.getActiveActivityStatus(appService));
        study.persist();
        pevent.reload();
        pevent.setEventAttrValue("Worksheet", "xyz");
        pevent.persist();
    }

    @Test
    public void testDuplicateEventAttr() throws Exception {
        addEventAttrs(study);
        // FIXME
        // List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        // Assert.assertEquals(5, labels.size());
        //
        // CollectionEventWrapper pevent = CollectionEventHelper
        // .addCollectionEvent(site, patient, TestCommon.getUniqueDate(r),
        // Utils.getRandomDate());
        // pevent.reload();
        //
        // pevent.setEventAttrValue("Worksheet", "abcdefghi");
        // pevent.persist();
        //
        // // change the worksheet value
        // pevent.setEventAttrValue("Worksheet", "jklmnopqr");
        // pevent.persist();
        // pevent.reload();
        //
        // // make sure only one value in database
        // HQLCriteria c = new HQLCriteria(
        // "select pvattr from "
        // + CollectionEvent.class.getName()
        // + " as pv "
        // + "join pv.pvAttrCollection as pvattr "
        // +
        // "join pvattr.studyEventAttr as spvattr where pv.id = ? and spvattr.label= ?",
        // Arrays.asList(new Object[] { pevent.getId(), "Worksheet" }));
        // List<EventAttr> results = appService.query(c);
        // Assert.assertEquals(1, results.size());
    }

    private void addEventAttrs(StudyWrapper study) throws Exception {
        // add PvAtt to study
        Collection<String> types = EventAttrTypeWrapper
            .getAllEventAttrTypesMap(appService).keySet();
        Assert.assertTrue("EventAttrTypes not initialized",
            types.contains("text"));
        study.setStudyEventAttr("PMBC Count", EventAttrTypeEnum.NUMBER);
        study.setStudyEventAttr("Worksheet", EventAttrTypeEnum.TEXT);
        study.setStudyEventAttr("Date", EventAttrTypeEnum.DATE_TIME);
        study.setStudyEventAttr("Consent", EventAttrTypeEnum.SELECT_MULTIPLE,
            new String[] { "c1", "c2", "c3" });
        study.setStudyEventAttr("Visit", EventAttrTypeEnum.SELECT_SINGLE,
            new String[] { "v1", "v2", "v3", "v4" });
        study.persist();
        study.reload();
    }

}
