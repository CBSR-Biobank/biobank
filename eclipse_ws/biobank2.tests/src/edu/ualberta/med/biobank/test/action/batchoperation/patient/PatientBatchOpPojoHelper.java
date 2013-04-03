package edu.ualberta.med.biobank.test.action.batchoperation.patient;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.batchoperation.patient.PatientBatchOpInputPojo;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;

/**
 * 
 * @author Nelson Loyola
 * 
 */
class PatientBatchOpPojoHelper {
    private final NameGenerator nameGenerator;

    PatientBatchOpPojoHelper(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    Set<PatientBatchOpInputPojo> createPatients(String studyName, int maxPatients) {
        Set<PatientBatchOpInputPojo> csvInfos = new HashSet<PatientBatchOpInputPojo>();
        for (int i = 0; i < maxPatients; ++i) {
            PatientBatchOpInputPojo patientInfo = new PatientBatchOpInputPojo();
            patientInfo.setStudyName(studyName);
            patientInfo.setPatientNumber(nameGenerator.next(String.class));
            patientInfo.setEnrollmentDate(Utils.getRandomDate());
            csvInfos.add(patientInfo);
        }
        return csvInfos;
    }

    public void addComments(Set<PatientBatchOpInputPojo> inputPojos) {
        for (PatientBatchOpInputPojo inputPojo : inputPojos) {
            inputPojo.setComment(nameGenerator.next(String.class));
        }
    }

}
