package edu.ualberta.med.biobank.action.csvimport.patient;

import java.io.IOException;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.action.ActionTest;
import edu.ualberta.med.biobank.action.csvimport.CsvUtil;
import edu.ualberta.med.biobank.action.exception.CsvImportException;
import edu.ualberta.med.biobank.model.study.Patient;

/**
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public class TestPatientCsvImport extends ActionTest {

    private static Logger log = LoggerFactory
        .getLogger(TestPatientCsvImport.class.getName());

    private static final String CSV_NAME = "import_patients.csv";

    private PatientCsvHelper patientCsvHelper;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        patientCsvHelper = new PatientCsvHelper(factory.getNameGenerator());
    }

    @Test
    public void noErrors() throws IOException {
        Transaction tx = session.beginTransaction();
        factory.createStudy();
        tx.commit();

        Set<PatientCsvInfo> csvInfos = patientCsvHelper.createPatients(
            factory.getDefaultStudy().getName(), 100);
        PatientCsvWriter.write(CSV_NAME, csvInfos);

        try {
            PatientCsvImportAction importAction =
                new PatientCsvImportAction(CSV_NAME);
            exec(importAction);
        } catch (CsvImportException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void badStudyName() throws IOException {
        Set<PatientCsvInfo> patientInfos = patientCsvHelper.createPatients(
            "badStudyName", 100);
        PatientCsvWriter.write(CSV_NAME, patientInfos);

        // try with a study that does not exist
        try {
            PatientCsvImportAction importAction =
                new PatientCsvImportAction(CSV_NAME);
            exec(importAction);
            Assert
                .fail("should not be allowed to import with a study name that does not exist");
        } catch (CsvImportException e) {
            Assert.assertTrue(true);
        }
    }

    private void checkCsvInfoAgainstDb(Set<PatientCsvInfo> csvInfos) {
        for (PatientCsvInfo csvInfo : csvInfos) {
            Criteria c = session.createCriteria(Patient.class, "p")
                .add(Restrictions.eq("pnumber",
                    csvInfo.getPatientNumber()));
            Patient patient = (Patient) c.uniqueResult();

            Assert.assertEquals(csvInfo.getStudyName(),
                patient.getStudy().getName());
            Assert.assertEquals(csvInfo.getCreatedAt(),
                patient.getTimeCreated());
        }
    }
}
