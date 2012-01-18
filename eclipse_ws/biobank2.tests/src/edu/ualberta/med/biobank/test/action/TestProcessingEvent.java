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

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.clinic.ContactSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetSpecimenInfosAction;
import edu.ualberta.med.biobank.common.action.info.CommentInfo;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventDeleteAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ModelIsUsedException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.PatientHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestProcessingEvent extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;

    private Integer siteId;
    private Integer studyId;
    private ClinicWrapper clinic;
    private Integer patientId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + R.nextInt();

        studyId = StudyHelper.createStudy(EXECUTOR, name,
            ActivityStatusEnum.ACTIVE);

        Integer clinicId =
            ClinicHelper.createClinic(EXECUTOR, name + "_clinic",
                ActivityStatusEnum.ACTIVE);
        ContactSaveAction contactSave = new ContactSaveAction();
        contactSave.setName(name + "_contact");
        contactSave.setClinicId(clinicId);
        Integer contactId = EXECUTOR.exec(contactSave).getId();

        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(studyId));
        StudySaveAction studySaveAction =
            StudyHelper.getSaveAction(EXECUTOR, studyInfo);
        studySaveAction.setContactIds(new HashSet<Integer>(contactId));
        EXECUTOR.exec(studySaveAction);

        patientId = PatientHelper.createPatient(EXECUTOR,
            name, studyId);

        siteId = SiteHelper.createSite(EXECUTOR, name, "Edmonton",
            ActivityStatusEnum.ACTIVE,
            new HashSet<Integer>(studyId));
    }

    @Test
    public void saveWithoutSpecimens() throws Exception {
        String worksheet = Utils.getRandomString(20, 50);
        List<CommentInfo> comments =
            Utils.getRandomCommentInfos(EXECUTOR.getUserId());
        Date date = Utils.getRandomDate();
        Integer pEventId = EXECUTOR.exec(new ProcessingEventSaveAction(
            null, siteId, date, worksheet, 1, comments, null)).getId();

        // Check ProcessingEvent is in database with correct values
        ProcessingEvent pevent = (ProcessingEvent) session.get(
            ProcessingEvent.class, pEventId);
        Assert.assertEquals(worksheet, pevent.getWorksheet());
        Assert.assertEquals(comments.size(), pevent.getCommentCollection()
            .size());
        Assert.assertEquals(date, pevent.getCreatedAt());
        Assert.assertEquals(0, pevent.getSpecimenCollection().size());
    }

    @Test
    public void sveWithSpecimens() throws Exception {
        String worksheet = Utils.getRandomString(50);
        List<CommentInfo> comments =
            Utils.getRandomCommentInfos(EXECUTOR.getUserId());
        Date date = Utils.getRandomDate();

        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(EXECUTOR,
                patientId, siteId);
        ArrayList<SpecimenInfo> sourceSpecs = EXECUTOR
            .exec(new CollectionEventGetSpecimenInfosAction(ceventId,
                false)).getList();

        // create a processing event with one of the collection event source
        // specimen
        Integer pEventId = EXECUTOR.exec(new ProcessingEventSaveAction(
            null, siteId, date, worksheet, 1, comments, Arrays
                .asList(sourceSpecs.get(0).specimen.getId()))).getId();

        // FIXME should test to add specimens that can't add ???

        // Check ProcessingEvent is in database with correct values
        ProcessingEvent pevent = (ProcessingEvent) session.get(
            ProcessingEvent.class, pEventId);
        Assert.assertEquals(worksheet, pevent.getWorksheet());
        Assert.assertEquals(comments.size(), pevent.getCommentCollection()
            .size());
        Assert.assertEquals(date, pevent.getCreatedAt());
        Assert.assertEquals(1, pevent.getSpecimenCollection().size());
    }

    @Test
    public void saveSameWorksheet() throws Exception {
        String worksheet = Utils.getRandomString(50);
        Date date = Utils.getRandomDate();
        EXECUTOR.exec(new ProcessingEventSaveAction(
            null, siteId, date, worksheet, 1, null, null));

        // try to save another pevent with the same worksheet
        try {
            EXECUTOR.exec(new ProcessingEventSaveAction(null, siteId,
                new Date(), worksheet, 1, null, null));
            Assert
                .fail("should not be able to use the same worksheet to 2 different pevents");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void delete() throws Exception {
        Integer pEventId = EXECUTOR.exec(new ProcessingEventSaveAction(
            null, siteId, Utils.getRandomDate(), Utils
                .getRandomString(50), 1, null, null)).getId();

        EXECUTOR.exec(new ProcessingEventDeleteAction(pEventId));

        ProcessingEvent pe = (ProcessingEvent) session.load(
            ProcessingEvent.class, pEventId);
        Assert.assertNull(pe);
    }

    @Test
    public void deleteWithSourcesSpecimens() throws Exception {
        // add cevent and source specimens
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(EXECUTOR,
                patientId, siteId);
        ArrayList<SpecimenInfo> sourceSpecs = EXECUTOR
            .exec(new CollectionEventGetSpecimenInfosAction(ceventId,
                false)).getList();
        Integer spcId = sourceSpecs.get(0).specimen.getId();

        // create a processing event with one of the collection event source
        // specimen.
        Integer pEventId = EXECUTOR.exec(new ProcessingEventSaveAction(
            null, siteId, Utils.getRandomDate(), Utils
                .getRandomString(50), 1, null,
            Arrays
                .asList(spcId))).getId();

        Specimen spc = (Specimen) session.load(Specimen.class, spcId);
        Assert.assertNotNull(spc);
        Assert.assertNotNull(spc.getProcessingEvent());
        Assert.assertEquals(pEventId, spc.getProcessingEvent().getId());

        // delete this processing event. Can do it since the specimen has no
        // children
        EXECUTOR.exec(new ProcessingEventDeleteAction(pEventId));

        ProcessingEvent pe =
            (ProcessingEvent) session.get(
                ProcessingEvent.class, pEventId);
        Assert.assertNull(pe);
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
        // add cevent and source specimens
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(EXECUTOR,
                patientId, siteId);
        ArrayList<SpecimenInfo> sourceSpecs = EXECUTOR
            .exec(new CollectionEventGetSpecimenInfosAction(ceventId,
                false)).getList();
        Integer spcId = sourceSpecs.get(0).specimen.getId();

        // FIXME need to add a child to the source specimen

        // create a processing event with one of the collection event source
        // specimen.
        Integer pEventId = EXECUTOR.exec(
            new ProcessingEventSaveAction(
                null, siteId, Utils.getRandomDate(),
                Utils.getRandomString(50), 1, null,
                Arrays.asList(spcId))).getId();

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
