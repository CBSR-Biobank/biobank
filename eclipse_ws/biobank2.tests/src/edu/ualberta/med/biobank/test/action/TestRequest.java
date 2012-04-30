package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.request.RequestClaimAction;
import edu.ualberta.med.biobank.common.action.request.RequestDeleteAction;
import edu.ualberta.med.biobank.common.action.request.RequestGetInfoAction;
import edu.ualberta.med.biobank.common.action.request.RequestGetSpecimenInfosAction;
import edu.ualberta.med.biobank.common.action.request.RequestStateChangeAction;
import edu.ualberta.med.biobank.common.action.researchGroup.RequestSubmitAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Request;
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
        name = testname.getMethodName() + getR().nextInt();
        studyId =
            StudyHelper
                .createStudy(getExecutor(), name, ActivityStatus.ACTIVE);
        rgId =
            ResearchGroupHelper.createResearchGroup(getExecutor(),
                name + "rg",
                name + "rg",
                studyId);
    }

    @Test
    public void testUpload() throws Exception {

        ResearchGroupGetInfoAction rgInfo =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = exec(rgInfo);

        // create specs
        Integer p =
            PatientHelper.createPatient(getExecutor(), name + "_patient",
                rg.researchGroup.getStudy().getId());
        Integer ceId =
            CollectionEventHelper.createCEventWithSourceSpecimens(
                getExecutor(),
                p, rgId);

        CollectionEventGetInfoAction ceReader =
            new CollectionEventGetInfoAction(ceId);
        CEventInfo ceInfo = exec(ceReader);
        List<String> specs = new ArrayList<String>();
        for (SpecimenInfo specInfo : ceInfo.sourceSpecimenInfos)
            specs.add(specInfo.specimen.getInventoryId());

        Assert.assertTrue(ceInfo.sourceSpecimenInfos.size() >= 2);
        specs.remove(Math.abs(getR().nextInt()) % specs.size());
        specs.remove(Math.abs(getR().nextInt()) % specs.size());

        // request specs
        RequestSubmitAction action =
            new RequestSubmitAction(rgId, specs);
        Integer rId = exec(action).getId();

        // make sure you got what was requested
        RequestGetSpecimenInfosAction specAction =
            new RequestGetSpecimenInfosAction(rId);
        List<Object[]> specInfo = exec(specAction).getList();

        for (int i = 0; i < specInfo.size(); i++) {
            RequestSpecimen spec = (RequestSpecimen) specInfo.get(i)[0];
            Assert.assertTrue(specs.contains(spec.getSpecimen()
                .getInventoryId()));
        }
    }

    @Test
    public void testClaim() throws Exception {
        Integer rId = RequestHelper.createRequest(session, getExecutor(), rgId);

        RequestGetSpecimenInfosAction specAction =
            new RequestGetSpecimenInfosAction(rId);
        List<Object[]> specInfo = exec(specAction).getList();
        List<Integer> ids = new ArrayList<Integer>();

        for (int i = 0; i < specInfo.size(); i++) {
            RequestSpecimen spec = (RequestSpecimen) specInfo.get(i)[0];
            ids.add(spec.getId());
        }

        RequestClaimAction claimAction =
            new RequestClaimAction(ids);
        exec(claimAction);

        specInfo = exec(specAction).getList();
        for (int i = 0; i < specInfo.size(); i++) {
            RequestSpecimen spec = (RequestSpecimen) specInfo.get(i)[0];
            Assert.assertTrue(spec.getClaimedBy() != null
                && !spec.getClaimedBy().equals(""));
        }

    }

    @Test
    public void testStateChanges() throws Exception {
        Integer rId = RequestHelper.createRequest(session, getExecutor(), rgId);

        RequestGetSpecimenInfosAction specAction =
            new RequestGetSpecimenInfosAction(rId);
        List<Object[]> specInfo = exec(specAction).getList();
        List<Integer> ids = new ArrayList<Integer>();

        for (int i = 0; i < specInfo.size(); i++) {
            RequestSpecimen spec = (RequestSpecimen) specInfo.get(i)[0];
            ids.add(spec.getId());
        }

        RequestStateChangeAction dispatchAction =
            new RequestStateChangeAction(ids,
                RequestSpecimenState.DISPATCHED_STATE);
        exec(dispatchAction);

        specInfo = exec(specAction).getList();
        for (int i = 0; i < specInfo.size(); i++) {
            RequestSpecimen spec = (RequestSpecimen) specInfo.get(i)[0];
            Assert.assertTrue(RequestSpecimenState.getState(spec.getState())
                .equals(RequestSpecimenState.DISPATCHED_STATE));
        }
    }

    @Test
    public void testDelete() throws Exception {
        Integer rId = RequestHelper.createRequest(session, getExecutor(), rgId);

        RequestReadInfo reqInfo = exec(new RequestGetInfoAction(rId));
        RequestDeleteAction delete = new RequestDeleteAction(reqInfo.request);
        exec(delete);

        rId = RequestHelper.createRequest(session, getExecutor(), rgId);
        session.beginTransaction();
        Request r = (Request) session.get(Request.class, rId);
        r.setSubmitted(new Date());
        session.saveOrUpdate(r);
        session.getTransaction().commit();
        reqInfo = exec(new RequestGetInfoAction(rId));
        delete = new RequestDeleteAction(reqInfo.request);

        try {
            exec(delete);
            Assert.fail("should not be allowed to delete the request");
        } catch (ConstraintViolationException e) {
            // good
        }
    }

}
