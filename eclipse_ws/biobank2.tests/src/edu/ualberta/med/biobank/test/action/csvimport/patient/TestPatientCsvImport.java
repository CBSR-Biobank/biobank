package edu.ualberta.med.biobank.test.action.csvimport.patient;

import java.io.IOException;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.csvimport.PatientCsvImportAction;
import edu.ualberta.med.biobank.common.action.csvimport.PatientCsvInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.test.action.ActionTest;
import edu.ualberta.med.biobank.test.util.csv.PatientCsvWriter;

@SuppressWarnings("nls")
public class TestPatientCsvImport extends ActionTest {
    private static final String CSV_NAME = "import_patients.csv";

    private Transaction tx;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        tx = session.beginTransaction();
        factory.createStudy();
        tx.commit();
    }

    @Test
    public void noErrors() throws IOException {
        Set<PatientCsvInfo> csvInfos = PatientCsvHelper.createPatients(
            factory.getDefaultStudy().getNameShort(), 100);
        PatientCsvWriter.write(CSV_NAME, csvInfos);

        PatientCsvImportAction importAction =
            new PatientCsvImportAction(CSV_NAME);
        exec(importAction);

        checkCsvInfoAgainstDb(csvInfos);
    }

    @Test
    public void badStudyName() throws IOException {
        Set<PatientCsvInfo> patientInfos = PatientCsvHelper.createPatients(
            "badStudyName", 100);
        PatientCsvWriter.write(CSV_NAME, patientInfos);

        // try with a study that does not exist
        try {
            PatientCsvImportAction importAction =
                new PatientCsvImportAction(CSV_NAME);
            exec(importAction);
            Assert
                .fail("should not be allowed to import with a study name that does not exist");
        } catch (ActionException e) {
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
                patient.getStudy().getNameShort());
            Assert.assertEquals(csvInfo.getCreatedAt(),
                patient.getCreatedAt());
        }
    }
}
