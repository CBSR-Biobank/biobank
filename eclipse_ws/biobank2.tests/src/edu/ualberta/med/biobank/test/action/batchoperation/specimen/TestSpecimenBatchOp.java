package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpGetResult;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpGetAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

/**
 *
 * @author Nelson Loyola
 *
 */
public class TestSpecimenBatchOp extends TestAction {

    private static Logger log = LoggerFactory.getLogger(TestSpecimenBatchOp.class);

    private static final String CSV_NAME = "import_specimens.csv";

    private SpecimenBatchOpPojoHelper specimenCsvHelper;

    private final Set<OriginInfo> originInfos = new HashSet<OriginInfo>();

    private NameGenerator nameGenerator;

    private Site defaultSite;

    private Clinic defaultClinic;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        nameGenerator = new NameGenerator("test_" + getMethodNameR());
        specimenCsvHelper = new SpecimenBatchOpPojoHelper(nameGenerator);

        // delete the CSV file if it exists
        File file = new File(CSV_NAME);
        file.delete();

        session.beginTransaction();
        defaultSite = factory.createSite();
        defaultClinic = factory.createClinic();
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
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), originInfos, patients);

        // write out to CSV file so we can view data
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    // test with large number of patients
    @Ignore
    @Test
    public void manyPatients() throws Exception {
        final int numPatients = 500;

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

        session.getTransaction().commit();

        Set<OriginInfo> testOriginInfos = new HashSet<OriginInfo>();

        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), testOriginInfos, patients);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
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

        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.sourceSpecimensCreate(
            originInfos,
            patients,
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

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void multiplePatientsSameWorksheet() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        for (int i = 0; i < 2; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
        }

        factory.createSourceSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(originInfos, patients,
                factory.getDefaultStudy().getSourceSpecimens());

        // give every parent specimen the same worksheet number
        String worksheet = pojos.iterator().next().getWorksheet();
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setWorksheet(worksheet);
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(pojos);
    }

    @Test
    public void existingWorksheet() throws Exception {
        factory.createSourceSpecimen();
        factory.createPatient();
        ProcessingEvent pevent = factory.createProcessingEvent();
        factory.createParentSpecimen();

        Set<Patient> patients = new HashSet<Patient>();
        patients.add(factory.createPatient());
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos = specimenCsvHelper.sourceSpecimensCreate(
            originInfos, patients, factory.getDefaultStudy().getSourceSpecimens());

        // give every parent specimen the same worksheet number
        String worksheet = pevent.getWorksheet();
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setWorksheet(worksheet);
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(pojos);
    }

    @Test
    public void onlyChildSpecimensNoPevents() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
                parentSpecimens.add(factory.createParentSpecimen());
            }
        }

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.createAliquotedSpecimens(
                factory.getDefaultStudy(), parentSpecimens);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
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
    public void onlyChildSpecimensWithPevents() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Map<String, Specimen> parentSpecimens = new HashMap<String, Specimen>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
                Specimen parentSpecimen = factory.createParentSpecimen();
                parentSpecimen.setProcessingEvent(factory.createProcessingEvent());
                parentSpecimens.put(parentSpecimen.getInventoryId(), parentSpecimen);
            }
        }

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos = specimenCsvHelper.createAliquotedSpecimens(
            factory.getDefaultStudy(), parentSpecimens.values());

        // add the worksheet
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            Specimen parentSpecimen = parentSpecimens.get(pojo.getParentInventoryId());
            pojo.setWorksheet(parentSpecimen.getProcessingEvent().getWorksheet());
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(pojos);
    }

    @Test
    public void onlyChildSpecimensNoParents() throws Exception {
        // the parent specimens will not exist in this test
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        patients.add(factory.createPatient());
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        parentSpecimens.add(factory.createParentSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> inputPojos = specimenCsvHelper.createAliquotedSpecimens(
            factory.getDefaultStudy(), parentSpecimens);

        // change the parent's inventory id to something that does not exist
        for (SpecimenBatchOpInputPojo inputPojo : inputPojos) {
            inputPojo.setParentInventoryId(inputPojo.getParentInventoryId() + "_1");
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, inputPojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), inputPojos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to create aliquot specimens with no parent specimens");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_PARENT_SPC_INV_ID_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void onlyChildSpecimensNoCollectionEvent() throws Exception {
        factory.createSpecimenType();
        AliquotedSpecimen aliquotedSpecimen = factory.createAliquotedSpecimen();

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos = new HashSet<SpecimenBatchOpInputPojo>();
        csvInfos.add(specimenCsvHelper.aliquotedSpecimenCreate(
            null, aliquotedSpecimen.getSpecimenType().getName()));
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to create aliquot specimens with no collection events");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_ALIQ_SPC_PATIENT_CEVENT_MISSING_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void invalidPatientNumber() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createSourceSpecimen();

        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.sourceSpecimensCreate(originInfos, patients,
                factory.getDefaultStudy().getSourceSpecimens());

        // change the patient number to something invalid
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            if (csvInfo.getPatientNumber() != null) {
                csvInfo.setPatientNumber(csvInfo.getPatientNumber() + "_2");
            }
        }
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to create aliquot specimens with invalid patients");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_PATIENT_NUMBER_INVALID_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void missingPatientNumber() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Patient patient = factory.createPatient();
        patients.add(patient);

        Set<SourceSpecimen> sourceSpecimens = new HashSet<SourceSpecimen>();
        sourceSpecimens.add(factory.createSourceSpecimen());

        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), originInfos, patients);
        // set all patient numbers to null
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            csvInfo.setPatientNumber("");
        }
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to import spcecimens when patient number is missing");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
        }
    }

    @Test
    public void invalidSourceSpecimenType() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createSourceSpecimen();

        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.sourceSpecimensCreate(originInfos, patients,
                factory.getDefaultStudy().getSourceSpecimens());

        // change the specimen type to something invalid
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            String stype = csvInfo.getSpecimenType();
            if (stype != null) {
                csvInfo.setSpecimenType(stype + "_x");
            }
        }
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to invalid specimen types");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_SPECIMEN_TYPE_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void invalidAliquotedSpecimenType() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createSourceSpecimen();

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), originInfos, patients);

        // change the specimen type to something invalid
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            String stype = csvInfo.getSpecimenType();
            if ((stype != null) && !csvInfo.getSourceSpecimen()) {
                csvInfo.setSpecimenType(stype + "_x");
            }
        }
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to invalid specimen types");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_SPECIMEN_TYPE_ERROR.format())
                .assertIn(e);
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
                parentSpecimen.setProcessingEvent(factory.createProcessingEvent());
                parentSpecimens.add(parentSpecimen);
            }
        }

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.createAliquotedSpecimens(
            factory.getDefaultStudy(), parentSpecimens);
        specimenCsvHelper.addComments(csvInfos);
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
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

        factory.createSourceSpecimen();
        factory.createSourceSpecimen();
        factory.createSourceSpecimen();

        SpecimenType aqSpecimenType = factory.createSpecimenType();
        factory.setDefaultAliquotedSpecimenType(aqSpecimenType);

        factory.createAliquotedSpecimen();
        factory.createAliquotedSpecimen();
        factory.createAliquotedSpecimen();

        Set<Container> childL2Containers = createContainers(aqSpecimenType);

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos = new HashSet<SpecimenBatchOpInputPojo>();
        csvInfos = specimenCsvHelper.createAllSpecimens(factory.getDefaultStudy(),
                                                        originInfos,
                                                        patients);

        Assert.assertTrue("have CSV data", csvInfos.size() > 0);

        // only the aliquoted specimens will have a position
        List<SpecimenBatchOpInputPojo> aliquotedSpecimensCsvInfos =
            new ArrayList<SpecimenBatchOpInputPojo>();

        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            if (csvInfo.getSourceSpecimen()) continue;
            aliquotedSpecimensCsvInfos.add(csvInfo);
        }

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(
            aliquotedSpecimensCsvInfos, childL2Containers, false);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(factory.getDefaultSite(),
                                                                           csvInfos,
                                                                           new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void onlyParentSpecimensWithLabelsAndPositions() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
            }
        }

        Set<Container> childL2Containers = createContainers(factory.getDefaultSourceSpecimenType());

        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.sourceSpecimensCreate(
            originInfos, patients, factory.getDefaultStudy().getSourceSpecimens());

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(
            new ArrayList<SpecimenBatchOpInputPojo>(csvInfos),
            childL2Containers,
            false);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void withProductBarcodesAndPositions() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
            }
        }

        Set<Container> childL2Containers = createContainers(factory.getDefaultSourceSpecimenType());

        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.sourceSpecimensCreate(
            originInfos, patients, factory.getDefaultStudy().getSourceSpecimens());

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(
            new ArrayList<SpecimenBatchOpInputPojo>(csvInfos),
            childL2Containers,
            true);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void onlyPalletPositions() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createSourceSpecimen();

        Set<Container> childL2Containers = createContainers(factory.getDefaultSourceSpecimenType());

        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.sourceSpecimensCreate(
            originInfos, patients, factory.getDefaultStudy().getSourceSpecimens());

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(
            new ArrayList<SpecimenBatchOpInputPojo>(csvInfos),
            childL2Containers,
            true);

        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            csvInfo.setPalletProductBarcode("");
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_PALLET_POS_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void positionsByLabelAlreadyOccupied() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createSourceSpecimen();
        Specimen specimenWithPosition = factory.createParentSpecimen();

        Set<Container> childL2Containers = createContainers(factory.getDefaultSourceSpecimenType());
        Container container = childL2Containers.iterator().next();

        SpecimenPosition pos = new SpecimenPosition();
        pos.setSpecimen(specimenWithPosition);
        pos.setRow(0);
        pos.setCol(0);
        pos.setContainer(container);

        ContainerType type = container.getContainerType();
        RowColPos rcp = new RowColPos(0, 0);
        String positionString = ContainerLabelingScheme.getPositionString(
            rcp, type.getChildLabelingScheme().getId(), type.getCapacity().getRowCapacity(),
            type.getCapacity().getColCapacity(), type.getLabelingLayout());
        pos.setPositionString(positionString);
        session.save(pos);
        session.flush();

        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.sourceSpecimensCreate(
            originInfos, patients, factory.getDefaultStudy().getSourceSpecimens());

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(
            new ArrayList<SpecimenBatchOpInputPojo>(csvInfos),
            childL2Containers,
            false);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_LABEL_POS_OCCUPIED_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void positionsByBarcodeAlreadyOccupied() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createSourceSpecimen();
        Specimen specimenWithPosition = factory.createParentSpecimen();

        Set<Container> childL2Containers = createContainers(factory.getDefaultSourceSpecimenType());
        Container container = childL2Containers.iterator().next();

        SpecimenPosition pos = new SpecimenPosition();
        pos.setSpecimen(specimenWithPosition);
        pos.setRow(0);
        pos.setCol(0);
        pos.setContainer(container);

        ContainerType type = container.getContainerType();
        RowColPos rcp = new RowColPos(0, 0);
        String positionString = ContainerLabelingScheme.getPositionString(
            rcp, type.getChildLabelingScheme().getId(), type.getCapacity().getRowCapacity(),
            type.getCapacity().getColCapacity(), type.getLabelingLayout());
        pos.setPositionString(positionString);
        session.save(pos);
        session.flush();

        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.sourceSpecimensCreate(
            originInfos, patients, factory.getDefaultStudy().getSourceSpecimens());

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(
            new ArrayList<SpecimenBatchOpInputPojo>(csvInfos),
            childL2Containers,
            true);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_CONTAINER_POS_OCCUPIED_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void withExistingPevents() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();
        Map<String, ProcessingEvent> peventMap = new HashMap<String, ProcessingEvent>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
                Specimen parentSpecimen = factory.createParentSpecimen();
                ProcessingEvent pevent = factory.createProcessingEvent();
                parentSpecimen.setProcessingEvent(pevent);
                parentSpecimens.add(parentSpecimen);

                peventMap.put(parentSpecimen.getInventoryId(), pevent);
                log.trace("added processing event: invId={} worsheet={}",
                          parentSpecimen.getInventoryId(), pevent.getWorksheet());
            }
        }

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos =
            specimenCsvHelper.createAliquotedSpecimens(factory.getDefaultStudy(), parentSpecimens);

        // add the worksheet number to the csvInfos
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            csvInfo.setWorksheet(peventMap.get(csvInfo.getParentInventoryId()).getWorksheet());
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void aliquotsWithNoParentSpc() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Set<Patient> patients = new HashSet<Patient>();

        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();
        }

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.aliquotedSpecimensCreate(
            patients, aliquotedSpecimens);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    // This test should fail since a single aliquot cannot have two parent specimens
    @Test
    public void aliquotWithTwoParentSpc() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Set<Patient> patients = new HashSet<Patient>();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();

        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createSourceSpecimen();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        factory.createCollectionEvent();

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> parentSpecimenPojos = specimenCsvHelper.sourceSpecimensCreate(
            originInfos, patients, factory.getDefaultStudy().getSourceSpecimens());

        Assert.assertTrue(parentSpecimenPojos.size() > 2);

        Set<SpecimenBatchOpInputPojo> childSpecimenPojos =
            specimenCsvHelper.aliquotedSpecimensCreate(patients, aliquotedSpecimens);

        Set<SpecimenBatchOpInputPojo> csvInfos = new HashSet<SpecimenBatchOpInputPojo>();
        csvInfos.addAll(parentSpecimenPojos);

        // modify the aliquot to point to the first parent specimen
        List<SpecimenBatchOpInputPojo> parentSpecimenPojosList =
            new ArrayList<SpecimenBatchOpInputPojo>(parentSpecimenPojos);

        SpecimenBatchOpInputPojo childSpecimenPojo = childSpecimenPojos.iterator().next();
        childSpecimenPojo.setParentInventoryId(parentSpecimenPojosList.get(0).getInventoryId());
        csvInfos.addAll(childSpecimenPojos);

        SpecimenBatchOpInputPojo newChildSpecimenPojo = new SpecimenBatchOpInputPojo();
        newChildSpecimenPojo.setInventoryId(childSpecimenPojo.getInventoryId());
        newChildSpecimenPojo.setParentInventoryId(childSpecimenPojo.getParentInventoryId());
        newChildSpecimenPojo.setSpecimenType(childSpecimenPojo.getSpecimenType());
        newChildSpecimenPojo.setCreatedAt(childSpecimenPojo.getCreatedAt());
        newChildSpecimenPojo.setPatientNumber(childSpecimenPojo.getPatientNumber());
        newChildSpecimenPojo.setVisitNumber(childSpecimenPojo.getVisitNumber());
        newChildSpecimenPojo.setSourceSpecimen(false);
        csvInfos.add(newChildSpecimenPojo);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("CSV file contains duplicate inventory IDs, test should fail");
        } catch (IllegalStateException e) {
            CsvUtil.showErrorsInLog(log, e);
        }
    }

    private void checkCsvInfoAgainstDb(Set<SpecimenBatchOpInputPojo> csvInfos) {
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            log.trace("checking specimen against db: inventory id {}", csvInfo.getInventoryId());

            Specimen specimen = (Specimen) session.createCriteria(Specimen.class)
                .add(Restrictions.eq("inventoryId", csvInfo.getInventoryId())).uniqueResult();

            Assert.assertNotNull(specimen);

            Assert.assertEquals(csvInfo.getSpecimenType(), specimen.getSpecimenType().getName());
            Assert.assertEquals(0, DateCompare.compare(csvInfo.getCreatedAt(), specimen.getCreatedAt()));

            if (csvInfo.getPatientNumber() != null) {
                Assert.assertEquals(csvInfo.getPatientNumber(),
                    specimen.getCollectionEvent().getPatient().getPnumber());
            }

            Assert.assertNotNull(specimen.getCollectionEvent());

            if (csvInfo.getVisitNumber() != null) {
                Assert.assertEquals(csvInfo.getVisitNumber(),
                    specimen.getCollectionEvent().getVisitNumber());
            }

            if (specimen.getOriginInfo().getShipmentInfo() != null) {
                Assert.assertEquals(csvInfo.getWaybill(),
                    specimen.getOriginInfo().getShipmentInfo().getWaybill());
            }

            if (csvInfo.getOriginCenter() != null) {
                Assert.assertEquals(csvInfo.getOriginCenter(),
                    specimen.getOriginInfo().getCenter().getNameShort());
            }

            if (csvInfo.getCurrentCenter() != null) {
                Assert.assertEquals(csvInfo.getCurrentCenter(),
                    specimen.getCurrentCenter().getNameShort());
            }

            if (csvInfo.getSourceSpecimen()) {
                Assert.assertNotNull(specimen.getOriginalCollectionEvent());

                if ((csvInfo.getWorksheet() != null) && !csvInfo.getWorksheet().isEmpty()) {
                    Assert.assertNotNull(specimen.getProcessingEvent());
                    Assert.assertEquals(csvInfo.getWorksheet(),
                        specimen.getProcessingEvent().getWorksheet());
                }
            } else {
                Assert.assertNull(specimen.getOriginalCollectionEvent());

                if (csvInfo.getParentInventoryId() != null) {
                    Assert.assertEquals(csvInfo.getParentInventoryId(),
                        specimen.getParentSpecimen().getInventoryId());

                    if (csvInfo.getWorksheet() != null) {
                        Assert.assertNotNull(specimen.getParentSpecimen().getProcessingEvent());

                        Assert.assertEquals(csvInfo.getWorksheet(),
                            specimen.getParentSpecimen().getProcessingEvent().getWorksheet());
                    }
                } else {
                    if (csvInfo.getWorksheet() != null) {
                        Assert.assertEquals(csvInfo.getWorksheet(), specimen
                            .getProcessingEvent().getWorksheet());
                    }
                }
            }

            if ((csvInfo.getPalletPosition() != null) && !csvInfo.getPalletPosition().isEmpty()) {
                SpecimenPosition specimenPosition = specimen.getSpecimenPosition();
                Assert.assertNotNull(specimenPosition);
                Assert.assertEquals(csvInfo.getPalletPosition(), specimenPosition.getPositionString());

                String label = csvInfo.getPalletLabel();
                boolean hasLabel = (label != null) && !label.isEmpty();

                if (hasLabel) {
                    Assert.assertEquals(label, specimenPosition.getContainer().getLabel());
                } else {
                    String barcode = csvInfo.getPalletProductBarcode();
                    Assert.assertEquals(barcode, specimenPosition.getContainer().getProductBarcode());
                }

            }

            if ((csvInfo.getComment() != null) && !csvInfo.getComment().isEmpty()) {
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
        session.save(ctype);

        Set<Container> result = new HashSet<Container>();
        for (int i = 0; i < 3; ++i) {
            Container container = factory.createContainer();
            container.setProductBarcode(nameGenerator.next(Container.class));
            result.add(container);
        }

        return result;
    }

    @Test
    public void specimenBatchOpGetAction() throws Exception {
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
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), originInfos, patients);

        // write out to CSV file so we can view data
        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
            factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
        Integer bachOpId = exec(importAction).getId();

        checkCsvInfoAgainstDb(csvInfos);

        BatchOpGetResult<Specimen> batchOpResult = exec(new SpecimenBatchOpGetAction(bachOpId));

        Assert.assertEquals(csvInfos.size(), batchOpResult.getModelObjects().size());
    }

    /**
     * Parent specimens should not be imported if the specimen type is not in the list of source
     * specimens for a study.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ClassNotFoundException
     */
    @Test
    public void studySourceSpecimens() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Set<Patient> patients = new HashSet<Patient>();
        patients.add(factory.createPatient());
        factory.createSourceSpecimen();
        SpecimenType specimenType = factory.createSpecimenType();

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> csvInfos = specimenCsvHelper.sourceSpecimensCreate(
            originInfos,
            patients,
            factory.getDefaultStudy().getSourceSpecimens());

        // change the specimen type
        for (SpecimenBatchOpInputPojo csvInfo : csvInfos) {
            csvInfo.setSpecimenType(specimenType.getNameShort());
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, csvInfos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), csvInfos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_STUDY_SOURCE_SPC_TYPE_ERROR.format())
                .assertIn(e);
        }
    }

    /**
     * Child specimens should not be imported if the specimen type is not in the list of aliquoted
     * specimens for a study.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void studyAliquotedSpecimens() throws Exception {
        // the parent specimens will not exist in this test
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        patients.add(factory.createPatient());
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        parentSpecimens.add(factory.createParentSpecimen());
        SpecimenType specimenType = factory.createSpecimenType();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> inputPojos = specimenCsvHelper.createAliquotedSpecimens(
            factory.getDefaultStudy(), parentSpecimens);

        // change the specimen type to something that does not exist
        for (SpecimenBatchOpInputPojo inputPojo : inputPojos) {
            inputPojo.setSpecimenType(specimenType.getNameShort());
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, inputPojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), inputPojos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_STUDY_ALIQUOTED_SPC_TYPE_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void originCenterIsInvalid() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        patients.add(factory.createPatient());
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        parentSpecimens.add(factory.createParentSpecimen());
        factory.createSpecimenType();
        factory.createAliquotedSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> inputPojos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), originInfos, patients);

        // change the origin center to something invalid
        for (SpecimenBatchOpInputPojo inputPojo : inputPojos) {
            inputPojo.setOriginCenter(nameGenerator.next(Center.class));
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, inputPojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), inputPojos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_ORIGIN_CENTER_SHORT_NAME_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void originCenterIsValid() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        patients.add(factory.createPatient());
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        parentSpecimens.add(factory.createParentSpecimen());
        factory.createSpecimenType();
        factory.createAliquotedSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> inputPojos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), originInfos, patients);

        // assign the origin center
        for (SpecimenBatchOpInputPojo inputPojo : inputPojos) {
            inputPojo.setOriginCenter(defaultClinic.getNameShort());
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, inputPojos);

        SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
            factory.getDefaultSite(), inputPojos, new File(CSV_NAME));

        try {
            Integer bachOpId = exec(importAction).getId();
            checkCsvInfoAgainstDb(inputPojos);
            BatchOpGetResult<Specimen> batchOpResult = exec(new SpecimenBatchOpGetAction(bachOpId));
            Assert.assertEquals(inputPojos.size(), batchOpResult.getModelObjects().size());
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
        }

    }

    @Test
    public void currentCenterIsInvalid() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        patients.add(factory.createPatient());
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        parentSpecimens.add(factory.createParentSpecimen());
        factory.createSpecimenType();
        factory.createAliquotedSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> inputPojos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), originInfos, patients);

        // change the current center to something invalid
        for (SpecimenBatchOpInputPojo inputPojo : inputPojos) {
            inputPojo.setCurrentCenter(nameGenerator.next(Center.class));
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, inputPojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), inputPojos, new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_CURRENT_CENTER_SHORT_NAME_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void currentCenterIsValid() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        patients.add(factory.createPatient());
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        parentSpecimens.add(factory.createParentSpecimen());
        factory.createSpecimenType();
        factory.createAliquotedSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> inputPojos = specimenCsvHelper.createAllSpecimens(
            factory.getDefaultStudy(), originInfos, patients);

        // assig the origin center
        for (SpecimenBatchOpInputPojo inputPojo : inputPojos) {
            inputPojo.setCurrentCenter(defaultSite.getNameShort());
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, inputPojos);

        SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
            factory.getDefaultSite(), inputPojos, new File(CSV_NAME));
        Integer bachOpId = exec(importAction).getId();

        checkCsvInfoAgainstDb(inputPojos);

        BatchOpGetResult<Specimen> batchOpResult = exec(new SpecimenBatchOpGetAction(bachOpId));

        Assert.assertEquals(inputPojos.size(), batchOpResult.getModelObjects().size());
    }

}
