package edu.ualberta.med.biobank.test.action.csvimport.patient;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.csvimport.patient.PatientCsvInfo;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.csvimport.specimen.SpecimenCsvHelper;

/**
 * 
 * @author loyola
 * 
 */
public class PatientCsvHelper {
    private static final NameGenerator nameGenerator = new NameGenerator(
        SpecimenCsvHelper.class.getSimpleName() + new Random());

    static Set<PatientCsvInfo> createPatients(String studyName, int maxPatients) {
        Set<PatientCsvInfo> csvInfos = new HashSet<PatientCsvInfo>();
        for (int i = 0; i < maxPatients; ++i) {
            PatientCsvInfo patientInfo = new PatientCsvInfo();
            patientInfo.setStudyName(studyName);
            patientInfo.setPatientNumber(nameGenerator.next(String.class));
            patientInfo.setCreatedAt(Utils.getRandomDate());
            csvInfos.add(patientInfo);
        }
        return csvInfos;
    }

}
