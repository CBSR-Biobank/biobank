package edu.ualberta.med.biobank.action;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.action.info.AddressSaveInfo;
import edu.ualberta.med.biobank.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.action.request.RequestGetSpecimenInfosAction;
import edu.ualberta.med.biobank.action.researchGroup.RequestSubmitAction;
import edu.ualberta.med.biobank.action.researchGroup.ResearchGroupDeleteAction;
import edu.ualberta.med.biobank.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.action.researchGroup.ResearchGroupSaveAction;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.study.Request;
import edu.ualberta.med.biobank.model.study.RequestSpecimen;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.type.ActivityStatus;
import edu.ualberta.med.biobank.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.action.helper.PatientHelper;
import edu.ualberta.med.biobank.action.helper.RequestHelper;
import edu.ualberta.med.biobank.action.helper.ResearchGroupHelper;
import edu.ualberta.med.biobank.action.helper.StudyHelper;

public class TestResearchGroup extends ActionTest {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer studyId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + getR().nextInt();
        studyId =
            StudyHelper
                .createStudy(getExecutor(), name, ActivityStatus.ACTIVE);
    }

    @Test
    public void saveResearchGroup() throws Exception {

        Integer rgId =
            ResearchGroupHelper.createResearchGroup(getExecutor(), name,
                name,
                studyId);

        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = exec(reader);

        Assert.assertTrue(rg.researchGroup.getName().equals(name + "rg"));
        Assert.assertTrue(rg.researchGroup.getName().equals(name + "rg"));
        Assert.assertTrue(rg.researchGroup.getStudy().getId().equals(studyId));
        Assert
            .assertTrue(rg.researchGroup.getActivityStatus() == ActivityStatus.ACTIVE);

    }

    @Test
    public void testUpload() throws Exception {
        Integer rgId =
            ResearchGroupHelper.createResearchGroup(getExecutor(), name + "rg",
                name + "rg",
                studyId);
        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = exec(reader);

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
    public void testDelete() throws Exception {
        // only one failure case specific to rg, rest are in center

        Integer rgId =
            ResearchGroupHelper.createResearchGroup(getExecutor(), name,
                name,
                studyId);
        Integer rId = RequestHelper.createRequest(session, getExecutor(), rgId);
        ResearchGroupReadInfo rg =
            exec(new ResearchGroupGetInfoAction(rgId));
        ResearchGroupDeleteAction delete =
            new ResearchGroupDeleteAction(rg.researchGroup);
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
            new ResearchGroupSaveInfo(null, name + "rg", name + "rg",
                studyId, "comment", addressSaveInfo,
                ActivityStatus.ACTIVE);
        ResearchGroupSaveAction rgSave = new ResearchGroupSaveAction(save);

        Integer rgId = exec(rgSave).getId();
        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = exec(reader);

        save.id = rgId;

        Assert.assertEquals(1, rg.researchGroup.getComments().size());
        exec(rgSave);
        rg = exec(reader);
        Assert.assertEquals(2, rg.researchGroup.getComments().size());
        exec(rgSave);
        rg = exec(reader);
        Assert.assertEquals(3, rg.researchGroup.getComments().size());
    }
}
