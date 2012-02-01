package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.AddressSaveInfo;
import edu.ualberta.med.biobank.common.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.common.action.request.RequestGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupDeleteAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupSaveAction;
import edu.ualberta.med.biobank.common.action.researchGroup.SubmitRequestAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.PatientHelper;
import edu.ualberta.med.biobank.test.action.helper.RequestHelper;
import edu.ualberta.med.biobank.test.action.helper.ResearchGroupHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestResearchGroup extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer studyId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + R.nextInt();
        studyId =
            StudyHelper
                .createStudy(EXECUTOR, name, ActivityStatusEnum.ACTIVE);
    }

    @Test
    public void saveResearchGroup() throws Exception {

        Integer rgId =
            ResearchGroupHelper.createResearchGroup(EXECUTOR, name,
                name,
                studyId);

        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = EXECUTOR.exec(reader);

        Assert.assertTrue(rg.rg.getName().equals(name + "rg"));
        Assert.assertTrue(rg.rg.getNameShort().equals(name + "rg"));
        Assert.assertTrue(rg.rg.getStudy().getId().equals(studyId));
        Assert.assertTrue(rg.rg.getActivityStatus().getId()
            .equals(ActivityStatusEnum.ACTIVE.getId()));

    }

    @Test
    public void testUpload() throws Exception {
        Integer rgId =
            ResearchGroupHelper.createResearchGroup(EXECUTOR, name + "rg",
                name + "rg",
                studyId);
        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = EXECUTOR.exec(reader);

        // create specs
        Integer p =
            PatientHelper.createPatient(EXECUTOR, name + "_patient",
                rg.rg.getStudy().getId());
        Integer ceId =
            CollectionEventHelper.createCEventWithSourceSpecimens(EXECUTOR,
                p, rgId);

        CollectionEventGetInfoAction ceReader =
            new CollectionEventGetInfoAction(ceId);
        CEventInfo ceInfo = EXECUTOR.exec(ceReader);
        List<String> specs = new ArrayList<String>();
        for (SpecimenInfo specInfo : ceInfo.sourceSpecimenInfos)
            specs.add(specInfo.specimen.getInventoryId());

        Assert.assertTrue(ceInfo.sourceSpecimenInfos.size() >= 2);
        specs.remove(Math.abs(R.nextInt()) % specs.size());
        specs.remove(Math.abs(R.nextInt()) % specs.size());

        // request specs
        SubmitRequestAction action =
            new SubmitRequestAction(rgId, specs);
        Integer rId = EXECUTOR.exec(action).getId();

        // make sure you got what was requested
        RequestGetInfoAction requestGetInfoAction =
            new RequestGetInfoAction(rId);
        RequestReadInfo rInfo = EXECUTOR.exec(requestGetInfoAction);

        for (RequestSpecimen spec : rInfo.specimens) {
            Assert.assertTrue(specs.contains(spec.getSpecimen()
                .getInventoryId()));
        }
    }

    @Test
    public void testDelete() throws Exception {
        // only one failure case specific to rg, rest are in center

        Integer rgId =
            ResearchGroupHelper.createResearchGroup(EXECUTOR, name,
                name,
                studyId);
        Integer rId = RequestHelper.createRequest(EXECUTOR, rgId);
        ResearchGroupDeleteAction delete =
            new ResearchGroupDeleteAction(rgId);
        try {
            EXECUTOR.exec(delete);
            Assert.fail();
        } catch (ActionException e) {
            System.out.println(e);
        }

        session.beginTransaction();
        Request r = (Request) session.load(Request.class, rId);
        session.delete(r);
        session.getTransaction().commit();

        EXECUTOR.exec(delete);
        // should be fine
    }

    @Test
    public void testComment() throws Exception {
        AddressSaveInfo addressSaveInfo =
            new AddressSaveInfo(null, "test", "test", "test", "test", "test",
                "test", "test", "test", "test", "test");
        ResearchGroupSaveInfo save =
            new ResearchGroupSaveInfo(null, name + "rg", name + "rg",
                studyId, "comment", addressSaveInfo,
                ActivityStatusEnum.ACTIVE.getId());
        ResearchGroupSaveAction rgSave = new ResearchGroupSaveAction(save);

        Integer rgId = EXECUTOR.exec(rgSave).getId();
        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = EXECUTOR.exec(reader);

        save.id = rgId;

        Assert.assertEquals(1, rg.rg.getCommentCollection().size());
        EXECUTOR.exec(rgSave);
        rg = EXECUTOR.exec(reader);
        Assert.assertEquals(2, rg.rg.getCommentCollection().size());
        EXECUTOR.exec(rgSave);
        rg = EXECUTOR.exec(reader);
        Assert.assertEquals(3, rg.rg.getCommentCollection().size());
    }
}
