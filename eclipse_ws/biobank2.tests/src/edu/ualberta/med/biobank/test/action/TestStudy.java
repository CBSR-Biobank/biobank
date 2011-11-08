package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
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
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.sourcespecimen.SourceSpecimenSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction;
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
        int numClinics = r.nextInt(5) + 2;
        int numContacts = 2;
        Set<Integer> clinicIds =
            ClinicHelper.createClinicsWithContacts(appService,
                name, numClinics, numContacts);

        StudyInfo studyInfo =
            appService.doAction(new StudyGetInfoAction(studyId));
        Set<Contact> expectedStudyContacts = new HashSet<Contact>();

        // get a contact ids from each clinic
        List<StudyGetClinicInfoAction.ClinicInfo> clinicInfos =
            new ArrayList<StudyGetClinicInfoAction.ClinicInfo>();
        for (Integer clinicId : clinicIds) {
            ClinicInfo clinicInfo =
                appService.doAction(new ClinicGetInfoAction(clinicId));
            List<Contact> contacts =
                new ArrayList<Contact>(clinicInfo.clinic.getContactCollection());
            Assert.assertNotNull(contacts);
            clinicInfos.add(new StudyGetClinicInfoAction.ClinicInfo(
                clinicInfo.clinic, 0L, 0L, contacts.get(0)));
            expectedStudyContacts.add(contacts.get(0));
        }
        studyInfo.setClinicInfos(clinicInfos);

        StudySaveAction studySave =
            StudyHelper.getSaveAction(appService, studyInfo);
        appService.doAction(studySave);

        studyInfo = appService.doAction(new StudyGetInfoAction(studyId));

        Set<Contact> actualStudyContacts = new HashSet<Contact>();
        for (StudyGetClinicInfoAction.ClinicInfo clinicInfo : studyInfo.clinicInfos) {
            actualStudyContacts.add(clinicInfo.getContact());
        }

        Assert.assertEquals(expectedStudyContacts, actualStudyContacts);

        // TODO: test removal of contacts from study
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
