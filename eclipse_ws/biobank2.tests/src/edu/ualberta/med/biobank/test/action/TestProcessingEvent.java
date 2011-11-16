package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetSpecimenInfosAction;
import edu.ualberta.med.biobank.common.action.info.CommentInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventDeleteAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ModelIsUsedException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.PatientHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestProcessingEvent extends TestAction {

    private Integer siteId;
    private StudyWrapper study;
    private ClinicWrapper clinic;
    private Integer patientId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String name = "Processing Event Test" + r.nextInt();
        study = StudyHelper.addStudy(name);
        clinic = ClinicHelper.addClinic(name + "clinic");
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        patientId = PatientHelper.createPatient(appService,
            name, study.getId());

        siteId = SiteHelper.createSite(appService, name, "Edmonton",
            ActivityStatusEnum.ACTIVE,
            new HashSet<Integer>(study.getId()));
    }

    @Test
    public void testSaveWithoutSpecimens() throws Exception {
        String worksheet = Utils.getRandomString(50);
        List<CommentInfo> comments = Utils.getRandomCommentInfos(currentUser
            .getId());
        Date date = Utils.getRandomDate();
        Integer pEventId = appService.doAction(new ProcessingEventSaveAction(
            null, siteId, date, worksheet, 1, comments, null));

        openHibernateSession();
        // Check ProcessingEvent is in database with correct values
        ProcessingEvent pevent = (ProcessingEvent) session.get(
            ProcessingEvent.class, pEventId);
        Assert.assertEquals(worksheet, pevent.getWorksheet());
        Assert.assertEquals(comments.size(), pevent.getCommentCollection()
            .size());
        Assert.assertTrue(compareDateInHibernate(date, pevent.getCreatedAt()));
        Assert.assertEquals(0, pevent.getSpecimenCollection().size());
        closeHibernateSession();
    }

    @Test
    public void testSaveWithSpecimens() throws Exception {
        String worksheet = Utils.getRandomString(50);
        List<CommentInfo> comments = Utils.getRandomCommentInfos(currentUser
            .getId());
        Date date = Utils.getRandomDate();

        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(appService,
                patientId, siteId);
        ArrayList<SpecimenInfo> sourceSpecs = appService
            .doAction(new CollectionEventGetSpecimenInfosAction(ceventId,
                false));

        // create a processing event with one of the collection event source
        // specimen
        Integer pEventId = appService.doAction(new ProcessingEventSaveAction(
            null, siteId, date, worksheet, 1, comments, Arrays
                .asList(sourceSpecs.get(0).specimen.getId())));

        // FIXME should test to add specimens that can't add ???

        openHibernateSession();
        // Check ProcessingEvent is in database with correct values
        ProcessingEvent pevent = (ProcessingEvent) session.get(
            ProcessingEvent.class, pEventId);
        Assert.assertEquals(worksheet, pevent.getWorksheet());
        Assert.assertEquals(comments.size(), pevent.getCommentCollection()
            .size());
        Assert.assertTrue(compareDateInHibernate(date, pevent.getCreatedAt()));
        Assert.assertEquals(1, pevent.getSpecimenCollection().size());
        closeHibernateSession();
    }

    @Test
    public void testSaveSameWorksheet() throws Exception {
        String worksheet = Utils.getRandomString(50);
        Date date = Utils.getRandomDate();
        appService.doAction(new ProcessingEventSaveAction(
            null, siteId, date, worksheet, 1, null, null));

        // try to save another pevent with the same worksheet
        try {
            appService.doAction(new ProcessingEventSaveAction(null, siteId,
                new Date(), worksheet, 1, null, null));
            Assert
                .fail("should not be able to use the same worksheet to 2 different pevents");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        Integer pEventId = appService.doAction(new ProcessingEventSaveAction(
            null, siteId, Utils.getRandomDate(), Utils
                .getRandomString(50), 1, null, null));

        appService.doAction(new ProcessingEventDeleteAction(pEventId));

        openHibernateSession();
        ProcessingEvent pe = ActionUtil.sessionGet(session,
            ProcessingEvent.class, pEventId);
        Assert.assertNull(pe);
        closeHibernateSession();
    }

    @Test
    public void testDeleteWithSourcesSpecimens() throws Exception {
        // add cevent and source specimens
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(appService,
                patientId, siteId);
        ArrayList<SpecimenInfo> sourceSpecs = appService
            .doAction(new CollectionEventGetSpecimenInfosAction(ceventId,
                false));
        Integer spcId = sourceSpecs.get(0).specimen.getId();

        // create a processing event with one of the collection event source
        // specimen.
        Integer pEventId = appService.doAction(new ProcessingEventSaveAction(
            null, siteId, Utils.getRandomDate(), Utils
                .getRandomString(50), 1, null,
            Arrays
                .asList(spcId)));

        openHibernateSession();
        Specimen spc = ActionUtil.sessionGet(session, Specimen.class, spcId);
        Assert.assertNotNull(spc);
        Assert.assertNotNull(spc.getProcessingEvent());
        Assert.assertEquals(pEventId, spc.getProcessingEvent().getId());
        closeHibernateSession();

        // delete this processing event. Can do it since the specimen has no
        // children
        appService.doAction(new ProcessingEventDeleteAction(pEventId));

        openHibernateSession();
        ProcessingEvent pe = ActionUtil.sessionGet(session,
            ProcessingEvent.class, pEventId);
        Assert.assertNull(pe);
        spc = ActionUtil.sessionGet(session, Specimen.class, spcId);
        session.refresh(spc);
        Assert.assertNotNull(spc);
        Assert.assertNull(spc.getProcessingEvent());
        closeHibernateSession();
    }

    @Test
    public void testDeleteWithAliquotedSpecimens() throws Exception {
        // add cevent and source specimens
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(appService,
                patientId, siteId);
        ArrayList<SpecimenInfo> sourceSpecs = appService
            .doAction(new CollectionEventGetSpecimenInfosAction(ceventId,
                false));
        Integer spcId = sourceSpecs.get(0).specimen.getId();

        // FIXME need to add a child to the source specimen

        // create a processing event with one of the collection event source
        // specimen.
        Integer pEventId = appService.doAction(new ProcessingEventSaveAction(
            null, siteId, Utils.getRandomDate(), Utils
                .getRandomString(50), 1, null,
            Arrays
                .asList(spcId)));

        openHibernateSession();
        Specimen spc = ActionUtil.sessionGet(session, Specimen.class, spcId);
        Assert.assertNotNull(spc);
        Assert.assertNotNull(spc.getProcessingEvent());
        Assert.assertEquals(pEventId, spc.getProcessingEvent().getId());
        closeHibernateSession();

        // delete this processing event. Can do it since the specimen has no
        // children
        try {
            appService.doAction(new ProcessingEventDeleteAction(pEventId));
            Assert
                .fail("one of the source specimen of this pevent has children. "
                    + "Can't delete the processing event");
        } catch (ModelIsUsedException e) {
            Assert.assertTrue(true);
        }

        openHibernateSession();
        ProcessingEvent pe = ActionUtil.sessionGet(session,
            ProcessingEvent.class, pEventId);
        Assert.assertNotNull(pe);
        closeHibernateSession();
    }
}
