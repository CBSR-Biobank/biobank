package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupDeleteAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetAllAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetStudyInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;

public class TestResearchGroup extends TestAction {

    @Test
    public void researchGroupGetInfo() throws Exception {
        session.beginTransaction();
        ResearchGroup researchGroup = factory.createResearchGroup();
        session.getTransaction().commit();

        ResearchGroupReadInfo rg = exec(new ResearchGroupGetInfoAction(researchGroup.getId()));

        Assert.assertEquals(researchGroup.getName(), rg.getResearchGroup().getName());
        Assert.assertEquals(researchGroup.getNameShort(), rg.getResearchGroup().getNameShort());
        Assert.assertEquals(ActivityStatus.ACTIVE, rg.getResearchGroup().getActivityStatus());

        List<Integer> studyIds = new ArrayList<>();
        for (Study study : researchGroup.getStudies()) {
            studyIds.add(study.getId());
        }

        for (Study study : rg.getResearchGroup().getStudies()) {
            Assert.assertTrue(studyIds.contains(study.getId()));
        }
    }

    @Test
    public void researchGroupGetAll() throws Exception {
        session.beginTransaction();
        Set<ResearchGroup> researchGroupsInDb =
            new HashSet<ResearchGroup>(Arrays.asList(factory.createResearchGroup(),
                                                     factory.createResearchGroup(),
                                                     factory.createResearchGroup()));
        session.getTransaction().commit();

        ArrayList<ResearchGroup> response = exec(new ResearchGroupGetAllAction()).getList();
        List<Integer> researchGroupIds = new ArrayList<>(0);
        for (ResearchGroup rg: response) {
            researchGroupIds.add(rg.getId());
        }

        for (ResearchGroup rgInDb : researchGroupsInDb) {
            Assert.assertTrue(researchGroupIds.contains(rgInDb.getId()));
        }
    }

    @Test
    public void researchGroupGetStudyInfo() throws Exception {
        session.beginTransaction();
        Set<Study> studies = new HashSet<Study>(Arrays.asList(factory.createStudy(),
                                                              factory.createStudy(),
                                                              factory.createStudy()));
        List<Integer> studyIds = new ArrayList<>(0);
        for (Study study : studies) {
            studyIds.add(study.getId());
        }
        ResearchGroup researchGroup = factory.createResearchGroup();
        researchGroup.setStudies(studies);
        session.getTransaction().commit();

        ArrayList<StudyCountInfo> response =
            exec(new ResearchGroupGetStudyInfoAction(researchGroup.getId())).getList();

        Assert.assertEquals(studies.size(), response.size());
        for (StudyCountInfo info : response) {
            Assert.assertTrue(studyIds.contains(info.getStudy().getId()));
        }
    }

    @Test
    public void researchGroupSave() throws Exception {
        session.beginTransaction();
        Study study = factory.createStudy();
        session.getTransaction().commit();

        ResearchGroup rg = new ResearchGroup();
        rg.getAddress().setCity("testville");
        rg.setName(factory.getNameGenerator().next(Center.class));
        rg.setNameShort(factory.getNameGenerator().next(Center.class));
        rg.setStudies(new HashSet<>(Arrays.asList(study)));

        ResearchGroupSaveInfo saveInfo = ResearchGroupSaveInfo.createFromResearchGroup(rg);
        Integer rgId = exec(new ResearchGroupSaveAction(saveInfo)).getId();

        ResearchGroup rgInDb = (ResearchGroup) session.createCriteria(ResearchGroup.class)
            .add(Restrictions.eq("id", rgId))
            .uniqueResult();

        Assert.assertEquals(rg.getNameShort(), rgInDb.getNameShort());
        Assert.assertTrue(rgInDb.getRequests().isEmpty());
        Assert.assertTrue(rgInDb.getStudies().size() == 1);
        Assert.assertEquals(rg.getActivityStatus(), rgInDb.getActivityStatus());
        Assert.assertEquals(rg.getAddress().getCity(), rgInDb.getAddress().getCity());

        for (Study rgStudy : rgInDb.getStudies()) {
            Assert.assertEquals(study.getId(), rgStudy.getId());
        }
    }

    @Test
    public void researchGroupDelete() throws Exception {
        session.beginTransaction();
        ResearchGroup researchGroup = factory.createResearchGroup();
        Request req = factory.createRequest();
        session.getTransaction().commit();

        try {
            exec(new ResearchGroupDeleteAction(researchGroup));
            Assert.fail();
        } catch (ConstraintViolationException e) {
            Assert.assertTrue("delete action should fail", true);
        }

        session.beginTransaction();
        for (RequestSpecimen rs : req.getRequestSpecimens()) {
            Specimen spec = rs.getSpecimen();
            session.delete(rs);
            session.delete(spec);
        }
        session.delete(req);
        session.getTransaction().commit();

        exec(new ResearchGroupDeleteAction(researchGroup));
        Assert.assertTrue("delete action should not fail this time", true);
    }

    @Test
    public void researchGroupComment() throws Exception {
        session.beginTransaction();
        ResearchGroup researchGroup = factory.createResearchGroup();
        Set<Comment> comments = new HashSet<Comment>(Arrays.asList(factory.createComment(),
                                                                  factory.createComment(),
                                                                  factory.createComment()));
        researchGroup.setComments(comments);
        session.getTransaction().commit();

        ResearchGroupReadInfo rg = exec(new ResearchGroupGetInfoAction(researchGroup.getId()));

        Assert.assertEquals(comments.size(), rg.getResearchGroup().getComments().size());
    }
}
