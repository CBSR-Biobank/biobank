package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.common.action.request.RequestClaimAction;
import edu.ualberta.med.biobank.common.action.request.RequestDeleteAction;
import edu.ualberta.med.biobank.common.action.request.RequestGetInfoAction;
import edu.ualberta.med.biobank.common.action.request.RequestGetSpecimenInfosAction;
import edu.ualberta.med.biobank.common.action.request.RequestStateChangeAction;
import edu.ualberta.med.biobank.common.action.researchGroup.RequestSubmitAction;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.test.action.helper.ContainerHelper;
import edu.ualberta.med.biobank.test.action.helper.RequestHelper;

public class TestRequest extends TestAction {

    private static final Logger log = LoggerFactory.getLogger(TestRequest.class);

    private ResearchGroup researchGroup;

    private enum SubmitActionType {
        NO_STUDY_NO_SITE,
        STUDY_AND_SITE
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session.beginTransaction();
        researchGroup = factory.createResearchGroup();
        session.getTransaction().commit();
    }

    @Test
    public void testUpload() throws Exception {
        for (SubmitActionType actionType: SubmitActionType.values()) {
            List<String> specimenIds = new ArrayList<>();
            session.beginTransaction();
            Container container = factory.createContainer();
            factory.createCollectionEvent();
            for (int i = 0; i < 5; i++) {
                Specimen specimen = factory.createParentSpecimen();
                specimenIds.add(specimen.getInventoryId());
                ContainerHelper.placeSpecimenInContainer(session, specimen, container);
                session.update(specimen);
                log.info("actionType" + actionType + ", specimen added: " + specimen.getInventoryId());
            }
            session.getTransaction().commit();

            Integer reqId = null;

            // request specimens
            switch (actionType) {

            case STUDY_AND_SITE: {
                Study study = factory.getDefaultStudy();
                Site site = factory.getDefaultSite();
                reqId = exec(new RequestSubmitAction(researchGroup.getId(),
                                                     specimenIds,
                                                     Arrays.asList(study.getId().toString()),
                                                     site.getId())).getId();
                break;
            }

            case NO_STUDY_NO_SITE: {
                reqId = exec(new RequestSubmitAction(researchGroup.getId(), specimenIds)).getId();
                break;
            }

            }

            // make sure you got what was requested
            List<Object[]> specData = exec(new RequestGetSpecimenInfosAction(reqId)).getList();

            for (Object[] specInfo : specData) {
                RequestSpecimen spec = (RequestSpecimen) specInfo[0];
                Assert.assertTrue(specimenIds.contains(spec.getSpecimen().getInventoryId()));
            }
        }
    }

    @Test
    public void testClaim() throws Exception {
        Integer rId = RequestHelper.createRequest(session, getExecutor(), researchGroup);

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
        Integer rId = RequestHelper.createRequest(session, getExecutor(), researchGroup);

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
            Assert.assertTrue(RequestSpecimenState.DISPATCHED_STATE == spec
                .getState());
        }
    }

    @Test
    public void testDelete() throws Exception {
        Integer rId = RequestHelper.createRequest(session, getExecutor(), researchGroup);

        RequestReadInfo reqInfo = exec(new RequestGetInfoAction(rId));
        RequestDeleteAction delete = new RequestDeleteAction(reqInfo.request);
        exec(delete);

        rId = RequestHelper.createRequest(session, getExecutor(), researchGroup);
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
