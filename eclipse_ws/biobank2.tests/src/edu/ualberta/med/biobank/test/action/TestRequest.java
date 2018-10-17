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
import edu.ualberta.med.biobank.common.action.request.RequestRetrievalAction;
import edu.ualberta.med.biobank.common.action.request.RequestStateChangeAction;
import edu.ualberta.med.biobank.common.action.request.RequestSubmitAction;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
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
    public void requestClaim() throws Exception {
        Request request = RequestHelper.createRequest(session, factory, researchGroup);
        exec(new RequestClaimAction(getRequestSpecimenIds(request)));

        session.clear();
        request = (Request) session.get(Request.class, request.getId());
        for (RequestSpecimen rs : request.getRequestSpecimens()) {
            Assert.assertNotNull(rs.getClaimedBy());
            Assert.assertFalse(rs.getClaimedBy().isEmpty());
        }

    }

    @Test
    public void requestDelete() throws Exception {
        Request request = RequestHelper.createRequest(session, factory, researchGroup);
        exec(new RequestDeleteAction(request));

        session.clear();
        Request dbCheck = (Request) session.get(Request.class, request.getId());
        Assert.assertNull(dbCheck);
    }

    @Test
    public void requestDeleteNotAllowed() throws Exception {
        Request request = RequestHelper.createRequest(session, factory, researchGroup);

        session.beginTransaction();
        request.setSubmitted(new Date());
        session.saveOrUpdate(request);
        session.getTransaction().commit();

        try {
            exec(new RequestDeleteAction(request));
            Assert.fail("should not be allowed to delete the request");
        } catch (ConstraintViolationException e) {
            // intentionally empty
        }
    }

    @Test
    public void requestGetInfo() throws Exception {
        Request request = RequestHelper.createRequest(session, factory, researchGroup);
        Dispatch dispatch = RequestHelper.requestAddDispatch(session, factory, request);

        List<Integer> specimenIds = new ArrayList<>(0);
        for (RequestSpecimen rs : request.getRequestSpecimens()) {
            specimenIds.add(rs.getSpecimen().getId());
        }

        RequestReadInfo info = exec(new RequestGetInfoAction(request.getId()));

        Assert.assertEquals(request.getId(), info.request.getId());
        Assert.assertEquals(request.getRequestSpecimens().size(),
                            info.request.getRequestSpecimens().size());

        Assert.assertEquals(request.getAddress().getCity(), info.request.getAddress().getCity());

        for (RequestSpecimen rs : info.request.getRequestSpecimens()) {
            Assert.assertTrue(specimenIds.contains(rs.getSpecimen().getId()));
        }

        Assert.assertEquals(request.getDispatches().size(), info.request.getDispatches().size());

        for (Dispatch d : info.request.getDispatches()) {
            Assert.assertEquals(dispatch.getId(), d.getId());
            Assert.assertEquals(dispatch.getSenderCenter().getId(), d.getSenderCenter().getId());
            Assert.assertEquals(dispatch.getReceiverCenter().getId(), d.getReceiverCenter().getId());
            for (DispatchSpecimen ds : d.getDispatchSpecimens()) {
                Assert.assertTrue(specimenIds.contains(ds.getSpecimen().getId()));
            }
        }
    }

    @Test
    public void requestSubmit() throws Exception {
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
            Request requestInDb = (Request) session.get(Request.class, reqId);

            for (RequestSpecimen rs : requestInDb.getRequestSpecimens()) {
                Assert.assertTrue(specimenIds.contains(rs.getSpecimen().getInventoryId()));
            }
        }
    }

    @Test
    public void requestStateChange() throws Exception {
        Request request = RequestHelper.createRequest(session, factory, researchGroup);
        exec(new RequestStateChangeAction(getRequestSpecimenIds(request),
                                          RequestSpecimenState.DISPATCHED_STATE));

        session.clear();
        request = (Request) session.get(Request.class, request.getId());
        for (RequestSpecimen rs : request.getRequestSpecimens()) {
            Assert.assertEquals(RequestSpecimenState.DISPATCHED_STATE, rs.getState());
        }
    }

    @Test
    public void requestGetSpecimenInfo() throws Exception {
        Request request = RequestHelper.createRequest(session, factory, researchGroup);
        List<Integer> specimenIds = getSpecimenIds(request);

        List<Object[]> specData = exec(new RequestGetSpecimenInfosAction(request.getId())).getList();
        for (Object[] specInfo : specData) {
            RequestSpecimen spec = (RequestSpecimen) specInfo[0];
            Assert.assertTrue(specimenIds.contains(spec.getSpecimen().getId()));
            Assert.assertNotNull(spec.getSpecimen().getCollectionEvent());
            Assert.assertNotNull(spec.getSpecimen().getSpecimenType());
            Assert.assertNotNull(spec.getSpecimen().getSpecimenPosition());
            Assert.assertNotNull(spec.getSpecimen().getSpecimenPosition().getContainer());
        }
    }

    @Test
    public void requestRetrieval() throws Exception {
        session.beginTransaction();
        Site site = factory.createSite();
        session.getTransaction().commit();

        Request request = RequestHelper.createRequest(session, factory, researchGroup);

        ArrayList<Request> response = exec(new RequestRetrievalAction(site)).getList();
        Assert.assertEquals(1, response.size());
        for (Request responseRequest : response) {
            Assert.assertEquals(request.getId(), responseRequest.getId());
        }
    }

    private List<Integer> getSpecimenIds(Request request) {
        List<Integer> ids = new ArrayList<Integer>(0);
        for (RequestSpecimen rs : request.getRequestSpecimens()) {
            ids.add(rs.getSpecimen().getId());
        }
        return ids;
    }


    private List<Integer> getRequestSpecimenIds(Request request) {
        List<Integer> ids = new ArrayList<Integer>(0);
        for (RequestSpecimen rs : request.getRequestSpecimens()) {
            ids.add(rs.getId());
        }
        return ids;
    }

}
