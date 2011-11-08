package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestStudy extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer siteId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();

        siteId =
            SiteHelper.createSite(appService, name, "Edmonton",
                ActivityStatusEnum.ACTIVE, new HashSet<Integer>());
    }

    @Test
    public void testGetContactCollection() throws Exception {
        Integer studyId =
            StudyHelper
                .createStudy(appService, name, ActivityStatusEnum.ACTIVE);

        int numClinics = r.nextInt(5) + 2;
        int numContacts = 2;
        Set<Integer> clinicIds = ClinicHelper.createClinicsWithContacts(appService, 
            name, numClinics, numContacts);
        
        for (Integer clinicId : clinicIds) {
            
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
