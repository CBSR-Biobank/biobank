package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class PatientHelper extends DbHelper {

    public static PatientWrapper newPatient(String number) {
        PatientWrapper patient = new PatientWrapper(appService);
        patient.setNumber(number);
        return patient;
    }

    public static PatientWrapper addPatient(String number, StudyWrapper study)
        throws Exception {
        PatientWrapper patient = newPatient(number);
        patient.setStudy(study);
        patient.persist();
        return patient;
    }

}
