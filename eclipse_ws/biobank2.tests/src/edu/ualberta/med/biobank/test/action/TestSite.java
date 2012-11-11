package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.SetResult;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.container.ContainerDeleteAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeDeleteAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchDeleteAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetInfoAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetSpecimenInfosAction;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.DispatchReadInfo;
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
import edu.ualberta.med.biobank.common.action.specimen.SpecimenDeleteAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.action.helper.DispatchHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestSite extends TestAction {

    private String name;

    private SiteSaveAction siteSaveAction;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();

        siteSaveAction =
            SiteHelper.getSaveAction(name, name, ActivityStatus.ACTIVE);
    }

    @Test
    public void saveNew() throws Exception {
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
        siteSaveAction.setName(name);
        siteSaveAction.setNameShort(null);
        try {
            exec(siteSaveAction);
            Assert.fail(
                "should not be allowed to add site with no short name");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        siteSaveAction.setNameShort(name);
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

        SiteInfo siteInfo =
            exec(new SiteGetInfoAction(factory.getDefaultSite().getId()));

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
        Integer siteId = exec(siteSaveAction).getId();

        // ensure we can change name on existing clinic
        SiteInfo siteInfo = exec(new SiteGetInfoAction(siteId));
        siteInfo.getSite().setName(name + "_2");
        siteSaveAction = SiteHelper.getSaveAction(siteInfo);
        exec(siteSaveAction);

        // ensure we can change short name on existing site
        siteInfo = exec(new SiteGetInfoAction(siteId));
        siteInfo.getSite().setNameShort(name + "_2");
        siteSaveAction = SiteHelper.getSaveAction(siteInfo);
        exec(siteSaveAction);

        // test for duplicate name
        SiteSaveAction saveSite = SiteHelper.getSaveAction(name + "_2", name,
            ActivityStatus.ACTIVE);
        try {
            exec(saveSite);
            Assert.fail("should not be allowed to add site with same name");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        // test for duplicate name short
        saveSite.setName(Utils.getRandomString(5, 10));
        saveSite.setNameShort(name + "_2");

        try {
            exec(saveSite);
            Assert.fail(
                "should not be allowed to add site with same name short");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void comments() {
        // save with no comments
        Integer siteId = exec(siteSaveAction).getId();
        SiteInfo siteInfo = exec(new SiteGetInfoAction(siteId));
        Assert.assertEquals(0, siteInfo.getSite().getComments().size());

        siteInfo = addComment(siteId);
        Assert.assertEquals(1, siteInfo.getSite().getComments().size());

        siteInfo = addComment(siteId);
        Assert.assertEquals(2, siteInfo.getSite().getComments().size());

        // TODO: check full name on each comment's user
        // for (Comment comment : siteInfo.site.getCommentCollection()) {
        //
        // }

    }

    private SiteInfo addComment(Integer siteId) {
        SiteSaveAction siteSaveAction = SiteHelper.getSaveAction(
            exec(new SiteGetInfoAction(siteId)));
        siteSaveAction.setCommentText(Utils.getRandomString(20, 30));
        exec(siteSaveAction).getId();
        return exec(new SiteGetInfoAction(siteId));
    }

    @Test
    public void studyCollection() throws Exception {
        Set<Integer> studyIds;
        List<Integer> allStudyIds = new ArrayList<Integer>();
        Set<Integer> studyIdsSet1 = new HashSet<Integer>();
        Set<Integer> studyIdsSet2 = new HashSet<Integer>();

        for (int i = 0; i < 20; ++i) {
            Integer id = StudyHelper.createStudy(
                getExecutor(), name + "_study" + i, ActivityStatus.ACTIVE);
            allStudyIds.add(id);
            if (i < 10) {
                studyIdsSet1.add(id);
            } else {
                studyIdsSet2.add(id);
            }
        }

        // add study set 1 one by one
        Integer siteId = exec(siteSaveAction).getId();
        SiteInfo siteInfo =
            exec(new SiteGetInfoAction(siteId));
        Set<Integer> expectedStudyIds = new HashSet<Integer>();

        for (Integer studyId : studyIdsSet1) {
            expectedStudyIds.add(studyId);

            SiteSaveAction siteSaveAction = SiteHelper.getSaveAction(siteInfo);
            studyIds = getStudyIds(siteInfo.getStudyCountInfos());
            studyIds.add(studyId);
            siteSaveAction.setStudyIds(studyIds);
            exec(siteSaveAction);
            siteInfo = exec(new SiteGetInfoAction(siteId));
            Assert.assertEquals(expectedStudyIds,
                getStudyIds(siteInfo.getStudyCountInfos()));
        }

        // create a second site, site 2, with the second set of studies
        Integer siteId2 = SiteHelper.createSite(getExecutor(), name + "_2",
            Utils.getRandomString(8, 12),
            ActivityStatus.ACTIVE, studyIdsSet2);
        siteInfo = exec(new SiteGetInfoAction(siteId2));
        expectedStudyIds.clear();
        expectedStudyIds.addAll(studyIdsSet2);
        Assert.assertEquals(expectedStudyIds,
            getStudyIds(siteInfo.getStudyCountInfos()));

        // make sure site 1 still has same collection
        siteInfo = exec(new SiteGetInfoAction(siteId));
        expectedStudyIds.clear();
        expectedStudyIds.addAll(studyIdsSet1);
        Assert.assertEquals(expectedStudyIds,
            getStudyIds(siteInfo.getStudyCountInfos()));

        // delete studies one by one from Site 1
        siteInfo = exec(new SiteGetInfoAction(siteId));
        for (Integer studyId : studyIdsSet1) {
            expectedStudyIds.remove(studyId);

            siteSaveAction = SiteHelper.getSaveAction(siteInfo);
            studyIds = getStudyIds(siteInfo.getStudyCountInfos());
            studyIds.remove(studyId);
            siteSaveAction.setStudyIds(studyIds);
            exec(siteSaveAction);
            siteInfo = exec(new SiteGetInfoAction(siteId));
            Assert.assertEquals(expectedStudyIds,
                getStudyIds(siteInfo.getStudyCountInfos()));
        }

        // delete studies from Site 2
        siteInfo = exec(new SiteGetInfoAction(siteId2));
        studyIds = getStudyIds(siteInfo.getStudyCountInfos());
        studyIds.removeAll(studyIdsSet2);
        siteSaveAction = SiteHelper.getSaveAction(siteInfo);
        exec(siteSaveAction);
        siteInfo = exec(new SiteGetInfoAction(siteId));
        Assert.assertTrue(getStudyIds(siteInfo.getStudyCountInfos()).isEmpty());

        // attempt to add an invalid study ID
        siteInfo = exec(new SiteGetInfoAction(siteId));
        SiteSaveAction siteSaveAction = SiteHelper.getSaveAction(siteInfo);
        studyIds = getStudyIds(siteInfo.getStudyCountInfos());
        studyIds.add(-1);
        siteSaveAction.setStudyIds(studyIds);
        try {
            exec(siteSaveAction);
            Assert.fail("should not be allowed to add an invalid study id");
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void containerTypes() {
        Integer siteId = exec(siteSaveAction).getId();

        List<ContainerLabelingScheme> labelingSchemes =
            new ArrayList<ContainerLabelingScheme>(
                getContainerLabelingSchemes().values());

        String ctName = name + "FREEZER01";

        ContainerTypeSaveAction ctSaveAction =
            ContainerTypeHelper.getSaveAction(ctName, ctName, siteId, true, 6,
                10, labelingSchemes.get(0).getId(), getR().nextDouble());
        Integer ctId = exec(ctSaveAction).getId();

        SiteInfo siteInfo = exec(new SiteGetInfoAction(siteId));
        Assert.assertEquals(1, siteInfo.getContainerTypeCount().longValue());
        Assert.assertEquals(ctId, siteInfo.getContainerTypeInfos().get(0)
            .getContainerType().getId());
        Assert.assertEquals(ctName, siteInfo.getContainerTypeInfos().get(0)
            .getContainerType().getName());

        // add another container
        ctName += "_2";
        ctSaveAction =
            ContainerTypeHelper.getSaveAction(ctName, ctName, siteId, true, 3,
                8, labelingSchemes.get(1).getId(), getR().nextDouble());
        ctId = exec(ctSaveAction).getId();

        siteInfo = exec(new SiteGetInfoAction(siteId));
        Assert.assertEquals(2, siteInfo.getContainerTypeCount().longValue());
        Assert.assertEquals(ctId, siteInfo.getContainerTypeInfos().get(1)
            .getContainerType().getId());
        Assert.assertEquals(ctName, siteInfo.getContainerTypeInfos().get(1)
            .getContainerType().getName());
    }

    private Set<Integer> getStudyIds(List<StudyCountInfo> studyCountInfo) {
        Set<Integer> ids = new HashSet<Integer>();
        for (StudyCountInfo info : studyCountInfo) {
            ids.add(info.getStudy().getId());
        }
        return ids;
    }

    @Test
    public void delete() {
        Integer siteId = exec(siteSaveAction).getId();
        SiteInfo siteInfo = exec(new SiteGetInfoAction(siteId));
        exec(new SiteDeleteAction(siteInfo.getSite()));

        // hql query for site should return empty
        Query q =
            session.createQuery("SELECT COUNT(*) FROM "
                + Site.class.getName() + " WHERE id=?");
        q.setParameter(0, siteId);
        Long result = HibernateUtil.getCountFromQuery(q);
        Assert.assertTrue(result.equals(0L));
    }

    private Provisioning createSiteWithContainerType() {
        session.beginTransaction();
        Provisioning provisioning = new Provisioning(session, factory);
        session.getTransaction().commit();

        provisioning.addContainerType(getExecutor(), name,
            getContainerLabelingSchemes().values().iterator().next()
                .getId(), getR().nextDouble());
        return provisioning;
    }

    @Test
    public void checkSiteGetCtypeInfoAction() throws Exception {
        Provisioning provisioning = createSiteWithContainerType();

        List<SiteContainerTypeInfo> ctypeInfo = exec(
            new SiteGetContainerTypeInfoAction(provisioning.siteId))
            .getList();

        Assert.assertEquals(1, ctypeInfo.size());
        Assert.assertEquals(0L, ctypeInfo.get(0).getContainerCount()
            .longValue());

        Integer containerTypeId = provisioning.containerTypeIds.get(0);
        provisioning.addContainer(getExecutor(), containerTypeId, "01");

        ctypeInfo = exec(
            new SiteGetContainerTypeInfoAction(provisioning.siteId))
            .getList();

        Assert.assertEquals(1L, ctypeInfo.get(0).getContainerCount()
            .longValue());
    }

    @Test
    public void deleteWithContainerTypes() {
        Provisioning provisioning = createSiteWithContainerType();
        SiteInfo siteInfo =
            exec(new SiteGetInfoAction(provisioning.siteId));
        try {
            exec(new SiteDeleteAction(siteInfo.getSite()));
            Assert
                .fail(
                "should not be allowed to delete a site with container types");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        // delete container type followed by site - should work now
        ContainerTypeInfo containerTypeInfo =
            exec(new ContainerTypeGetInfoAction(
                provisioning.containerTypeIds.get(0)));
        exec(new ContainerTypeDeleteAction(containerTypeInfo
            .getContainerType()));
        exec(new SiteDeleteAction(siteInfo.getSite()));
    }

    @Test
    public void deleteWithContainers() {
        Provisioning provisioning = createSiteWithContainerType();
        Integer containerTypeId = provisioning.containerTypeIds.get(0);
        Integer containerId =
            provisioning.addContainer(getExecutor(), containerTypeId, "01");

        SiteInfo siteInfo =
            exec(new SiteGetInfoAction(provisioning.siteId));
        try {
            exec(new SiteDeleteAction(siteInfo.getSite()));
            Assert
                .fail(
                "should not be allowed to delete a site with containers");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        List<Container> topContainers =
            exec(new SiteGetTopContainersAction(provisioning.siteId))
                .getList();
        Assert.assertEquals(1, topContainers.size());

        // delete container followed by site - should work now
        ContainerInfo containerInfo =
            exec(new ContainerGetInfoAction(containerId));
        exec(new ContainerDeleteAction(containerInfo.container));
        ContainerTypeInfo containerTypeInfo =
            exec(new ContainerTypeGetInfoAction(containerTypeId));
        exec(new ContainerTypeDeleteAction(containerTypeInfo
            .getContainerType()));
        exec(new SiteDeleteAction(siteInfo.getSite()));
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
        Provisioning provisioning = new Provisioning(session, factory);
        // create a second site to dispatch to
        Site site2 = factory.createSite();
        session.getTransaction().commit();

        Integer dispatchId1 = DispatchHelper.createDispatch(getExecutor(), 
            provisioning.getClinic(), provisioning.getSite(), 
            provisioning.patientIds.get(0));

        Integer dispatchId2 = DispatchHelper.createDispatch(getExecutor(), 
            provisioning.getSite(), site2, provisioning.patientIds.get(0));

        SiteInfo siteInfo =
            exec(new SiteGetInfoAction(provisioning.siteId));
        try {
            exec(new SiteDeleteAction(siteInfo.getSite()));
            Assert
                .fail(
                "should not be allowed to delete a site which is a source of dispatches");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        // delete the dispatch and then the site
        Set<Specimen> specimens = new HashSet<Specimen>();
        SetResult<DispatchSpecimen> dispatchSpecimens =
            getExecutor()
                .exec(new DispatchGetSpecimenInfosAction(dispatchId1));
        for (DispatchSpecimen dspec : dispatchSpecimens.getSet()) {
            specimens.add(dspec.getSpecimen());
        }

        dispatchSpecimens =
            getExecutor()
                .exec(new DispatchGetSpecimenInfosAction(dispatchId2));
        for (DispatchSpecimen dspec : dispatchSpecimens.getSet()) {
            specimens.add(dspec.getSpecimen());
        }

        DispatchReadInfo dispatchInfo =
            exec(new DispatchGetInfoAction(dispatchId2));
        exec(new DispatchDeleteAction(dispatchInfo.dispatch));
        dispatchInfo =
            exec(new DispatchGetInfoAction(dispatchId1));
        exec(new DispatchDeleteAction(dispatchInfo.dispatch));

        for (Specimen specimen : specimens) {
            exec(new SpecimenDeleteAction(specimen));
        }

        deleteOriginInfos(provisioning.siteId);
        exec(new SiteDeleteAction(siteInfo.getSite()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteWithDstDispatch() throws Exception {
        session.beginTransaction();
        Provisioning provisioning = new Provisioning(session, factory);
        session.getTransaction().commit();

        Integer dispatchId = DispatchHelper.createDispatch(getExecutor(), 
            provisioning.getClinic(), provisioning.getSite(),
            provisioning.patientIds.get(0));

        SiteInfo siteInfo =
            exec(new SiteGetInfoAction(provisioning.siteId));
        try {
            exec(new SiteDeleteAction(siteInfo.getSite()));
            Assert
                .fail(
                "should not be allowed to delete a site which is a destination for dispatches");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        // delete the dispatch and then the site - no need to delete dispatch
        // specimens
        DispatchReadInfo dispatchInfo =
            exec(new DispatchGetInfoAction(dispatchId));
        exec(new DispatchDeleteAction(dispatchInfo.dispatch));

        // TODO: delete specimens
        Transaction tx = session.beginTransaction();
        Query q = session.createQuery("from " + Specimen.class.getName()
            + " where currentCenter.id = ?")
            .setParameter(0, provisioning.siteId);
        for (Specimen s : (List<Specimen>) q.list()) {
            session.delete(s);
        }

        tx.commit();

        exec(new SiteDeleteAction(siteInfo.getSite()));
    }

    @Test
    public void getStudyInfo() throws Exception {
        Transaction tx = session.beginTransaction();
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
        tx.commit();

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
}
