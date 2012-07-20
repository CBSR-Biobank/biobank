package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.csvimport.SpecimenCsvImportAction;
import edu.ualberta.med.biobank.common.action.csvimport.SpecimenCsvInfo;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException.ImportError;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.action.csvhelper.SpecimenCsvHelper;
import edu.ualberta.med.biobank.test.util.csv.SpecimenCsvWriter;

@SuppressWarnings("nls")
public class TestSpecimenCsvImport extends ActionTest {

    private static Logger log = LoggerFactory
        .getLogger(TestSpecimenCsvImport.class.getName());

    private static final String CSV_NAME = "import_specimens.csv";

    private Transaction tx;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        tx = session.beginTransaction();
        factory.createSite();
        factory.createClinic();
        factory.createStudy();
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

        Set<SpecimenCsvInfo> csvInfos = new HashSet<SpecimenCsvInfo>();

        try {
            csvInfos = SpecimenCsvHelper.createAllSpecimens(
                factory.getDefaultStudy(), factory.getDefaultClinic(),
                factory.getDefaultSite(), patients);
            SpecimenCsvWriter.write(CSV_NAME, csvInfos);

            SpecimenCsvImportAction importAction =
                new SpecimenCsvImportAction(CSV_NAME);
            exec(importAction);
        } catch (CsvImportException e) {
            showErrorsInLog(e);
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

        Set<SpecimenCsvInfo> csvInfos = new HashSet<SpecimenCsvInfo>();

        try {
            // make sure you can add parent specimens without a worksheet #
            csvInfos = SpecimenCsvHelper.sourceSpecimensCreate(factory
                .getDefaultClinic(), factory.getDefaultSite(), patients,
                factory.getDefaultStudy().getSourceSpecimens());

            // remove the worksheet # for the last half
            int half = csvInfos.size() / 2;
            int count = 0;
            for (SpecimenCsvInfo csvInfo : csvInfos) {
                if (count > half) {
                    csvInfo.setWorksheet(null);
                }
                ++count;
            }

            SpecimenCsvWriter.write(CSV_NAME, csvInfos);

            SpecimenCsvImportAction importAction =
                new SpecimenCsvImportAction(CSV_NAME);
            exec(importAction);
        } catch (CsvImportException e) {
            showErrorsInLog(e);
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

        Set<SpecimenCsvInfo> csvInfos = new HashSet<SpecimenCsvInfo>();

        try {
            csvInfos = SpecimenCsvHelper.createAliquotedSpecimens(
                factory.getDefaultStudy(), factory.getDefaultClinic(),
                factory.getDefaultSite(), parentSpecimens);
            SpecimenCsvWriter.write(CSV_NAME, csvInfos);

            SpecimenCsvImportAction importAction =
                new SpecimenCsvImportAction(CSV_NAME);
            exec(importAction);
        } catch (CsvImportException e) {
            showErrorsInLog(e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void missingPatient() {
        Set<Patient> patients = new HashSet<Patient>();
        patients.add(factory.createPatient());
        patients.add(factory.createPatient());
        patients.add(factory.createPatient());

        Set<SourceSpecimen> sourceSpecimens = new HashSet<SourceSpecimen>();
        sourceSpecimens.add(factory.createSourceSpecimen());
        sourceSpecimens.add(factory.createSourceSpecimen());
        sourceSpecimens.add(factory.createSourceSpecimen());

        Set<AliquotedSpecimen> aliquotedSpecimens =
            new HashSet<AliquotedSpecimen>();
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        aliquotedSpecimens.add(factory.createAliquotedSpecimen());

        tx.commit();

        try {
            Set<SpecimenCsvInfo> csvInfo =
                SpecimenCsvHelper.createAllSpecimens(
                    factory.getDefaultStudy(), factory.getDefaultClinic(),
                    factory.getDefaultSite(), patients);
            SpecimenCsvWriter.write(CSV_NAME, csvInfo);

            SpecimenCsvImportAction importAction =
                new SpecimenCsvImportAction(CSV_NAME);
            exec(importAction);
        } catch (CsvImportException e) {
            Assert.fail("errors in CVS data");
            showErrorsInLog(e);
        } catch (Exception e) {
            Assert.fail("could not import data");
        }
    }

    private void checkCsvInfoAgainstDb(Set<SpecimenCsvInfo> csvInfos) {
        for (SpecimenCsvInfo csvInfo : csvInfos) {
            Criteria c = session.createCriteria(Specimen.class, "p")
                .add(Restrictions.eq("inventoryId", csvInfo.getInventoryId()));

            Specimen specimen = (Specimen) c.uniqueResult();
            Assert.assertEquals(csvInfo.getSpecimenType(),
                specimen.getSpecimenType().getName());
            Assert.assertEquals(0, DateCompare.compare(csvInfo.getCreatedAt(),
                specimen.getCreatedAt()));
            Assert.assertEquals(csvInfo.getPatientNumber(), specimen
                .getCollectionEvent().getPatient().getPnumber());

            Assert.assertNotNull(specimen.getCollectionEvent());
            Assert.assertEquals(csvInfo.getVisitNumber(), specimen
                .getCollectionEvent().getVisitNumber());

            Assert.assertEquals(csvInfo.getCurrentCenter(), specimen
                .getCurrentCenter().getName());
            Assert.assertEquals(csvInfo.getOriginCenter(), specimen
                .getOriginInfo().getCenter().getName());

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
        }
    }

    private void showErrorsInLog(CsvImportException e) {
        for (ImportError ie : e.getErrors()) {
            log.error("ERROR: line no {}: {}", ie.getLineNumber(),
                ie.getMessage());
        }

    }
}
