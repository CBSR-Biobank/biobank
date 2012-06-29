package edu.ualberta.med.biobank.test.action;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.csvimport.PatientCsvImportAction;
import edu.ualberta.med.biobank.common.action.csvimport.PatientCsvInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.util.csv.PatientCsvWriter;

public class TestPatientCsvImport extends ActionTest {

    @SuppressWarnings("nls")
    @Test
    public void testPatientCsvImport() throws IOException {
        Transaction tx = session.beginTransaction();
        Study study = factory.createStudy();
        tx.commit();

        patientsCreateAndImportCsv(study.getNameShort());

        // try with a study that does not exist
        try {
            patientsCreateAndImportCsv("badstudyname");
            Assert
                .fail("should not be allowed to import with a study name that does not exist");
        } catch (ActionException e) {
            Assert.assertTrue(true);
        }
    }

    @SuppressWarnings("nls")
    private void patientsCreateAndImportCsv(String studyName)
        throws IOException {
        final String CSV_NAME = "import_patients.csv";

        Set<PatientCsvInfo> patientInfos = new HashSet<PatientCsvInfo>();
        for (int i = 0, n = 10 + getR().nextInt(30); i < n; ++i) {
            PatientCsvInfo patientInfo = new PatientCsvInfo();
            patientInfo.setStudyName(studyName);
            patientInfo.setPatientNumber(Utils.getRandomString(10, 15));
            patientInfo.setCreatedAt(Utils.getRandomDate());
            patientInfos.add(patientInfo);
        }

        PatientCsvWriter.write(CSV_NAME, patientInfos);
        PatientCsvImportAction importAction =
            new PatientCsvImportAction(CSV_NAME);
        exec(importAction);
    }

}
