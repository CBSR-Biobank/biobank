package edu.ualberta.med.biobank.action.csvimport.patient;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.NameGenerator;

/**
 * 
 * @author loyola
 * 
 */
class PatientCsvHelper {
    private static NameGenerator nameGenerator;

    PatientCsvHelper(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    Set<PatientCsvInfo> createPatients(String studyName, int maxPatients) {
        Set<PatientCsvInfo> csvInfos = new HashSet<PatientCsvInfo>();
        for (int i = 0; i < maxPatients; ++i) {
            PatientCsvInfo patientInfo = new PatientCsvInfo();
            patientInfo.setStudyName(studyName);
            patientInfo.setPatientNumber(nameGenerator.next(String.class));
            patientInfo.setCreatedAt(new Date());
            csvInfos.add(patientInfo);
        }
        return csvInfos;
    }

}
