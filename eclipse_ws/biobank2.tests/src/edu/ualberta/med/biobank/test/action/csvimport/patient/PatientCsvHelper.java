package edu.ualberta.med.biobank.test.action.csvimport.patient;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.batchoperation.patient.PatientBatchOpInputRow;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;

/**
 * 
 * @author Nelson Loyola
 * 
 */
class PatientCsvHelper {
    private final NameGenerator nameGenerator;

    PatientCsvHelper(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    Set<PatientBatchOpInputRow> createPatients(String studyName, int maxPatients) {
        Set<PatientBatchOpInputRow> csvInfos = new HashSet<PatientBatchOpInputRow>();
        for (int i = 0; i < maxPatients; ++i) {
            PatientBatchOpInputRow patientInfo = new PatientBatchOpInputRow();
            patientInfo.setStudyName(studyName);
            patientInfo.setPatientNumber(nameGenerator.next(String.class));
            patientInfo.setCreatedAt(Utils.getRandomDate());
            csvInfos.add(patientInfo);
        }
        return csvInfos;
    }

}
