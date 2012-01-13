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
import edu.ualberta.med.biobank.common.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.request.RequestClaimAction;
import edu.ualberta.med.biobank.common.action.request.RequestGetInfoAction;
import edu.ualberta.med.biobank.common.action.request.RequestStateChangeAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.SubmitRequestAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.PatientHelper;
import edu.ualberta.med.biobank.test.action.helper.RequestHelper;
import edu.ualberta.med.biobank.test.action.helper.ResearchGroupHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestRequest extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer studyId;

    private Integer rgId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();
        studyId =
            StudyHelper
                .createStudy(actionExecutor, name, ActivityStatusEnum.ACTIVE);
        rgId =
            ResearchGroupHelper.createResearchGroup(actionExecutor, name + "rg",
                name + "rg",
                studyId);
    }

    @Test
    public void testUpload() throws Exception {

        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = actionExecutor.exec(reader);

        // create specs
        Integer p =
            PatientHelper.createPatient(actionExecutor, name + "_patient",
                rg.rg.getStudy().getId());
        Integer ceId =
            CollectionEventHelper.createCEventWithSourceSpecimens(actionExecutor,
                p, rgId);

        CollectionEventGetInfoAction ceReader =
            new CollectionEventGetInfoAction(ceId);
        CEventInfo ceInfo = actionExecutor.exec(ceReader);
        List<String> specs = new ArrayList<String>();
        for (SpecimenInfo specInfo : ceInfo.sourceSpecimenInfos)
            specs.add(specInfo.specimen.getInventoryId());

        Assert.assertTrue(ceInfo.sourceSpecimenInfos.size() >= 2);
        specs.remove(Math.abs(r.nextInt()) % specs.size());
        specs.remove(Math.abs(r.nextInt()) % specs.size());

        // request specs
        SubmitRequestAction action =
            new SubmitRequestAction(rgId, specs);
        Integer rId = actionExecutor.exec(action).getId();

        // make sure you got what was requested
        RequestGetInfoAction requestGetInfoAction =
            new RequestGetInfoAction(rId);
        RequestReadInfo rInfo = actionExecutor.exec(requestGetInfoAction);

        for (RequestSpecimen spec : rInfo.specimens) {
            Assert.assertTrue(specs.contains(spec.getSpecimen()
                .getInventoryId()));
        }
    }

    @Test
    public void testClaim() throws Exception {
        Integer rId = RequestHelper.createRequest(actionExecutor, rgId);

        RequestGetInfoAction requestGetInfoAction =
            new RequestGetInfoAction(rId);
        RequestReadInfo rInfo = actionExecutor.exec(requestGetInfoAction);

        List<Integer> ids = new ArrayList<Integer>();
        for (RequestSpecimen rs : rInfo.specimens) {
            ids.add(rs.id);
        }

        RequestClaimAction claimAction =
            new RequestClaimAction(ids);
        actionExecutor.exec(claimAction);

        rInfo = actionExecutor.exec(requestGetInfoAction);
        for (RequestSpecimen spec : rInfo.specimens)
            Assert.assertTrue(spec.claimedBy != null
                && !spec.claimedBy.equals(""));

    }

    @Test
    public void testStateChanges() throws Exception {
        Integer rId = RequestHelper.createRequest(actionExecutor, rgId);

        RequestGetInfoAction requestGetInfoAction =
            new RequestGetInfoAction(rId);
        RequestReadInfo rInfo = actionExecutor.exec(requestGetInfoAction);

        List<Integer> ids = new ArrayList<Integer>();
        for (RequestSpecimen rs : rInfo.specimens) {
            ids.add(rs.id);
        }

        RequestStateChangeAction dispatchAction =
            new RequestStateChangeAction(ids,
                RequestSpecimenState.DISPATCHED_STATE);
        actionExecutor.exec(dispatchAction);

        rInfo = actionExecutor.exec(requestGetInfoAction);
        for (RequestSpecimen spec : rInfo.specimens)
            Assert.assertTrue(RequestSpecimenState.getState(spec.getState())
                .equals(RequestSpecimenState.DISPATCHED_STATE));
    }

}
