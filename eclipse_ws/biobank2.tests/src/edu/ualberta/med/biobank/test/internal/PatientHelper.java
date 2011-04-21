package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class PatientHelper extends DbHelper {

    public static PatientWrapper newPatient(String number) {
        PatientWrapper patient = new PatientWrapper(appService);
        patient.setPnumber(number);
        return patient;
    }

    public static PatientWrapper newPatient(String number, StudyWrapper study)
        throws Exception {
        PatientWrapper patient = newPatient(number);
        patient.setStudy(study);
        return patient;
    }

    public static PatientWrapper addPatient(String number, StudyWrapper study)
        throws Exception {
        PatientWrapper patient = newPatient(number, study);
        patient.persist();
        study.reload();
        return patient;
    }

    public static List<PatientWrapper> addRandPatients(String number,
        StudyWrapper study) throws Exception {
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();
        int nber = r.nextInt(15) + 1;
        for (int i = 0; i < nber; i++) {
            patients.add(addPatient(number + i, study));
        }
        study.reload();
        return patients;
    }

    public static int addPatients(String number, StudyWrapper study)
        throws Exception {
        return addRandPatients(number, study).size();
    }
}
