package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.aliquotedspecimen.AliquotedSpecimenSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetContactsAction;
import edu.ualberta.med.biobank.common.action.sourcespecimen.SourceSpecimenSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TestStudy extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer siteId;
    private Integer studyId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();
        siteId =
            SiteHelper.createSite(appService, name, "Edmonton",
                ActivityStatusEnum.ACTIVE, new HashSet<Integer>());
        studyId =
            StudyHelper
                .createStudy(appService, name, ActivityStatusEnum.ACTIVE);
    }

    @Test
    public void testGetContactCollection() throws Exception {
        // check for empty contact list after creation of study
        Assert.assertTrue(getStudyContacts().isEmpty());

        int numClinics = r.nextInt(5) + 2;
        int numContacts = 2;
        Set<Integer> clinicIds =
            ClinicHelper.createClinicsWithContacts(appService,
                name, numClinics, numContacts);

        List<Contact> studyContactsSet1 = new ArrayList<Contact>();
        List<Contact> studyContactsSet2 = new ArrayList<Contact>();
        List<Contact> expectedStudyContacts = new ArrayList<Contact>();

        // get a contact id from each clinic
        for (Integer clinicId : clinicIds) {
            ArrayList<Contact> contacts =
                appService.doAction(new ClinicGetContactsAction(clinicId));
            Assert.assertNotNull(contacts);
            Assert.assertNotNull(contacts.get(0));
            Assert.assertNotNull(contacts.get(1));
            studyContactsSet1.add(contacts.get(0));
            studyContactsSet2.add(contacts.get(1));
        }

        // add a contact one by one from set 1
        for (Contact c : studyContactsSet1) {
            studyAddContacts(Arrays.asList(c));
            expectedStudyContacts.add(c);
            Assert.assertEquals(expectedStudyContacts, getStudyContacts());
        }

        // add contact set 2
        studyAddContacts(studyContactsSet2);
        expectedStudyContacts.addAll(studyContactsSet2);
        Assert.assertEquals(expectedStudyContacts, getStudyContacts());

        // remove all contacts from study randomly
        for (Contact c : studyContactsSet1) {

        }
    }

    private void studyAddContacts(List<Contact> contacts)
        throws ApplicationException {
        for (Contact c : contacts) {
            StudyInfo studyInfo =
                appService.doAction(new StudyGetInfoAction(studyId));
            studyInfo.contacts.add(c);
            StudySaveAction studySave =
                StudyHelper.getSaveAction(appService, studyInfo);
            appService.doAction(studySave);
        }
    }

    private List<Contact> getStudyContacts() throws ApplicationException {
        StudyInfo studyInfo = appService.doAction(new
            StudyGetInfoAction(studyId));
        return studyInfo.contacts;
    }

    @Test
    public void testSourceSpecimens() throws Exception {
        openHibernateSession();
        Query q = session.createQuery("from " + SpecimenType.class.getName());
        @SuppressWarnings("unchecked")
        List<SpecimenType> spcTypes = q.list();
        closeHibernateSession();

        List<Integer> srcSpcIds = new ArrayList<Integer>();
        for (int i = 0, n = r.nextInt(5) + 2; i < n; ++i) {
            SourceSpecimenSaveAction srcSpcSaveAction =
                new SourceSpecimenSaveAction();
            srcSpcSaveAction.setNeedOriginalVolume(r.nextBoolean());
            srcSpcSaveAction.setStudyId(studyId);
            srcSpcSaveAction.setSpecimenTypeId(spcTypes.get(
                r.nextInt(spcTypes.size()))
                .getId());
            srcSpcIds.add(appService.doAction(srcSpcSaveAction));
        }

        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));

        List<Integer> actualSrcSpcIds = new ArrayList<Integer>();
        for (SourceSpecimen srcSpc : studyInfo.sourceSpcs) {
            actualSrcSpcIds.add(srcSpc.getId());
        }

        Assert.assertEquals(srcSpcIds, actualSrcSpcIds);

        // TODO: test removal of source specimens from study
    }

    @Test
    public void testAliquotedSpecimens() throws Exception {
        openHibernateSession();
        Query q = session.createQuery("from " + SpecimenType.class.getName());
        @SuppressWarnings("unchecked")
        List<SpecimenType> spcTypes = q.list();
        closeHibernateSession();

        List<Integer> aqSpcIds = new ArrayList<Integer>();
        for (int i = 0, n = r.nextInt(5) + 2; i < n; ++i) {
            AliquotedSpecimenSaveAction aqSpcSaveAction =
                new AliquotedSpecimenSaveAction();
            aqSpcSaveAction.setQuantity(r.nextInt());
            aqSpcSaveAction.setVolume(r.nextDouble());
            aqSpcSaveAction.setStudyId(studyId);
            aqSpcSaveAction.setActivityStatusId(ActivityStatusEnum.ACTIVE
                .getId());
            aqSpcSaveAction.setSpecimenTypeId(spcTypes.get(
                r.nextInt(spcTypes.size()))
                .getId());
            aqSpcIds.add(appService.doAction(aqSpcSaveAction));
        }

        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));

        List<Integer> actualAqSpcIds = new ArrayList<Integer>();
        for (AliquotedSpecimen aqSpc : studyInfo.aliquotedSpcs) {
            actualAqSpcIds.add(aqSpc.getId());
        }

        Assert.assertEquals(aqSpcIds, actualAqSpcIds);

        // TODO: test removal of source specimens from study

    }
}
