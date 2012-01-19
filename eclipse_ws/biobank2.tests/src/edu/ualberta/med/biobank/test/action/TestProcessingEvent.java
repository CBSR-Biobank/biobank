package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetSourceSpecimenInfoAction;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.CommentInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventDeleteAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction.PEventInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ModelIsUsedException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;

public class TestProcessingEvent extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + R.nextInt();
    }

    @Test
    public void saveWithoutSpecimens() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        String worksheet = Utils.getRandomString(20, 50);
        List<CommentInfo> comments =
            Utils.getRandomCommentInfos(EXECUTOR.getUserId());
        Date date = Utils.getRandomDate();
        Integer pEventId = EXECUTOR.exec(new ProcessingEventSaveAction(
            null, provisioning.siteId, date, worksheet, 1, comments,
            new HashSet<Integer>())).getId();

        // Check ProcessingEvent is in database with correct values
        PEventInfo peventInfo =
            EXECUTOR.exec(new ProcessingEventGetInfoAction(pEventId));

        Assert.assertEquals(worksheet, peventInfo.pevent.getWorksheet());
        Assert.assertEquals(comments.size(), peventInfo.pevent
            .getCommentCollection().size());
        Assert.assertEquals(date, peventInfo.pevent.getCreatedAt());
        Assert
            .assertEquals(0, peventInfo.sourceSpecimenInfos.size());
    }

    @Test
    public void saveWithSpecimens() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        String worksheet = Utils.getRandomString(50);
        List<CommentInfo> comments =
            Utils.getRandomCommentInfos(EXECUTOR.getUserId());
        Date date = Utils.getRandomDate();

        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(EXECUTOR,
                provisioning.patientIds.get(0), provisioning.siteId);
        ArrayList<SpecimenInfo> sourceSpecs =
            EXECUTOR
                .exec(new CollectionEventGetSourceSpecimenInfoAction(ceventId))
                .getList();

        // create a processing event with one of the collection event source
        // specimen
        Integer pEventId = EXECUTOR.exec(
            new ProcessingEventSaveAction(
                null, provisioning.siteId, date, worksheet, 1, comments,
                new HashSet<Integer>(
                    Arrays.asList(sourceSpecs.get(0).specimen.getId()))))
            .getId();

        // FIXME should test to add specimens that can't add ???

        // Check ProcessingEvent is in database with correct values
        PEventInfo peventInfo =
            EXECUTOR.exec(new ProcessingEventGetInfoAction(pEventId));

        Assert.assertEquals(worksheet, peventInfo.pevent.getWorksheet());
        Assert.assertEquals(comments.size(), peventInfo.pevent
            .getCommentCollection().size());
        Assert.assertEquals(date, peventInfo.pevent.getCreatedAt());
        Assert
            .assertEquals(1, peventInfo.sourceSpecimenInfos.size());
    }

    @Test
    public void saveSameWorksheet() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        String worksheet = Utils.getRandomString(50);
        Date date = Utils.getRandomDate();
        EXECUTOR.exec(new ProcessingEventSaveAction(
            null, provisioning.siteId, date, worksheet, 1, null,
            new HashSet<Integer>()));

        // try to save another pevent with the same worksheet
        try {
            EXECUTOR.exec(new ProcessingEventSaveAction(null,
                provisioning.siteId, new Date(), worksheet, 1, null,
                new HashSet<Integer>()));
            Assert
                .fail("should not be able to use the same worksheet to 2 different pevents");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void delete() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        Integer pEventId = EXECUTOR.exec(new ProcessingEventSaveAction(
            null, provisioning.siteId, Utils.getRandomDate(), Utils
                .getRandomString(50), 1, null,
            new HashSet<Integer>())).getId();

        EXECUTOR.exec(new ProcessingEventDeleteAction(pEventId));

        try {
            EXECUTOR.exec(new ProcessingEventGetInfoAction(pEventId));
            Assert
                .fail("one of the source specimen of this pevent has children. "
                    + "Can't delete the processing event");
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void deleteWithSourcesSpecimens() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        // add cevent and source specimens
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(EXECUTOR,
                provisioning.patientIds.get(0), provisioning.siteId);
        ArrayList<SpecimenInfo> sourceSpecs =
            EXECUTOR
                .exec(new CollectionEventGetSourceSpecimenInfoAction(ceventId))
                .getList();
        Integer spcId = sourceSpecs.get(0).specimen.getId();

        // create a processing event with one of the collection event source
        // specimen.
        Integer pEventId = EXECUTOR.exec(new ProcessingEventSaveAction(
            null, provisioning.siteId, Utils.getRandomDate(), Utils
                .getRandomString(50), 1, null,
            new HashSet<Integer>(Arrays.asList(spcId)))).getId();

        Specimen spc = (Specimen) session.load(Specimen.class, spcId);
        Assert.assertNotNull(spc);
        Assert.assertNotNull(spc.getProcessingEvent());
        Assert.assertEquals(pEventId, spc.getProcessingEvent().getId());

        // delete this processing event. Can do it since the specimen has no
        // children
        EXECUTOR.exec(new ProcessingEventDeleteAction(pEventId));

        try {
            EXECUTOR.exec(new ProcessingEventGetInfoAction(pEventId));
            Assert.fail("processing event still exists");
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(true);
        }

        session.clear();
        spc = (Specimen) session.load(Specimen.class, spcId);
        session.refresh(spc);
        Assert.assertNotNull(spc);
        Assert.assertNull(spc.getProcessingEvent());
    }

    @Ignore
    @Test
    /*
     * Need way to create aliquoted specimens
     */
    public void deleteWithAliquotedSpecimens() throws Exception {
        Provisioning provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);

        // add cevent and source specimens
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(EXECUTOR,
                provisioning.patientIds.get(0), provisioning.siteId);
        ArrayList<SpecimenInfo> sourceSpecs = EXECUTOR.exec(
            new CollectionEventGetSourceSpecimenInfoAction(ceventId))
            .getList();
        Integer spcId = sourceSpecs.get(0).specimen.getId();

        // FIXME need to add a child to the source specimen

        // create a processing event with one of the collection event source
        // specimen.
        Integer pEventId = EXECUTOR.exec(
            new ProcessingEventSaveAction(
                null, provisioning.siteId, Utils.getRandomDate(),
                Utils.getRandomString(50), 1, null,
                new HashSet<Integer>(Arrays.asList(spcId)))).getId();

        Specimen spc = (Specimen) session.load(Specimen.class, spcId);
        Assert.assertNotNull(spc);
        Assert.assertNotNull(spc.getProcessingEvent());
        Assert.assertEquals(pEventId, spc.getProcessingEvent().getId());

        // delete this processing event. Can do it since the specimen has no
        // children
        try {
            EXECUTOR.exec(new ProcessingEventDeleteAction(pEventId));
            Assert
                .fail("one of the source specimen of this pevent has children. "
                    + "Can't delete the processing event");
        } catch (ModelIsUsedException e) {
            Assert.assertTrue(true);
        }

        ProcessingEvent pe =
            (ProcessingEvent) session.load(ProcessingEvent.class,
                pEventId);
        Assert.assertNotNull(pe);
    }
}
