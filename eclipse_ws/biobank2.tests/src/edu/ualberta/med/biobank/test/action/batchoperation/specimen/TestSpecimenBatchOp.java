package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class TestSpecimenBatchOp extends TestAction {

    private static Logger log = LoggerFactory
        .getLogger(TestSpecimenBatchOp.class.getName());

    private static final String CSV_NAME = "import_specimens.csv";

    private SpecimenBatchOpPojoHelper specimenCsvHelper;

    private final Set<OriginInfo> originInfos = new HashSet<OriginInfo>();

    private Transaction tx;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        specimenCsvHelper =
            new SpecimenBatchOpPojoHelper(factory.getNameGenerator());

        tx = session.beginTransaction();
        factory.createSite();
        factory.createClinic();
        factory.createStudy();

        // add 2 shipments
        //
        // source center on origin info will be the clinic created above
        factory.createShipmentInfo();
        originInfos.add(factory.createOriginInfo());
        factory.createShipmentInfo();
        originInfos.add(factory.createOriginInfo());
    }

    @Test
    public void noErrorsNoContainers() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        patients.add(factory.createPatient());
        patients.add(factory.createPatient());
        patients.add(factory.createPatient());

        Set<SourceSpecimen> sourceSpecimens = new HashSet<SourceSpecimen>();
        sourceSpecimens.add(factory.createSourceSpecimen());
        factory.createSpecimenType();
        sourceSpecimens.add(factory.createSourceSpecimen());
        factory.createSpecimenType();
        sourceSpecimens.add(factory.createSourceSpecimen());

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens =
            new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        tx.commit();

        ArrayList<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.createAllSpecimens(
                factory.getDefaultStudy(), originInfos, patients);

        // write out to CSV file so we can view data
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), csvInfos,
                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    // test with 1000 patients
    @Ignore
    @Test
    public void manyPatients() throws Exception {
        final int numPatients = 1000;

        Set<Patient> patients = new HashSet<Patient>();

        for (int i = 0; i < numPatients; i++) {
            patients.add(factory.createPatient());
        }

        Set<SourceSpecimen> sourceSpecimens = new HashSet<SourceSpecimen>();
        sourceSpecimens.add(factory.createSourceSpecimen());
        factory.createSpecimenType();

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens =
            new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        tx.commit();

        Set<OriginInfo> testOriginInfos = new HashSet<OriginInfo>();

        ArrayList<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.createAllSpecimens(
                factory.getDefaultStudy(), testOriginInfos, patients);

        // write out to CSV file so we can view data
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), csvInfos,
                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void onlyParentSpecimensInCsv() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
            }
        }

        tx.commit();

        // make sure you can add parent specimens without a worksheet #
        ArrayList<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.sourceSpecimensCreate(originInfos, patients,
                factory.getDefaultStudy().getSourceSpecimens());

        // remove the worksheet # for the last half
        int half = csvInfos.size() / 2;
        int count = 0;
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            if (count > half) {
                csvInfo.setWorksheet(null);
            }
            ++count;
        }

        try {

            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), csvInfos,
                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    /*
     * Test if we can import aliquoted specimens only.
     * 
     * The CSV file has no positions here.
     */
    @Test
    public void onlyChildSpecimensInCsv() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
                Specimen parentSpecimen = factory.createParentSpecimen();
                parentSpecimen.setProcessingEvent(factory
                    .createProcessingEvent());
                parentSpecimens.add(parentSpecimen);
            }
        }

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens =
            new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        tx.commit();

        ArrayList<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.createAliquotedSpecimens(
                factory.getDefaultStudy(), parentSpecimens);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), csvInfos,
                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void onlyChildSpecimensNoCollectionEvent() throws Exception {
        factory.createSpecimenType();
        AliquotedSpecimen aliquotedSpecimen =
            factory.createAliquotedSpecimen();

        tx.commit();

        ArrayList<SpecimenBatchOpInputPojo> csvInfos =
            new ArrayList<SpecimenBatchOpInputPojo>();
        csvInfos.add(
            specimenCsvHelper.aliquotedSpecimenCreate(null,
                aliquotedSpecimen.getSpecimenType().getName()));
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), csvInfos,
                    new File(CSV_NAME));
            exec(importAction);
            Assert
                .fail("should not be allowed to create aliquot specimens with no collection events");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(SpecimenBatchOpAction.CSV_CEVENT_ERROR
                    .format());
        }
    }

    @Test
    public void missingPatient() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Patient patient = factory.createPatient();
        patients.add(patient);

        Set<SourceSpecimen> sourceSpecimens = new HashSet<SourceSpecimen>();
        sourceSpecimens.add(factory.createSourceSpecimen());

        Set<AliquotedSpecimen> aliquotedSpecimens =
            new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        tx.commit();

        ArrayList<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.createAllSpecimens(factory.getDefaultStudy(),
                originInfos, patients);
        // set all patient numbers to null
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            csvInfo.setPatientNumber("");
        }
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), csvInfos,
                    new File(CSV_NAME));
            exec(importAction);
            Assert
                .fail("should not be allowed to import spcecimens when patient number is missing");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
        }
    }

    @Test
    public void withComments() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
                Specimen parentSpecimen = factory.createParentSpecimen();
                parentSpecimen.setProcessingEvent(factory
                    .createProcessingEvent());
                parentSpecimens.add(parentSpecimen);
            }
        }

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens =
            new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        tx.commit();

        ArrayList<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.createAliquotedSpecimens(
                factory.getDefaultStudy(), parentSpecimens);
        specimenCsvHelper.addComments(csvInfos);
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), csvInfos,
                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void noErrorsWithContainers() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        patients.add(factory.createPatient());
        patients.add(factory.createPatient());
        patients.add(factory.createPatient());

        Set<SourceSpecimen> sourceSpecimens = new HashSet<SourceSpecimen>();
        sourceSpecimens.add(factory.createSourceSpecimen());
        factory.createSpecimenType();
        sourceSpecimens.add(factory.createSourceSpecimen());
        factory.createSpecimenType();
        sourceSpecimens.add(factory.createSourceSpecimen());

        // create a new specimen type for the aliquoted specimens
        SpecimenType aqSpecimenType = factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens =
            new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        Set<Container> childL2Containers = createContainers(aqSpecimenType);

        tx.commit();

        ArrayList<SpecimenBatchOpInputPojo> csvInfos =
            new ArrayList<SpecimenBatchOpInputPojo>();
        csvInfos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), originInfos, patients);

        // only the aliquoted specimens will have a position
        List<SpecimenBatchOpInputPojo> aliquotedSpecimensCsvInfos =
            new ArrayList<SpecimenBatchOpInputPojo>();

        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            if (csvInfo.getSourceSpecimen()) continue;
            aliquotedSpecimensCsvInfos.add(csvInfo);
        }

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(
            aliquotedSpecimensCsvInfos, childL2Containers);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), csvInfos,
                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void onlyParentSpecimensInCsvWithPositions() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
            }
        }

        Set<Container> childL2Containers =
            createContainers(factory.getDefaultSourceSpecimenType());

        tx.commit();

        // make sure you can add parent specimens without a worksheet #
        ArrayList<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.sourceSpecimensCreate(originInfos, patients,
                factory.getDefaultStudy().getSourceSpecimens());

        specimenCsvHelper
            .fillContainersWithSpecimenBatchOpPojos(
                new ArrayList<SpecimenBatchOpInputPojo>(csvInfos),
                childL2Containers);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), csvInfos,
                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void withExistingPevents() {
        // TODO
    }

    @Test
    public void aliquotsWithNoParentSpc() {
        // TODO
    }

    private void checkCsvInfoAgainstDb(
        ArrayList<SpecimenBatchOpInputPojo> csvInfos) {
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            log.trace("checking specimen against db: inventory id {}",
                csvInfo.getInventoryId());

            Specimen specimen = (Specimen) session
                .createCriteria(Specimen.class)
                .add(Restrictions.eq("inventoryId", csvInfo.getInventoryId()))
                .uniqueResult();

            Assert.assertEquals(csvInfo.getSpecimenType(),
                specimen.getSpecimenType().getName());
            Assert.assertEquals(0, DateCompare.compare(csvInfo.getCreatedAt(),
                specimen.getCreatedAt()));

            if (csvInfo.getPatientNumber() != null) {
                Assert.assertEquals(csvInfo.getPatientNumber(), specimen
                    .getCollectionEvent().getPatient().getPnumber());
            }

            Assert.assertNotNull(specimen.getCollectionEvent());

            if (csvInfo.getVisitNumber() != null) {
                Assert.assertEquals(csvInfo.getVisitNumber(), specimen
                    .getCollectionEvent().getVisitNumber());
            }

            if (specimen.getOriginInfo().getShipmentInfo() != null) {
                Assert.assertEquals(csvInfo.getWaybill(), specimen
                    .getOriginInfo().getShipmentInfo().getWaybill());
            }

            if (csvInfo.getSourceSpecimen()) {
                Assert.assertNotNull(specimen.getOriginalCollectionEvent());

                if ((csvInfo.getWorksheet() != null)
                    && !csvInfo.getWorksheet().isEmpty()) {
                    Assert.assertNotNull(specimen.getProcessingEvent());
                    Assert.assertEquals(csvInfo.getWorksheet(), specimen
                        .getProcessingEvent().getWorksheet());
                }
            } else {
                Assert.assertEquals(csvInfo.getParentInventoryId(),
                    specimen.getParentSpecimen().getInventoryId());
                Assert.assertNull(specimen.getOriginalCollectionEvent());
                Assert.assertNotNull(specimen.getParentSpecimen()
                    .getProcessingEvent());
            }

            if ((csvInfo.getPalletPosition() != null)
                && !csvInfo.getPalletPosition().isEmpty()) {
                Assert.assertEquals(csvInfo.getPalletPosition(),
                    specimen.getSpecimenPosition().getPositionString());
                Assert.assertEquals(csvInfo.getPalletLabel(),
                    specimen.getSpecimenPosition().getContainer().getLabel());

            }

            if ((csvInfo.getComment() != null)
                && !csvInfo.getComment().isEmpty()) {
                Assert.assertEquals(1, specimen.getComments().size());
                Assert.assertEquals(csvInfo.getComment(),
                    specimen.getComments().iterator().next().getMessage());

            }
        }
    }

    // need to add allowed specimen type to the leaft containers' container
    // types
    private Set<Container> createContainers(SpecimenType specimenType) {
        factory.createTopContainer();
        factory.createParentContainer();

        ContainerType ctype = factory.createContainerType();
        ctype.getChildContainerTypes().clear();
        ctype.getSpecimenTypes().clear();
        ctype.getSpecimenTypes().add(specimenType);

        Set<Container> result = new HashSet<Container>();
        result.add(factory.createContainer());
        result.add(factory.createContainer());
        result.add(factory.createContainer());

        return result;
    }
}
