package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.info.AddressSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.common.action.request.RequestGetSpecimenInfosAction;
import edu.ualberta.med.biobank.common.action.researchGroup.RequestSubmitAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupDeleteAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.PatientHelper;
import edu.ualberta.med.biobank.test.action.helper.RequestHelper;
import edu.ualberta.med.biobank.test.action.helper.ResearchGroupHelper;

public class TestResearchGroup extends TestAction {

    private Study study;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session.beginTransaction();
        study = factory.createStudy();
        session.getTransaction().commit();
    }

    @Test
    public void saveResearchGroup() throws Exception { 
        String rgName = getMethodNameR();
        Integer rgId = ResearchGroupHelper.createResearchGroup(getExecutor(), rgName,
            rgName, study.getId());

        ResearchGroupGetInfoAction reader = new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = exec(reader);

        Assert.assertEquals(rgName, rg.getResearchGroup().getName());
        Assert.assertEquals(rgName, rg.getResearchGroup().getNameShort());
        Assert.assertEquals(study.getId(), rg.getResearchGroup().getStudies().iterator().next());
        Assert.assertEquals(ActivityStatus.ACTIVE, rg.getResearchGroup().getActivityStatus());
    }

    @Test
    public void testUpload() throws Exception {
        Integer rgId =
            ResearchGroupHelper.createResearchGroup(getExecutor(), getMethodNameR(),
                getMethodNameR(), study.getId());
        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = exec(reader);

        // create specs
        Integer p = PatientHelper.createPatient(getExecutor(), testName + "_patient", rg.getResearchGroup().getStudies().iterator().next().getId());
        Integer ceId = CollectionEventHelper.createCEventWithSourceSpecimens(getExecutor(), p, rg.getResearchGroup());

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
    public void testDelete() throws Exception {
        // only one failure case specific to rg, rest are in center

        Integer rgId = ResearchGroupHelper.createResearchGroup(getExecutor(), 
            getMethodNameR(), getMethodNameR(), study.getId());
        
        Integer rId = RequestHelper.createRequest(session, getExecutor(), 
            (ResearchGroup) session.load(ResearchGroup.class, rgId));
        
        ResearchGroupReadInfo rg =
            exec(new ResearchGroupGetInfoAction(rgId));
        ResearchGroupDeleteAction delete =
            new ResearchGroupDeleteAction(rg.getResearchGroup());
        try {
            exec(delete);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }
        session.close();
        session = openSession();
        session.beginTransaction();

        Request r = (Request) session.load(Request.class, rId);
        for (RequestSpecimen rs : r.getRequestSpecimens()) {
            Specimen spec = rs.getSpecimen();
            session.delete(rs);
            session.delete(spec);
        }
        r = (Request) session.load(Request.class, rId);
        session.delete(r);
        session.getTransaction().commit();

        exec(delete);
        // should be fine
    }

    @Test
    public void testComment() throws Exception {
        AddressSaveInfo addressSaveInfo =
            new AddressSaveInfo(null, "test", "test", "test", "test", "test",
                "test", "test", "test", "test");
        ResearchGroupSaveInfo save =
            new ResearchGroupSaveInfo(null, getMethodNameR(), getMethodNameR(), "comment", addressSaveInfo, ActivityStatus.ACTIVE);
        ResearchGroupSaveAction rgSave = new ResearchGroupSaveAction(save);

        Integer rgId = exec(rgSave).getId();
        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = exec(reader);

        save.id = rgId;

        Assert.assertEquals(1, rg.getResearchGroup().getComments().size());
        exec(rgSave);
        rg = exec(reader);
        Assert.assertEquals(2, rg.getResearchGroup().getComments().size());
        exec(rgSave);
        rg = exec(reader);
        Assert.assertEquals(3, rg.getResearchGroup().getComments().size());
    }
}
