package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpGetResult;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpGetAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.test.Factory;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

/**
 *
 * @author Nelson Loyola
 *
 */
public class TestSpecimenBatchOp extends CommonSpecimenBatachOpTests<SpecimenBatchOpInputPojo> {

    private static Logger log = LoggerFactory.getLogger(TestSpecimenBatchOp.class);

    private static final String CSV_NAME = "import_specimens.csv";

    private SpecimenBatchOpPojoHelper specimenCsvHelper;

    private final Set<OriginInfo> originInfos = new HashSet<OriginInfo>();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        specimenCsvHelper = new SpecimenBatchOpPojoHelper(nameGenerator);

        // delete the CSV file if it exists
        File file = new File(CSV_NAME);
        file.delete();

        // add 2 shipments
        //
        // source center on origin info will be the clinic created above
        session.beginTransaction();
        factory.createShipmentInfo();
        originInfos.add(factory.createOriginInfo());
        factory.createShipmentInfo();
        originInfos.add(factory.createOriginInfo());
        session.getTransaction().commit();
    }

    @Test
    public void noErrorsNoContainers() throws Exception {
        session.beginTransaction();
        Set<Patient> patients = new HashSet<Patient>();
        patients.add(factory.createPatient());
        factory.createCollectionEvent();

        Set<SourceSpecimen> sourceSpecimens = new HashSet<SourceSpecimen>();
        sourceSpecimens.add(factory.createSourceSpecimen());

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAllSpecimens(session, originInfos, patients);

        // write out to CSV file so we can view data
        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    // test with large number of patients
    @Ignore
    @Test
    public void manyPatients() throws Exception {
        final int numPatients = 500;

        session.beginTransaction();
        Set<Patient> patients = new HashSet<Patient>();

        for (int i = 0; i < numPatients; i++) {
            patients.add(factory.createPatient());
            factory.createCollectionEvent();
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
        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAllSpecimens(session, testOriginInfos, patients);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void onlyParentSpecimensInCsv() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        factory.createSourceSpecimen();
        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

        // remove the worksheet # for the last half
        int half = pojos.size() / 2;
        int count = 0;
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            if (count > half) {
                pojo.setWorksheet(null);
            }
            ++count;
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void aliquotsWithParentSpecimens() throws Exception {
        final SimpleTestFixture fixture = new SimpleTestFixture(session, factory);
        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAllSpecimens(session,
                                                 originInfos,
                                                 fixture.patients);
        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void aliquotsWithParentSpecimensInDb() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        for (int i = 0; i < 2; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();
            factory.createParentSpecimen();
        }
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos = specimenCsvHelper.createAliquotedSpecimens(patients);
        assertTrue("no pojos generated", pojos.size() > 0);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void failsForSourceSpecimensWithParentInventoryId() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        Specimen parentSpecimen = factory.createParentSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

        SpecimenBatchOpInputPojo pojo = pojos.iterator().next();
        pojo.setInventoryId(parentSpecimen.getInventoryId());

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to create aliquot specimens with no parent specimens");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(SPC_ALREADY_EXISTS_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void failsForSpecimensThatExistInDb() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        for (int i = 0; i < 2; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();
        }
        factory.createSourceSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setParentInventoryId(nameGenerator.next(Specimen.class));
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to create aliquot specimens with no parent specimens");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_PARENT_SPC_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void multiplePatientsSameWorksheet() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        for (int i = 0; i < 2; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();
        }

        factory.createSourceSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

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
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void withVolume() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        for (int i = 0; i < 2; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);

            // create source specimens and parent specimens
            for (int j = 0; j < 2; j++) {
                factory.createSourceSpecimen();
                factory.createParentSpecimen();
            }
        }
        factory.createAliquotedSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos = specimenCsvHelper.createAliquotedSpecimens(patients);
        assertTrue("no pojos generated", pojos.size() > 0);

        // set the volume
        Random r = new Random();
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            assertFalse(pojo.getSourceSpecimen());
            pojo.setVolume(new BigDecimal(r.nextInt(10)));
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void existingWorksheet() throws Exception {
        session.beginTransaction();
        factory.createSourceSpecimen();
        factory.createPatient();
        factory.createCollectionEvent();
        ProcessingEvent pevent = factory.createProcessingEvent();
        factory.createParentSpecimen();

        Set<Patient> patients = new HashSet<Patient>();
        patients.add(factory.createPatient());
        factory.createCollectionEvent();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

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
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void onlyChildSpecimensNoPevents() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();
            factory.createParentSpecimen();
        }

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos = specimenCsvHelper.createAliquotedSpecimens(patients);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
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

        session.beginTransaction();
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

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAliquotedSpecimens(patients);

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
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void onlyChildSpecimensNoParents() throws Exception {
        // the parent specimens will not exist in this test
        Set<Patient> patients = new HashSet<Patient>();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();

        session.beginTransaction();
        patients.add(factory.createPatient());
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        parentSpecimens.add(factory.createParentSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAliquotedSpecimens(patients);

        // change the parent's inventory id to something that does not exist
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setParentInventoryId(pojo.getParentInventoryId() + "_1");
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to create aliquot specimens with no parent specimens");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_PARENT_SPC_INV_ID_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void onlyChildSpecimensNoCollectionEvent() throws Exception {
        session.beginTransaction();
        factory.createSpecimenType();
        AliquotedSpecimen aliquotedSpecimen = factory.createAliquotedSpecimen();

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos = new HashSet<SpecimenBatchOpInputPojo>();
        pojos.add(specimenCsvHelper.genericSpecimenCreate(
            null, aliquotedSpecimen.getSpecimenType().getName()));
        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to create aliquot specimens with no collection events");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_ALIQ_SPC_PATIENT_CEVENT_MISSING_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void invalidPatientNumber() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        session.beginTransaction();
        factory.createSourceSpecimen();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        session.getTransaction().commit();

        for (String testCase : Arrays.asList("empty", "invalid")) {
            // make sure you can add parent specimens without a worksheet #
            Set<SpecimenBatchOpInputPojo> pojos =
                specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

            if (testCase.equals("empty")) {
                for (SpecimenBatchOpInputPojo pojo : pojos) {
                    if (pojo.getPatientNumber() != null) {
                        pojo.setPatientNumber(StringUtil.EMPTY_STRING);
                    }
                }
            } else {
                for (SpecimenBatchOpInputPojo pojo : pojos) {
                    if (pojo.getPatientNumber() != null) {
                        pojo.setPatientNumber(pojo.getPatientNumber() + "_2");
                    }
                }
            }

            SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

            try {
                SpecimenBatchOpAction importAction =
                    new SpecimenBatchOpAction(factory.getDefaultSite(), pojos, new File(CSV_NAME));
                exec(importAction);
                fail("should not be allowed to create aliquot specimens with invalid patients");
            } catch (BatchOpErrorsException e) {
                new AssertBatchOpException()
                    .withMessage(CSV_PATIENT_NUMBER_INVALID_ERROR.format())
                    .assertIn(e);
            }
        }
    }

    @Test
    public void wrongPatientNumber() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        session.beginTransaction();
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        Patient patient = factory.createPatient();
        patients.add(patient);
        CollectionEvent event = factory.createCollectionEvent();
        event.setVisitNumber(3);
        factory.createParentSpecimen();
        Patient wrongPatient = factory.createPatient();
        CollectionEvent wrongEvent = factory.createCollectionEvent();
        wrongEvent.setVisitNumber(10);
        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAliquotedSpecimens(patients);

        // change the patient on the aliquoted number to something invalid
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            if (pojo.getParentInventoryId() != null) {
                pojo.setPatientNumber(wrongPatient.getPnumber());
                pojo.setVisitNumber(wrongEvent.getVisitNumber());
            }
        }
        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to create aliquot specimens with invalid patients");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException().withMessage(CSV_PATIENT_MATCH_ERROR.format()).assertIn(e);
        }
    }
    @Test
    public void invalidVisitNumber() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        Patient patient = factory.createPatient();
        patients.add(patient);
        CollectionEvent event = factory.createCollectionEvent();
        factory.createParentSpecimen();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAliquotedSpecimens(patients);

        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setParentInventoryId(null);
            pojo.setPatientNumber(patient.getPnumber());
            pojo.setVisitNumber(event.getVisitNumber() + 10);
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to create aliquot specimens with an invalid visit number");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException().withMessage(CSV_CEVENT_ERROR.format()).assertIn(e);
        }
    }


    @Test
    public void missingPatientNumber() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        session.beginTransaction();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();

        Set<SourceSpecimen> sourceSpecimens = new HashSet<SourceSpecimen>();
        sourceSpecimens.add(factory.createSourceSpecimen());

        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAllSpecimens(session, originInfos, patients);

        // set all patient numbers to null
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setPatientNumber("");
        }
        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to import spcecimens when patient number is missing");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
        }
    }

    @Test
    public void invalidSourceSpecimenType() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        session.beginTransaction();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        factory.createSourceSpecimen();

        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

        // change the specimen type to something invalid
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            String stype = pojo.getSpecimenType();
            if (stype != null) {
                pojo.setSpecimenType(stype + "_x");
            }
        }
        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to invalid specimen types");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_SPECIMEN_TYPE_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void invalidAliquotedSpecimenType() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        session.beginTransaction();
        Patient patient = factory.createPatient();
        factory.createCollectionEvent();
        factory.createParentSpecimen();
        patients.add(patient);
        factory.createSourceSpecimen();

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAllSpecimens(session, originInfos, patients);

        // change the specimen type to something invalid
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            String stype = pojo.getSpecimenType();
            if ((stype != null) && !pojo.getSourceSpecimen()) {
                pojo.setSpecimenType(stype + "_x");
            }
        }
        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to invalid specimen types");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_SPECIMEN_TYPE_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void withComments() throws Exception {
        final Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        for (int i = 0; i < 3; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 3; j++) {
                factory.createSourceSpecimen();
                Specimen parentSpecimen = factory.createParentSpecimen();
                parentSpecimen.setProcessingEvent(factory.createProcessingEvent());
            }
        }

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        factory.createAliquotedSpecimen();
        factory.createAliquotedSpecimen();
        session.getTransaction().commit();

        withComments(new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createAliquotedSpecimens(patients);
            }
        });
    }

    @Test
    public void noErrorsWithContainers() throws Exception {
        final SimpleTestFixture fixture = new SimpleTestFixture(session, factory);
        noErrorsWithContainers(new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createAllSpecimens(session,
                                                            originInfos,
                                                            fixture.patients);
            }
        });
    }

    @Test
    public void onlyParentSpecimensWithLabelsAndPositions() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        for (int i = 0; i < 2; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();
            factory.createSourceSpecimen();
        }
        session.getTransaction().commit();

        Set<Container> childL2Containers =
            SpecimenBatchOpPojoHelper.createContainers(session,
                                                       factory,
                                                       nameGenerator,
                                                       factory.getDefaultSourceSpecimenType(),
                                                       5);

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

        SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos,
                                                         childL2Containers,
                                                         false);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void withInvalidWaybill() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        factory.createSourceSpecimen();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        session.getTransaction().commit();

        // make sure you can add parent specimens without a worksheet #
        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setWaybill(pojo.getWaybill() + nameGenerator.next(String.class));
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should not be allowed to create aliquot specimens with no parent specimens");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_WAYBILL_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void withExistingPevents() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Set<Patient> patients = new HashSet<Patient>();
        session.beginTransaction();
        Set<Specimen> parentSpecimens = new HashSet<Specimen>();
        Map<String, ProcessingEvent> peventMap = new HashMap<String, ProcessingEvent>();

        for (int i = 0; i < 2; i++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();

            // create 3 source specimens and parent specimens
            for (int j = 0; j < 2; j++) {
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

        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAliquotedSpecimens(patients);

        // add the worksheet number to the pojos
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setWorksheet(peventMap.get(pojo.getParentInventoryId()).getWorksheet());
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    @Test
    public void aliquotsWithNoParentSpc() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
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

        Set<SpecimenBatchOpInputPojo> pojos = specimenCsvHelper.aliquotedSpecimensCreate(
            patients, aliquotedSpecimens);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    // This test should fail since a single aliquot cannot have two parent specimens
    @Test
    public void aliquotWithTwoParentSpc() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        Set<Patient> patients = new HashSet<Patient>();
        Set<AliquotedSpecimen> aliquotedSpecimens = new HashSet<AliquotedSpecimen>();

        session.beginTransaction();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        factory.createSourceSpecimen();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        factory.createCollectionEvent();

        // create a new specimen type for the aliquoted specimens
        factory.createSpecimenType();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> parentSpecimenPojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

        assertTrue(parentSpecimenPojos.size() > 2);

        Set<SpecimenBatchOpInputPojo> childSpecimenPojos =
            specimenCsvHelper.aliquotedSpecimensCreate(patients, aliquotedSpecimens);

        Set<SpecimenBatchOpInputPojo> pojos = new HashSet<SpecimenBatchOpInputPojo>();
        pojos.addAll(parentSpecimenPojos);

        // modify the aliquot to point to the first parent specimen
        List<SpecimenBatchOpInputPojo> parentSpecimenPojosList =
            new ArrayList<SpecimenBatchOpInputPojo>(parentSpecimenPojos);

        SpecimenBatchOpInputPojo childSpecimenPojo = childSpecimenPojos.iterator().next();
        childSpecimenPojo.setParentInventoryId(parentSpecimenPojosList.get(0).getInventoryId());
        pojos.addAll(childSpecimenPojos);

        SpecimenBatchOpInputPojo newChildSpecimenPojo = new SpecimenBatchOpInputPojo();
        newChildSpecimenPojo.setInventoryId(childSpecimenPojo.getInventoryId());
        newChildSpecimenPojo.setParentInventoryId(childSpecimenPojo.getParentInventoryId());
        newChildSpecimenPojo.setSpecimenType(childSpecimenPojo.getSpecimenType());
        newChildSpecimenPojo.setCreatedAt(childSpecimenPojo.getCreatedAt());
        newChildSpecimenPojo.setPatientNumber(childSpecimenPojo.getPatientNumber());
        newChildSpecimenPojo.setVisitNumber(childSpecimenPojo.getVisitNumber());
        newChildSpecimenPojo.setSourceSpecimen(false);
        pojos.add(newChildSpecimenPojo);

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("CSV file contains duplicate inventory IDs, test should fail");
        } catch (IllegalStateException e) {
            CsvUtil.showErrorsInLog(log, e);
        }
    }

    @Test
    public void withProductBarcodesAndPositions() throws Exception {
        final Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        for (int i = 0; i < 3; i++) {
            patients.add(factory.createPatient());
            factory.createCollectionEvent();
            factory.createSourceSpecimen();
        }
        session.getTransaction().commit();

        withProductBarcodesAndPositions(new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);
            }
        });
    }

    @Test
    public void withInvalidContainerInformation() throws Exception {
        final Set<Patient> patients = new HashSet<Patient>();
        session.beginTransaction();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        factory.createSourceSpecimen();
        session.getTransaction().commit();

        IPojosCreator<SpecimenBatchOpInputPojo> pojosCreator =
            new IPojosCreator<SpecimenBatchOpInputPojo>() {
                @Override
                public Set<SpecimenBatchOpInputPojo> create() {
                    return specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);
                }
            };
        withInvalidContainerInformation(pojosCreator, false);
    }

    @Test
    public void withInvalidSpecimenPositions() throws Exception {
        final Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        factory.createSourceSpecimen();
        session.getTransaction().commit();

        IPojosCreator<SpecimenBatchOpInputPojo> pojosCreator =
            new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);
            }
        };
        Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>();
        specimenTypes.add(factory.getDefaultSourceSpecimenType());
        withInvalidSpecimenPositions(pojosCreator, specimenTypes);
    }

    @Test
    public void withInvalidSpecimenTypes() throws Exception {
        final Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        factory.createSourceSpecimen();
        session.getTransaction().commit();

        withInvalidSpecimenTypes(new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);
            }
        });
    }

    @Test
    public void withInvalidContainerType() throws Exception {
        final Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createCollectionEvent();
        factory.createSourceSpecimen();
        session.getTransaction().commit();

        Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>();
        specimenTypes.add(factory.getDefaultSourceSpecimenType());

        IPojosCreator<SpecimenBatchOpInputPojo> pojosCreator =
            new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);
            }
        };
        withInvalidContainerType(pojosCreator, specimenTypes);
    }

    @Test
    public void positionsAlreadyOccupied() throws Exception {
        final Set<Patient> patients = new HashSet<Patient>();

        session.beginTransaction();
        Patient patient = factory.createPatient();
        patients.add(patient);
        factory.createSourceSpecimen();
        session.getTransaction().commit();

        Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>();
        specimenTypes.add(factory.getDefaultSourceSpecimenType());

        IPojosCreator<SpecimenBatchOpInputPojo> pojosCreator =
            new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);
            }
        };
        withPositionsAlreadyOccupied(pojosCreator,
                                     specimenTypes,
                                     patients.iterator().next());
    }

    @Test(expected = IllegalStateException.class)
    public void errorsIfInventoryIdFoundMoreThanOnce() throws Exception {
        final SimpleTestFixture fixture = new SimpleTestFixture(session, factory);
        IPojosCreator<SpecimenBatchOpInputPojo> pojosCreator =
            new IPojosCreator<SpecimenBatchOpInputPojo>() {
                @Override
                public Set<SpecimenBatchOpInputPojo> create() {
                    return specimenCsvHelper.sourceSpecimensCreate(session,
                                                                   originInfos,
                                                                   fixture.patients);
                }
            };

        errorsIfInventoryIdFoundMoreThanOnce(pojosCreator);
    }

    @Test
    public void specimenBatchOpGetAction() throws Exception {
        final SimpleTestFixture fixture = new SimpleTestFixture(session, factory);
        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAllSpecimens(session, originInfos, fixture.patients);

        // write out to CSV file so we can view data
        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
            factory.getDefaultSite(), pojos, new File(CSV_NAME));
        Integer bachOpId = exec(importAction).getId();

        checkAgainstDb(pojos);

        BatchOpGetResult<Specimen> batchOpResult = exec(new SpecimenBatchOpGetAction(bachOpId));

        assertEquals(pojos.size(), batchOpResult.getModelObjects().size());
        assertEquals(getGlobalAdmin().getLogin(), batchOpResult.getExecutedBy());

        Date timeNow = new Date();
        assertTrue("Dates aren't close enough to each other!",
                          (timeNow.getTime() - batchOpResult.getTimeExecuted().getTime()) < 10000);
        assertEquals(CSV_NAME, batchOpResult.getInput().getName());
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
        session.beginTransaction();
        patients.add(factory.createPatient());
        factory.createCollectionEvent();
        factory.createSourceSpecimen();
        SpecimenType specimenType = factory.createSpecimenType();
        session.getTransaction().commit();

        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.sourceSpecimensCreate(session, originInfos, patients);

        // change the specimen type
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setSpecimenType(specimenType.getNameShort());
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should fail");
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
        final SimpleTestFixture fixture = new SimpleTestFixture(session, factory);
        Set<SpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createAliquotedSpecimens(fixture.patients);

        session.beginTransaction();
        SpecimenType specimenType = factory.createSpecimenType();
        session.getTransaction().commit();

        // change the specimen type to another that is not in the study
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setSpecimenType(specimenType.getNameShort());
        }

        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            SpecimenBatchOpAction importAction = new SpecimenBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
            fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_STUDY_ALIQUOTED_SPC_TYPE_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void originCenterIsValid() throws Exception {
        final SimpleTestFixture fixture = new SimpleTestFixture(session, factory);
        originCenterIsValid(new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createAllSpecimens(session, originInfos, fixture.patients);
            }
        });
    }

    @Test
    public void originCenterIsInvalid() throws Exception {
        final SimpleTestFixture fixture = new SimpleTestFixture(session, factory);
        originCenterIsInvalid(new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createAllSpecimens(session, originInfos, fixture.patients);
            }
        });
    }

    @Test
    public void currentCenterIsValid() throws Exception {
        final SimpleTestFixture fixture = new SimpleTestFixture(session, factory);
        currentCenterIsValid(new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createAllSpecimens(session, originInfos, fixture.patients);
            }
        });
    }

    @Test
    public void currentCenterIsInvalid() throws Exception {
        final SimpleTestFixture fixture = new SimpleTestFixture(session, factory);
        currentCenterIsInvalid(new IPojosCreator<SpecimenBatchOpInputPojo>() {
            @Override
            public Set<SpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createAllSpecimens(session, originInfos, fixture.patients);
            }
        });
    }

    @Override
    protected void checkAgainstDb(SpecimenBatchOpInputPojo pojo, Specimen specimen) {
        super.checkAgainstDb(pojo, specimen);

        assertNotNull(specimen);
        assertEquals(pojo.getSpecimenType(), specimen.getSpecimenType().getName());
        assertEquals(0, DateCompare.compare(pojo.getCreatedAt(), specimen.getCreatedAt()));
        assertNotNull(specimen.getCollectionEvent());

        if (pojo.getVisitNumber() != null) {
            assertEquals(pojo.getVisitNumber(),
                                specimen.getCollectionEvent().getVisitNumber());
        }

        if (specimen.getOriginInfo().getShipmentInfo() != null) {
            assertEquals(pojo.getWaybill(),
                                specimen.getOriginInfo().getShipmentInfo().getWaybill());
        }

        if (pojo.getOriginCenter() != null) {
            assertEquals(pojo.getOriginCenter(),
                                specimen.getOriginInfo().getCenter().getNameShort());
        }

        if (pojo.getCurrentCenter() != null) {
            assertEquals(pojo.getCurrentCenter(),
                                specimen.getCurrentCenter().getNameShort());
        }

        if (pojo.getSourceSpecimen()) {
            assertNotNull(specimen.getOriginalCollectionEvent());

            if ((pojo.getWorksheet() != null) && !pojo.getWorksheet().isEmpty()) {
                assertNotNull(specimen.getProcessingEvent());
                assertEquals(pojo.getWorksheet(),
                                    specimen.getProcessingEvent().getWorksheet());
            }
        } else {
            assertNull(specimen.getOriginalCollectionEvent());

            if (pojo.getParentInventoryId() != null) {
                if (pojo.getWorksheet() != null) {
                    assertNotNull(specimen.getParentSpecimen().getProcessingEvent());

                    assertEquals(pojo.getWorksheet(),
                                 specimen.getParentSpecimen().getProcessingEvent().getWorksheet());
                }
            } else {
                if (pojo.getWorksheet() != null) {
                    assertEquals(pojo.getWorksheet(),
                                 specimen.getProcessingEvent().getWorksheet());
                }
            }

            if (pojo.getVolume() != null) {
                assertTrue(specimen.getQuantity().compareTo(pojo.getVolume()) == 0);
            }
        }
    }

    @Override
    protected File writePojosToCsv(Set<SpecimenBatchOpInputPojo> pojos) throws IOException {
        SpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);
        return new File(CSV_NAME);
    }

    @Override
    protected Action<IdResult> createAction(Set<SpecimenBatchOpInputPojo> pojos, File file)
        throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        return new SpecimenBatchOpAction(factory.getDefaultSite(), pojos, file);
    }

}

final class SimpleTestFixture {

    Set<Patient> patients = new HashSet<Patient>();

    SimpleTestFixture(Session session, Factory factory) {
        session.beginTransaction();
        patients.add(factory.createPatient());
        factory.createSourceSpecimen();
        factory.createAliquotedSpecimen();
        factory.createParentSpecimen();
        factory.createSpecimenType();
        factory.createAliquotedSpecimen();
        session.getTransaction().commit();
    }
}
