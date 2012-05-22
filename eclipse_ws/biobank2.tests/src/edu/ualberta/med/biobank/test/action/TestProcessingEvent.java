package edu.ualberta.med.biobank.test.action;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetSourceSpecimenListInfoAction;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.originInfo.OriginInfoSaveAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventDeleteAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction.PEventInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;

public class TestProcessingEvent extends ActionTest {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Provisioning provisioning;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();
        provisioning = new Provisioning(getExecutor(), name);
    }

    @Test
    public void saveWithoutSpecimens() throws Exception {
        String worksheet = Utils.getRandomString(20, 50);
        String commentText = Utils.getRandomString(20, 30);
        Date date = Utils.getRandomDate();
        Integer pEventId =
            exec(
                new ProcessingEventSaveAction(
                    null, provisioning.siteId, date, worksheet,
                    ActivityStatus.ACTIVE, commentText,
                    new HashSet<Integer>(), new HashSet<Integer>())).getId();

        // Check ProcessingEvent is in database with correct values
        PEventInfo peventInfo =
            exec(new ProcessingEventGetInfoAction(pEventId));

        Assert.assertEquals(worksheet, peventInfo.pevent.getWorksheet());
        Assert.assertEquals(1, peventInfo.pevent.getComments().size());
        Assert.assertEquals(date, peventInfo.pevent.getCreatedAt());
        Assert
            .assertEquals(0, peventInfo.sourceSpecimenInfos.size());
    }

    @Test
    public void saveWithSpecimens() throws Exception {
        String worksheet = Utils.getRandomString(50);
        String commentText = Utils.getRandomString(20, 30);
        Date date = Utils.getRandomDate();

        // create a collection event from a clinic
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(getExecutor(),
                provisioning.patientIds.get(0), provisioning.clinicId);
        CEventInfo ceventInfo =
            exec(new CollectionEventGetInfoAction(ceventId));
        List<SpecimenInfo> sourceSpecs = ceventInfo.sourceSpecimenInfos;

        // ship the specimens to the site
        OriginInfoSaveInfo oiSaveInfo = new OriginInfoSaveInfo(
            null, provisioning.siteId, provisioning.clinicId, null,
            SpecimenInfo.getSpecimenIds(sourceSpecs), null);
        ShipmentInfoSaveInfo shipSaveInfo = new ShipmentInfoSaveInfo(
            null, Utils.getRandomString(2, 5), Utils.getRandomDate(),
            Utils.getRandomDate(), Utils.getRandomString(2, 5),
            getShippingMethods().get(0).getId());
        getExecutor()
            .exec(new OriginInfoSaveAction(oiSaveInfo, shipSaveInfo));

        // create a processing event with one of the collection event source
        // specimen
        Integer pEventId =
            exec(
                new ProcessingEventSaveAction(
                    null, provisioning.siteId, date, worksheet,
                    ActivityStatus.ACTIVE, commentText,
                    new HashSet<Integer>(
                        Arrays.asList(sourceSpecs.get(0).specimen.getId())),
                    new HashSet<Integer>()))
                .getId();

        // sourceSpecs.get(0) will have its activity status set to closed now
        sourceSpecs.get(0).specimen.setActivityStatus(ActivityStatus.CLOSED);

        // create aliquoted specimens by doing a scan link
        Set<AliquotedSpecimenInfo> aliquotedSpecimenInfos =
            new HashSet<AliquotedSpecimenInfo>();
        for (int i = 0, n = getR().nextInt(5) + 1; i < n; i++) {
            AliquotedSpecimenInfo aliquotedSpecimenInfo =
                new AliquotedSpecimenInfo();
            aliquotedSpecimenInfo.inventoryId = Utils.getRandomString(10, 15);
            aliquotedSpecimenInfo.typeId = getSpecimenTypes().get(0).getId();
            aliquotedSpecimenInfo.activityStatus = ActivityStatus.ACTIVE;
            aliquotedSpecimenInfo.parentSpecimenId =
                sourceSpecs.get(0).specimen.getId();
            aliquotedSpecimenInfo.containerId = null;
            aliquotedSpecimenInfo.position = null;
            aliquotedSpecimenInfos.add(aliquotedSpecimenInfo);
        }

        exec(new SpecimenLinkSaveAction(provisioning.siteId,
            provisioning.studyId, aliquotedSpecimenInfos));

        // Check ProcessingEvent is in database with correct values
        PEventInfo peventInfo =
            exec(new ProcessingEventGetInfoAction(pEventId));

        Assert.assertEquals(worksheet, peventInfo.pevent.getWorksheet());
        Assert.assertEquals(1, peventInfo.pevent.getComments().size());
        Assert.assertEquals(date, peventInfo.pevent.getCreatedAt());
        Assert
            .assertEquals(1, peventInfo.sourceSpecimenInfos.size());

        ClinicInfo clinicInfo =
            exec(new ClinicGetInfoAction(provisioning.clinicId));
        SiteInfo siteInfo =
            exec(new SiteGetInfoAction(provisioning.siteId));
        PatientInfo patientInfo =
            exec(new PatientGetInfoAction(provisioning.patientIds
                .get(0)));

        // check eager loaded associations
        for (SpecimenInfo specimenInfo : peventInfo.sourceSpecimenInfos) {
            Assert.assertEquals(sourceSpecs.get(0).specimen.getSpecimenType()
                .getName(), specimenInfo.specimen.getSpecimenType().getName());

            Assert.assertEquals(
                sourceSpecs.get(0).specimen.getActivityStatus(),
                specimenInfo.specimen.getActivityStatus());

            Assert.assertEquals(clinicInfo.clinic.getName(),
                specimenInfo.specimen.getOriginInfo().getCenter().getName());

            Assert.assertEquals(siteInfo.getSite().getName(),
                specimenInfo.specimen.getCurrentCenter().getName());

            Assert.assertEquals(patientInfo.patient.getPnumber(),
                specimenInfo.specimen.getCollectionEvent().getPatient()
                    .getPnumber());

            Assert.assertEquals(patientInfo.patient.getStudy().getName(),
                specimenInfo.specimen.getCollectionEvent().getPatient()
                    .getStudy().getName());

        }
    }

    @Test
    public void delete() throws Exception {
        Integer pEventId = exec(new ProcessingEventSaveAction(
            null, provisioning.siteId, Utils.getRandomDate(), Utils
                .getRandomString(50), ActivityStatus.ACTIVE, null,
            new HashSet<Integer>(), new HashSet<Integer>())).getId();

        PEventInfo peventInfo =
            exec(new ProcessingEventGetInfoAction(pEventId));
        exec(new ProcessingEventDeleteAction(peventInfo.pevent));

        try {
            exec(new ProcessingEventGetInfoAction(pEventId));
            Assert
                .fail("one of the source specimen of this pevent has children. "
                    + "Can't delete the processing event");
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void deleteWithSourcesSpecimens() throws Exception {
        // add cevent and source specimens
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(getExecutor(),
                provisioning.patientIds.get(0), provisioning.siteId);
        CEventInfo ceventInfo =
            exec(new CollectionEventGetInfoAction(ceventId));
        List<SpecimenInfo> sourceSpecs = ceventInfo.sourceSpecimenInfos;
        Integer spcId = sourceSpecs.get(0).specimen.getId();

        // create a processing event with one of the collection event source
        // specimen.
        Integer pEventId =
            exec(
                new ProcessingEventSaveAction(
                    null, provisioning.siteId, Utils.getRandomDate(), Utils
                        .getRandomString(50), ActivityStatus.ACTIVE, null,
                    new HashSet<Integer>(Arrays.asList(spcId)),
                    new HashSet<Integer>())).getId();

        Specimen spc = (Specimen) session.load(Specimen.class, spcId);
        Assert.assertNotNull(spc);
        Assert.assertNotNull(spc.getProcessingEvent());
        Assert.assertEquals(pEventId, spc.getProcessingEvent().getId());

        // delete this processing event. Can do it since the specimen has no
        // children
        PEventInfo peventInfo =
            exec(new ProcessingEventGetInfoAction(pEventId));
        exec(new ProcessingEventDeleteAction(peventInfo.pevent));

        try {
            exec(new ProcessingEventGetInfoAction(pEventId));
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
        // add cevent and source specimens
        Integer ceventId = CollectionEventHelper
            .createCEventWithSourceSpecimens(getExecutor(),
                provisioning.patientIds.get(0), provisioning.siteId);
        List<SpecimenInfo> sourceSpecs = exec(
            new CollectionEventGetSourceSpecimenListInfoAction(ceventId))
            .getList();
        Integer spcId = sourceSpecs.get(0).specimen.getId();

        // FIXME need to add a child to the source specimen

        // create a processing event with one of the collection event source
        // specimen.
        Integer pEventId =
            exec(
                new ProcessingEventSaveAction(
                    null, provisioning.siteId, Utils.getRandomDate(),
                    Utils.getRandomString(50), ActivityStatus.ACTIVE, null,
                    new HashSet<Integer>(Arrays.asList(spcId)),
                    new HashSet<Integer>())).getId();

        Specimen spc = (Specimen) session.load(Specimen.class, spcId);
        Assert.assertNotNull(spc);
        Assert.assertNotNull(spc.getProcessingEvent());
        Assert.assertEquals(pEventId, spc.getProcessingEvent().getId());

        // delete this processing event. Can do it since the specimen has no
        // children
        PEventInfo peventInfo =
            exec(new ProcessingEventGetInfoAction(pEventId));
        try {
            exec(new ProcessingEventDeleteAction(peventInfo.pevent));
            Assert
                .fail("one of the source specimen of this pevent has children. "
                    + "Can't delete the processing event");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        ProcessingEvent pe =
            (ProcessingEvent) session.load(ProcessingEvent.class,
                pEventId);
        Assert.assertNotNull(pe);
    }
}
