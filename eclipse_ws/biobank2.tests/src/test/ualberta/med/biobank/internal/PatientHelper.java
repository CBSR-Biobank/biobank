package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class PatientHelper extends DbHelper {

    public static PatientWrapper newPatient(String number) {
        PatientWrapper patient = new PatientWrapper(appService);
        patient.setNumber(number);
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

    public static int addPatients(String number, StudyWrapper study)
        throws Exception {
        int nber = r.nextInt(15) + 1;
        for (int i = 0; i < nber; i++) {
            addPatient(number + i, study);
        }
        study.reload();
        return nber;
    }

}
