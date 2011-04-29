package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrTypeWrapper;
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

        OriginInfoWrapper originInfo = new OriginInfoWrapper(appService);
        originInfo.setCenter(site);
        originInfo.persist();
        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(site, patient, 1, originInfo, spc);

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
                .addCollectionEvent(site, patient, 1, originInfo, spc);
            cevent.reload();
            Assert.assertEquals(patient, cevent.getPatient());
        }
    }

    @Test
    public void testRemoveSpecimens() throws Exception {
        String name = "testRemoveSpecimens" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite("site" + name);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy("study" + name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        OriginInfoWrapper oi = new OriginInfoWrapper(appService);
        oi.setCenter(clinic);
        oi.persist();
        SpecimenTypeWrapper type = SpecimenTypeWrapper.getAllSpecimenTypes(
            appService, false).get(0);
        SpecimenWrapper[] newSpecs = new SpecimenWrapper[r.nextInt(10) + 3];
        for (int i = 0; i < newSpecs.length; i++) {
            newSpecs[i] = SpecimenHelper.newSpecimen(type);
        }
        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(site, patient1, 1, oi, newSpecs);

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
        SiteWrapper site = SiteHelper.addSite("site" + name);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy("study" + name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        // FIXME
        // CollectionEventHelper.addCollectionEvent(site, method,
        // SpecimenHelper.newSpecimen(patient1, Utils.getRandomDate(), 0.1));
        // CollectionEventWrapper cevent = CollectionEventHelper
        // .addCollectionEvent(site, method, SpecimenHelper.newSpecimen(
        // patient1, Utils.getRandomDate(), 0.1));
        // CollectionEventHelper.addCollectionEvent(site, method,
        // SpecimenHelper.newSpecimen(patient1, Utils.getRandomDate(), 0.1));
        //
        // int countBefore = appService.search(CollectionEvent.class,
        // new CollectionEvent()).size();
        //
        // for (SpecimenWrapper spc : cevent.getSpecimenCollection(false)) {
        // spc.delete();
        // }
        //
        // cevent.reload();
        // cevent.delete();
        //
        // int countAfter = appService.search(CollectionEvent.class,
        // new CollectionEvent()).size();
        //
        // Assert.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite("site" + name);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy("study" + name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        // FIXME
        // CollectionEventWrapper cevent1 = CollectionEventHelper
        // .addCollectionEvent(site, method, SpecimenHelper.newSpecimen(
        // patient1, Utils.getRandomDate(), 0.1));
        // cevent1.setDateReceived(DateFormatter.dateFormatter
        // .parse("2010-02-01 23:00"));
        // CollectionEventWrapper cevent2 = CollectionEventHelper
        // .addCollectionEvent(site, method, SpecimenHelper.newSpecimen(
        // patient1, Utils.getRandomDate(), 0.1));
        // cevent2.setDateReceived(DateFormatter.dateFormatter
        // .parse("2009-12-01 23:00"));
        //
        // Assert.assertTrue(cevent1.compareTo(cevent2) > 0);
        // Assert.assertTrue(cevent2.compareTo(cevent1) < 0);
        //
        // Assert.assertTrue(cevent1.compareTo(null) == 0);
        // Assert.assertTrue(cevent2.compareTo(null) == 0);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite("site" + name);
        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name);
        StudyWrapper study = StudyHelper.addStudy("study" + name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        PatientWrapper patient1 = PatientHelper.addPatient(name, study);
        // FIXME
        // CollectionEventWrapper cevent1 = CollectionEventHelper
        // .addCollectionEvent(
        // site,
        // ShippingMethodWrapper.getShippingMethods(appService).get(0),
        // SpecimenHelper.newSpecimen(patient1, Utils.getRandomDate(), 0.1));
        // String oldWaybill = cevent1.getWaybill();
        // cevent1.setWaybill("QQQQ");
        // cevent1.reset();
        // Assert.assertEquals(oldWaybill, cevent1.getWaybill());
    }

    @Test
    public void testResetNew() throws Exception {
        CollectionEventWrapper cevent = new CollectionEventWrapper(appService);
        cevent.setVisitNumber(120);
        cevent.reset();
        Assert.assertEquals(null, cevent.getVisitNumber());
    }

    @Test
    public void testGetTodaycevents() throws Exception {
        String name = "testTodaycevents_" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ClinicWrapper clinic1 = ClinicHelper.addClinic(name + "_1");
        ClinicWrapper clinic2 = ClinicHelper.addClinic(name + "_2");
        StudyWrapper study = StudyHelper.addStudy(name);
        ContactWrapper contact1 = ContactHelper.addContact(clinic1, name);
        ContactWrapper contact2 = ContactHelper.addContact(clinic2, name);
        study.addToContactCollection(Arrays.asList(contact1, contact2));
        study.persist();

        PatientWrapper patient1 = PatientHelper.addPatient(name + "_1", study);
        PatientWrapper patient2 = PatientHelper.addPatient(name + "_2", study);

        ShippingMethodWrapper method = ShippingMethodWrapper
            .getShippingMethods(appService).get(0);
        // FIXME
        // CollectionEventHelper.addCollectionEvent(site, method,
        // SpecimenHelper.newSpecimen(patient1, Utils.getRandomDate(), 0.1)); //
        // another
        // // day
        // CollectionEventWrapper cevent2 = CollectionEventHelper
        // .newCollectionEvent(site, method, "waybill_" + name + "_2",
        // new Date(), SpecimenHelper.newSpecimen(patient1,
        // Utils.getRandomDate(), 0.1)); // today
        // cevent2.persist();
        // CollectionEventWrapper cevent3 = CollectionEventHelper
        // .newCollectionEvent(site, method, "waybill_" + name + "_3",
        // new Date(), SpecimenHelper.newSpecimen(patient2,
        // Utils.getRandomDate(), 0.1)); // today
        // cevent3.persist();
        //
        // List<CollectionEventWrapper> ships = CollectionEventWrapper
        // .getTodayCollectionEvents(appService);
        // Assert.assertEquals(2, ships.size());
        // Assert.assertTrue(ships.contains(cevent2));
        // Assert.assertTrue(ships.contains(cevent3));
    }

    @Test
    public void testGetSetEventAttrLabels() throws Exception {
        // addEventAttrs(study);
        // FIXME
        // List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        // Assert.assertEquals(5, labels.size());
        //
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
        // Utils.getRandomDate());
        // pevent.reload();
        //
        // Assert.assertEquals(0, pevent.getEventAttrLabels().length);
        //
        // pevent.setEventAttrValue("PMBC Count", "-0.543");
        // pevent.setEventAttrValue("Worksheet", "abcdefghi");
        // pevent.setEventAttrValue("Date", "1999-12-31 23:59");
        // pevent.setEventAttrValue("Consent", "c1;c2;c3");
        // pevent.setEventAttrValue("Visit", "v1");
        // pevent.persist();
        //
        // labels = Arrays.asList(pevent.getEventAttrLabels());
        // Assert.assertEquals(5, labels.size());
        // Assert.assertTrue(labels.containsAll(Arrays.asList("PMBC Count",
        // "Worksheet", "Date", "Consent", "Visit")));
        //
        // // set an invalid label
        // try {
        // pevent.setEventAttrValue("xyz", "abcdef");
        // Assert.fail("should not be allowed to assign invalid value");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
    }

    @Test
    public void testEmptyGetEventAttr() throws Exception {
        // FIXME
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
        // Utils.getRandomDate());
        // pevent.reload();
        // List<String> pvAttr = Arrays.asList(pevent.getEventAttrLabels());
        // Assert.assertEquals(0, pvAttr.size());
    }

    @Test
    public void testGetEventAttr() throws Exception {
        // addEventAttrs(study);
        // FIXME
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
        // Utils.getRandomDate());
        // pevent.reload();
        //
        // // no values have been set yet, they should return null
        // Assert.assertEquals(null, pevent.getEventAttrValue("PMBC Count"));
        // Assert.assertEquals(null, pevent.getEventAttrValue("Worksheet"));
        // Assert.assertEquals(null, pevent.getEventAttrValue("Date"));
        // Assert.assertEquals(null, pevent.getEventAttrValue("Consent"));
        // Assert.assertEquals(null, pevent.getEventAttrValue("Visit"));
        //
        // // select an invalid EventAttr label
        // try {
        // pevent.getEventAttrValue("abcdef");
        // Assert.fail("should not be query an invalid label");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
        //
        // // assign EventAttrs correctly
        // String worksheetValue = Utils.getRandomString(10, 20);
        // pevent.setEventAttrValue("PMBC Count", "-0.543");
        // pevent.setEventAttrValue("Worksheet", worksheetValue);
        // pevent.setEventAttrValue("Date", "1999-12-31 23:59");
        // pevent.setEventAttrValue("Consent", "c1;c2;c3");
        // pevent.setEventAttrValue("Visit", "v1");
        // pevent.persist();
        //
        // pevent.reload();
        // List<String> pvAttr = Arrays.asList(pevent.getEventAttrLabels());
        // Assert.assertEquals(5, pvAttr.size());
        // Assert.assertEquals("-0.543",
        // pevent.getEventAttrValue("PMBC Count"));
        // Assert.assertEquals(worksheetValue,
        // pevent.getEventAttrValue("Worksheet"));
        // Assert.assertEquals("1999-12-31 23:59",
        // pevent.getEventAttrValue("Date"));
        // Assert.assertEquals("c1;c2;c3", pevent.getEventAttrValue("Consent"));
        // Assert.assertEquals("v1", pevent.getEventAttrValue("Visit"));
        //
        // // select an invalid value for a number EventAttr
        // try {
        // pevent.setEventAttrValue("PMBC Count", "abcdef");
        // Assert.fail("should not be allowed to assign invalid value");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
        //
        // // select an invalid value for a date_time EventAttr
        // try {
        // pevent.setEventAttrValue("PMBC Count", "1999-12-31 2300:59");
        // Assert.fail("should not be allowed to assign invalid value");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
        //
        // // select an invalid value for a select_multiple EventAttr
        // try {
        // pevent.setEventAttrValue("Consent", "c2;c99");
        // Assert.fail("should not be allowed to assign invalid value");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
        //
        // // select an invalid value for a select_single EventAttr
        // try {
        // pevent.setEventAttrValue("Visit", "abcdef");
        // Assert.fail("should not be allowed to assign invalid value");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
    }

    @Test
    public void testGetEventAttrTypeName() throws Exception {
        // addEventAttrs(study);
        // FIXME
        // List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        // Assert.assertEquals(5, labels.size());
        //
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
        // Utils.getRandomDate());
        // pevent.reload();
        //
        // // get types before they are assigned on Processing Event
        // Assert.assertEquals("number",
        // pevent.getEventAttrTypeName("PMBC Count"));
        // Assert.assertEquals("text",
        // pevent.getEventAttrTypeName("Worksheet"));
        // Assert.assertEquals("date_time",
        // pevent.getEventAttrTypeName("Date"));
        // Assert.assertEquals("select_multiple",
        // pevent.getEventAttrTypeName("Consent"));
        // Assert.assertEquals("select_single",
        // pevent.getEventAttrTypeName("Visit"));
        //
        // // select an invalid label
        // try {
        // pevent.getEventAttrTypeName("xyz");
        // Assert.fail("should not be allowed get type for invalid label");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
        //
        // pevent.setEventAttrValue("PMBC Count", "-0.543");
        // pevent.setEventAttrValue("Worksheet", "abcdefghi");
        // pevent.setEventAttrValue("Date", "1999-12-31 23:59");
        // pevent.setEventAttrValue("Consent", "c1;c2;c3");
        // pevent.setEventAttrValue("Visit", "v1");
        // pevent.persist();
        //
        // // set value to null
        // pevent.setEventAttrValue("PMBC Count", null);
        // pevent.persist();
        //
        // // get types after they are assigned on Processing Event
        // Assert.assertEquals("number",
        // pevent.getEventAttrTypeName("PMBC Count"));
        // Assert.assertEquals("text",
        // pevent.getEventAttrTypeName("Worksheet"));
        // Assert.assertEquals("date_time",
        // pevent.getEventAttrTypeName("Date"));
        // Assert.assertEquals("select_multiple",
        // pevent.getEventAttrTypeName("Consent"));
        // Assert.assertEquals("select_single",
        // pevent.getEventAttrTypeName("Visit"));
    }

    @Test
    public void testEventAttrPermissible() throws Exception {
        // addEventAttrs(study);
        // FIXME
        // List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        // Assert.assertEquals(5, labels.size());
        //
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
        // Utils.getRandomDate());
        // pevent.reload();
        //
        // pevent.setEventAttrValue("PMBC Count", "-0.543");
        // pevent.setEventAttrValue("Worksheet", "abcdefghi");
        // pevent.setEventAttrValue("Date", "1999-12-31 23:59");
        // pevent.setEventAttrValue("Consent", "c1;c2;c3");
        // pevent.setEventAttrValue("Visit", "v1");
        // pevent.persist();
        // pevent.reload();
        //
        // Assert.assertEquals(null,
        // pevent.getEventAttrPermissible("PMBC Count"));
        // Assert.assertEquals(null,
        // pevent.getEventAttrPermissible("Worksheet"));
        // Assert.assertEquals(null, pevent.getEventAttrPermissible("Date"));
        //
        // List<String> permissibles = Arrays.asList(pevent
        // .getEventAttrPermissible("Consent"));
        // Assert.assertEquals(3, permissibles.size());
        // Assert.assertTrue(permissibles.containsAll(Arrays.asList("c1", "c2",
        // "c3")));
        //
        // permissibles =
        // Arrays.asList(pevent.getEventAttrPermissible("Visit"));
        // Assert.assertEquals(4, permissibles.size());
        // Assert.assertTrue(permissibles.containsAll(Arrays.asList("v1", "v2",
        // "v3", "v4")));
        //
        // // select an invalid label
        // try {
        // pevent.getEventAttrPermissible("xyz");
        // Assert
        // .fail("should not be allowed get permissible for invalid label");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
    }

    @Test
    public void testGetSetEventAttrActivityStatus() throws Exception {
        // addEventAttrs(study);
        // FIXME
        // List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        // Assert.assertEquals(5, labels.size());
        //
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
        // Utils.getRandomDate());
        // pevent.reload();
        //
        // // lock an attribute
        // study.setStudyEventAttrActivityStatus("Worksheet",
        // ActivityStatusWrapper
        // .getActivityStatus(appService,
        // ActivityStatusWrapper.CLOSED_STATUS_STRING));
        // study.persist();
        // pevent.reload();
        // try {
        // pevent.setEventAttrValue("Worksheet", "xyz");
        // Assert.fail("should not be allowed set value for locked label");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
        //
        // // unlock the attribute
        // study.setStudyEventAttrActivityStatus("Worksheet",
        // ActivityStatusWrapper.getActiveActivityStatus(appService));
        // study.persist();
        // pevent.reload();
        // pevent.setEventAttrValue("Worksheet", "xyz");
        // pevent.persist();
    }

    @Test
    public void testDuplicateEventAttr() throws Exception {
        addEventAttrs(study);
        // FIXME
        // List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        // Assert.assertEquals(5, labels.size());
        //
        // ProcessingEventWrapper pevent = ProcessingEventHelper
        // .addProcessingEvent(site, patient, TestCommon.getUniqueDate(r),
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
        // + ProcessingEvent.class.getName()
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
