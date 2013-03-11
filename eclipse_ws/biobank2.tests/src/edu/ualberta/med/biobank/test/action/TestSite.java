package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.hibernate.criterion.Restrictions;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.center.CenterGetStudyListAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.container.ContainerDeleteAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeDeleteAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.SiteContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventDeleteAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction.PEventInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.common.action.site.SiteDeleteAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetContainerTypeInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetStudyInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetTopContainersAction;
import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;

public class TestSite extends TestAction {

    @Test
    public void saveNew() throws Exception {
        NameGenerator nameGenerator = new NameGenerator("test_" + getMethodNameR());
        SiteSaveAction siteSaveAction = new SiteSaveAction();

        // null name
        siteSaveAction.setName(null);
        try {
            exec(siteSaveAction);
            Assert.fail("should not be allowed to add site with no name");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(TestAction.contains(e, NotEmpty.class,
                Site.class, "getName"));
            Assert.assertTrue(true);
        }

        // null short name
        siteSaveAction.setName(nameGenerator.next(String.class));
        siteSaveAction.setNameShort(null);
        try {
            exec(siteSaveAction);
            Assert.fail(
                "should not be allowed to add site with no short name");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        siteSaveAction.setNameShort(nameGenerator.next(String.class));
        siteSaveAction.setActivityStatus(null);
        try {
            exec(siteSaveAction);
            Assert.fail(
                "should not be allowed to add Site with no activity status");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        siteSaveAction.setActivityStatus(ActivityStatus.ACTIVE);
        siteSaveAction.setAddress(null);
        try {
            exec(siteSaveAction);
            Assert.fail(
                "should not be allowed to add site with no address");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        Set<Integer> studyIds = new HashSet<Integer>();
        studyIds.clear();
        studyIds.add(-1);
        siteSaveAction.setStudyIds(studyIds);
        try {
            exec(siteSaveAction);
            Assert.fail(
                "should not be allowed to add site with an invalid site id");
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(true);
        }

        // success path
        Address address = new Address();
        address.setCity(Utils.getRandomString(5, 10));
        siteSaveAction.setStudyIds(new HashSet<Integer>());
        siteSaveAction.setAddress(address);
        exec(siteSaveAction);
    }

    public void deleteSpecimens(Set<Specimen> parents) {
        Set<Specimen> children = new HashSet<Specimen>();
        for (Specimen parent : parents) {
            for (Specimen child : parent.getChildSpecimens()) {
                children.add(child);
            }
        }
        deleteSpecimens(children);
        for (Specimen parent : parents) {
            session.delete(parent);
        }
        session.flush();
    }

    @Test
    public void checkGetAction() throws Exception {
        session.beginTransaction();
        factory.createProcessingEvent();
        Specimen alqSpecimen = factory.createChildSpecimen();
        factory.getDefaultParentSpecimen().getChildSpecimens().add(alqSpecimen);
        session.getTransaction().commit();

        Site site = factory.getDefaultSite();

        SiteInfo siteInfo = exec(new SiteGetInfoAction(factory.getDefaultSite().getId()));

        Assert.assertEquals(site.getAddress().getCity(), siteInfo.getSite()
            .getAddress().getCity());
        Assert.assertEquals(ActivityStatus.ACTIVE,
            siteInfo.getSite().getActivityStatus());
        Assert.assertEquals(new Long(1), siteInfo.getPatientCount());
        Assert.assertEquals(new Long(1), siteInfo.getProcessingEventCount());
        Assert.assertEquals(new Long(2), siteInfo.getSpecimenCount());
    }

    @Test
    public void nameChecks() throws Exception {
        session.beginTransaction();
        Site site = factory.createSite();
        session.getTransaction().commit();

        // ensure we can change name on existing site
        String name = factory.getNameGenerator().next(String.class);
        SiteSaveAction siteSaveAction = SiteHelper.getSaveAction(
            name, site.getNameShort(), ActivityStatus.ACTIVE);
        siteSaveAction.setId(site.getId());
        exec(siteSaveAction);

        // ensure we can change short name on existing site
        siteSaveAction = SiteHelper.getSaveAction(
            site.getName(), name, ActivityStatus.ACTIVE);
        siteSaveAction.setId(site.getId());
        exec(siteSaveAction);

        // test for duplicate name
        SiteSaveAction saveSite = SiteHelper.getSaveAction(
            name, site.getNameShort(), ActivityStatus.ACTIVE);
        try {
            exec(saveSite);
            Assert.fail("should not be allowed to add site with same name");
        } catch (ConstraintViolationException e) {
            // do nothing
        }

        // test for duplicate name short
        saveSite.setName(factory.getNameGenerator().next(String.class));
        saveSite.setNameShort(name);

        try {
            exec(saveSite);
            Assert.fail("should not be allowed to add site with same name short");
        } catch (ConstraintViolationException e) {
            // do nothing
        }
    }

    @Test
    public void comments() {
        // start with no comments
        session.beginTransaction();
        Site site = factory.createSite();
        session.getTransaction().commit();

        addComment(site.getId());
        SiteInfo siteInfo = exec(new SiteGetInfoAction(site.getId()));
        Assert.assertEquals(1, siteInfo.getSite().getComments().size());

        for (Comment comment : siteInfo.getSite().getComments()) {
            Assert.assertEquals(getGlobalAdmin(), comment.getUser());
        }

        addComment(site.getId());
        siteInfo = exec(new SiteGetInfoAction(site.getId()));
        Assert.assertEquals(2, siteInfo.getSite().getComments().size());

        for (Comment comment : siteInfo.getSite().getComments()) {
            Assert.assertEquals(getGlobalAdmin(), comment.getUser());
        }
    }

    private void addComment(Integer siteId) {
        SiteSaveAction siteSaveAction = SiteHelper.getSaveAction(
            exec(new SiteGetInfoAction(siteId)));
        siteSaveAction.setCommentText(factory.getNameGenerator().next(String.class));
        exec(siteSaveAction).getId();
    }

    @Test
    public void saveStudies() throws Exception {
        session.beginTransaction();
        Site site = factory.createSite();
        Set<Study> studies = new HashSet<Study>();
        studies.add(factory.createStudy());
        studies.add(factory.createStudy());
        session.getTransaction().commit();

        Set<Integer> studyIds = new HashSet<Integer>();
        for (Study study : studies) {
            studyIds.add(study.getId());
        }

        SiteSaveAction siteSaveAction = new SiteSaveAction();
        siteSaveAction.setId(site.getId());
        siteSaveAction.setName(site.getName());
        siteSaveAction.setNameShort(site.getNameShort());
        siteSaveAction.setAddress(site.getAddress());
        siteSaveAction.setActivityStatus(site.getActivityStatus());
        siteSaveAction.setStudyIds(studyIds);
        exec(siteSaveAction);

        session.clear();
        Site siteAfterSave = (Site) session.load(Site.class, site.getId());
        Assert.assertEquals(studies.size(), siteAfterSave.getStudies().size());
        Assert.assertTrue(siteAfterSave.getStudies().containsAll(studies));
    }

    @Test
    public void getStudies() {
        session.beginTransaction();
        Site site = factory.createSite();
        Set<Study> studies = new HashSet<Study>();
        studies.add(factory.createStudy());
        studies.add(factory.createStudy());
        site.getStudies().addAll(studies);
        for (Study study : studies) {
            study.getSites().add(site);
        }
        session.getTransaction().commit();

        SiteInfo siteInfo = exec(new SiteGetInfoAction(site));
        Assert.assertEquals(studies.size(), siteInfo.getSite().getStudies().size());
        Assert.assertTrue(siteInfo.getSite().getStudies().containsAll(studies));
    }

    @Test
    public void deleteAllStudies() {
        session.beginTransaction();
        Site site = factory.createSite();
        Set<Study> studies = new HashSet<Study>();
        studies.add(factory.createStudy());
        studies.add(factory.createStudy());
        site.getStudies().addAll(studies);
        for (Study study : studies) {
            study.getSites().add(site);
        }
        session.getTransaction().commit();

        // create a new save action but so not assign the study ids
        SiteSaveAction siteSaveAction = new SiteSaveAction();
        siteSaveAction.setId(site.getId());
        siteSaveAction.setName(site.getName());
        siteSaveAction.setNameShort(site.getNameShort());
        siteSaveAction.setAddress(site.getAddress());
        siteSaveAction.setActivityStatus(site.getActivityStatus());
        exec(siteSaveAction);

        SiteInfo siteInfo = exec(new SiteGetInfoAction(site));
        Assert.assertEquals(0, siteInfo.getSite().getStudies().size());
    }

    @Test
    public void deleteOneStudy() {
        session.beginTransaction();
        Site site = factory.createSite();
        List<Study> studies = new ArrayList<Study>();
        studies.add(factory.createStudy());
        studies.add(factory.createStudy());
        site.getStudies().addAll(studies);
        for (Study study : studies) {
            study.getSites().add(site);
        }
        session.getTransaction().commit();

        Set<Integer> studyIdsToRemain = new HashSet<Integer>();
        studyIdsToRemain.add(studies.get(0).getId());

        // create a new save action but so not assign the study ids
        SiteSaveAction siteSaveAction = new SiteSaveAction();
        siteSaveAction.setId(site.getId());
        siteSaveAction.setName(site.getName());
        siteSaveAction.setNameShort(site.getNameShort());
        siteSaveAction.setAddress(site.getAddress());
        siteSaveAction.setActivityStatus(site.getActivityStatus());
        siteSaveAction.setStudyIds(studyIdsToRemain);
        exec(siteSaveAction);

        SiteInfo siteInfo = exec(new SiteGetInfoAction(site));
        Assert.assertEquals(studies.size() - 1, siteInfo.getSite().getStudies().size());
        Assert.assertTrue(siteInfo.getSite().getStudies().contains(studies.get(0)));
    }

    @Test
    public void containerTypes() {
        SiteSaveAction siteSaveAction =
            SiteHelper.getSaveAction(getMethodNameR(), getMethodNameR(), ActivityStatus.ACTIVE);
        Integer siteId = exec(siteSaveAction).getId();

        String ctName = getMethodName() + "FREEZER01";

        ContainerTypeSaveAction ctSaveAction = ContainerTypeHelper.getSaveAction(
            ctName, ctName, siteId, true, 6, 10, getLabelingSchemeWithLargerCapacity(1).getId(),
            getR().nextDouble());
        Integer ctId = exec(ctSaveAction).getId();

        SiteInfo siteInfo = exec(new SiteGetInfoAction(siteId));
        Assert.assertEquals(1, siteInfo.getContainerTypeCount().longValue());
        Assert.assertEquals(ctId, siteInfo.getContainerTypeInfos().get(0)
            .getContainerType().getId());
        Assert.assertEquals(ctName, siteInfo.getContainerTypeInfos().get(0)
            .getContainerType().getName());

        // add another container
        ctName += "_2";
        ctSaveAction = ContainerTypeHelper.getSaveAction(ctName, ctName, siteId, true, 3, 8,
            getLabelingSchemeWithLargerCapacity(1).getId(), getR().nextDouble());
        ctId = exec(ctSaveAction).getId();

        siteInfo = exec(new SiteGetInfoAction(siteId));
        Assert.assertEquals(2, siteInfo.getContainerTypeCount().longValue());
        Assert.assertEquals(ctId, siteInfo.getContainerTypeInfos().get(1)
            .getContainerType().getId());
        Assert.assertEquals(ctName, siteInfo.getContainerTypeInfos().get(1)
            .getContainerType().getName());
    }

    @Test
    public void delete() {
        session.beginTransaction();
        Site site = factory.createSite();
        session.getTransaction().commit();

        exec(new SiteDeleteAction(site));

        @SuppressWarnings("unchecked")
        List<Site> sites = session.createCriteria(Site.class)
            .add(Restrictions.eq("id", site.getId())).list();
        Assert.assertEquals(0, sites.size());
    }

    private Provisioning createSiteWithContainerType() {
        session.beginTransaction();
        Provisioning provisioning = new Provisioning(session, factory);
        session.getTransaction().commit();

        provisioning.addContainerType(getExecutor(), getMethodName(),
            getLabelingSchemeWithLargerCapacity(1).getId(), getR().nextDouble());
        return provisioning;
    }

    @Test
    public void checkSiteGetCtypeInfoAction() throws Exception {
        Provisioning provisioning = createSiteWithContainerType();

        List<SiteContainerTypeInfo> ctypeInfo = exec(
            new SiteGetContainerTypeInfoAction(provisioning.siteId)).getList();

        Assert.assertEquals(1, ctypeInfo.size());
        Assert.assertEquals(0L, ctypeInfo.get(0).getContainerCount().longValue());

        Integer containerTypeId = provisioning.containerTypeIds.get(0);
        provisioning.addContainer(getExecutor(), containerTypeId, "01");

        ctypeInfo = exec(new SiteGetContainerTypeInfoAction(provisioning.siteId)).getList();

        Assert.assertEquals(1L, ctypeInfo.get(0).getContainerCount().longValue());
    }

    @Test
    public void deleteWithContainerTypes() {
        Provisioning provisioning = createSiteWithContainerType();
        SiteInfo siteInfo = exec(new SiteGetInfoAction(provisioning.siteId));
        try {
            exec(new SiteDeleteAction(siteInfo.getSite()));
            Assert.fail(
                "should not be allowed to delete a site with container types");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        // delete container type followed by site - should work now
        ContainerTypeInfo containerTypeInfo = exec(new ContainerTypeGetInfoAction(
            provisioning.containerTypeIds.get(0)));
        exec(new ContainerTypeDeleteAction(containerTypeInfo.getContainerType()));
        exec(new SiteDeleteAction(siteInfo.getSite()));
    }

    @Test
    public void deleteWithContainers() {
        Provisioning provisioning = createSiteWithContainerType();
        Integer containerTypeId = provisioning.containerTypeIds.get(0);
        Integer containerId = provisioning.addContainer(getExecutor(), containerTypeId, "01");

        SiteInfo siteInfo = exec(new SiteGetInfoAction(provisioning.siteId));
        try {
            exec(new SiteDeleteAction(siteInfo.getSite()));
            Assert.fail("should not be allowed to delete a site with containers");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        List<Container> topContainers = exec(new SiteGetTopContainersAction(provisioning.siteId))
            .getList();
        Assert.assertEquals(1, topContainers.size());

        // delete container followed by site - should work now
        Container qryContainer = new Container();
        qryContainer.setId(containerId);
        List<Container> containers = exec(new ContainerGetInfoAction(qryContainer)).getList();
        exec(new ContainerDeleteAction(containers.get(0)));
        ContainerTypeInfo containerTypeInfo = exec(new ContainerTypeGetInfoAction(containerTypeId));
        exec(new ContainerTypeDeleteAction(containerTypeInfo.getContainerType()));
        exec(new SiteDeleteAction(siteInfo.getSite()));
    }

    @Test
    public void getTopContainers() throws Exception {
        session.beginTransaction();
        Site site = factory.createSite();
        Set<Container> containers = new HashSet<Container>();
        containers.add(factory.createTopContainer());
        factory.createContainer();
        containers.add(factory.createTopContainer());
        factory.createContainer();
        session.getTransaction().commit();

        List<Container> actionResult = exec(new SiteGetTopContainersAction(site.getId())).getList();
        Assert.assertEquals(containers.size(), actionResult.size());
        Assert.assertTrue(actionResult.containsAll(containers));
    }

    @Test
    public void deleteWithProcessingEvents() throws Exception {
        session.beginTransaction();
        Provisioning provisioning = new Provisioning(session, factory);
        session.getTransaction().commit();

        // create a collection event
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(getExecutor(),
                provisioning.patientIds.get(0), provisioning.getClinic());
        CEventInfo ceventInfo =
            exec(new CollectionEventGetInfoAction(ceventId));
        List<SpecimenInfo> sourceSpecs = ceventInfo.sourceSpecimenInfos;

        // create a processing event with one of the collection event source
        // specimens
        Site site = (Site) session.load(Site.class, provisioning.siteId);
        Integer peventId = exec(new ProcessingEventSaveAction(
            null, site, Utils.getRandomDate(), Utils.getRandomString(5,
                8), ActivityStatus.ACTIVE, null,
            new HashSet<Integer>(
                Arrays.asList(sourceSpecs.get(0).specimen.getId())),
            new HashSet<Integer>()))
            .getId();

        SiteInfo siteInfo =
            exec(new SiteGetInfoAction(provisioning.siteId));
        try {
            exec(new SiteDeleteAction(siteInfo.getSite()));
            Assert
                .fail(
                "should not be allowed to delete a site with processing events");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        // delete the processing event
        ProcessingEvent pevent = (ProcessingEvent)
            session.get(ProcessingEvent.class, peventId);
        PEventInfo peventInfo =
            exec(new ProcessingEventGetInfoAction(pevent));
        exec(new ProcessingEventDeleteAction(peventInfo.pevent));
        exec(new SiteDeleteAction(siteInfo.getSite()));
    }

    @Test
    public void deleteWithSrcDispatch() throws Exception {
        session.beginTransaction();
        Site site1 = factory.createSite();
        Site site2 = factory.createSite();
        factory.createDispatch(site1, site2);
        session.getTransaction().commit();

        try {
            exec(new SiteDeleteAction(site1));
            Assert.fail(
                "should not be allowed to delete a site which is a source of dispatches");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        // now delete the dispatch and then the originating site
        session.beginTransaction();
        session.delete(factory.getDefaultDispatch());
        session.getTransaction().commit();

        exec(new SiteDeleteAction(site1));
    }

    @Test
    public void deleteWithDstDispatch() throws Exception {
        session.beginTransaction();
        Site site1 = factory.createSite();
        Site site2 = factory.createSite();
        factory.createDispatch(site1, site2);
        session.getTransaction().commit();

        try {
            exec(new SiteDeleteAction(site2));
            Assert.fail(
                "should not be allowed to delete a site which is a destination for dispatches");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        // now delete the dispatch and then the originating site
        session.beginTransaction();
        session.delete(factory.getDefaultDispatch());
        session.getTransaction().commit();

        exec(new SiteDeleteAction(site2));
    }

    @Test
    public void getStudyInfo() throws Exception {
        session.beginTransaction();
        factory.createSite();
        factory.getDefaultSite().getStudies().add(factory.createStudy());
        Set<Patient> patients = new HashSet<Patient>();
        Set<CollectionEvent> cevents = new HashSet<CollectionEvent>();

        for (int i = 0; i < 5; i++) {
            patients.add(factory.createPatient());
            for (int j = 0; j < 5; j++) {
                cevents.add(factory.createCollectionEvent());
            }
        }
        session.getTransaction().commit();

        List<StudyCountInfo> studyCountInfos =
            exec(new SiteGetStudyInfoAction(factory.getDefaultSite().getId()))
                .getList();
        Assert.assertEquals(1, studyCountInfos.size());

        StudyCountInfo studyCountInfo = studyCountInfos.get(0);

        Assert.assertEquals(factory.getDefaultStudy(),
            studyCountInfo.getStudy());
        Assert.assertEquals(cevents.size(), studyCountInfo
            .getCollectionEventCount().intValue());
        Assert.assertEquals(patients.size(), studyCountInfo.getPatientCount()
            .intValue());
    }

    @Test
    public void getStudyList() {
        session.beginTransaction();
        Site site = factory.createSite();
        Set<Study> studies = new HashSet<Study>();
        Set<Study> studies2 = new HashSet<Study>();

        studies.add(factory.createStudy());
        studies.add(factory.createStudy());
        studies2.add(factory.getDefaultStudy());
        studies.add(factory.createStudy());

        site.getStudies().addAll(studies);
        session.getTransaction().commit();

        List<Study> actionStudies = exec(new CenterGetStudyListAction(site)).getList();

        Assert.assertEquals(studies.size(), actionStudies.size());
        Assert.assertTrue(actionStudies.containsAll(studies));

        session.beginTransaction();
        Site site2 = factory.createSite();
        site2.getStudies().addAll(studies2);
        session.getTransaction().commit();

        actionStudies = exec(new CenterGetStudyListAction(site2)).getList();
        Assert.assertEquals(studies2.size(), actionStudies.size());
        Assert.assertTrue(actionStudies.containsAll(studies2));

        session.beginTransaction();
        site.getStudies().remove(factory.getDefaultStudy());
        session.getTransaction().commit();

        actionStudies = exec(new CenterGetStudyListAction(site)).getList();
        Assert.assertEquals(studies.size() - 1, actionStudies.size());
    }

    @Test
    public void getStudyListSiteNull() {
        // do not persist this site -> do not commit this transaction
        session.beginTransaction();
        Site site = factory.createSite();

        try {
            exec(new CenterGetStudyListAction(site)).getList();
            Assert.fail("cannot call action for site not yet saved to DB");
        } catch (ActionException e) {
            // do nothing
        }
    }
}
