package test.ualberta.med.biobank.internal;

import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
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

    public static PatientVisitWrapper newPatientVisit(PatientWrapper patient,
        ClinicWrapper clinic, Date dateDrawn, Date dateProcessed,
        Date dateReceived) {
        PatientVisitWrapper pv = new PatientVisitWrapper(appService);
        pv.setPatient(patient);
        pv.setClinic(clinic);
        pv.setDateDrawn(dateDrawn);
        pv.setDateProcessed(dateProcessed);
        pv.setDateReceived(dateReceived);
        return pv;
    }

    public static PatientVisitWrapper addPatientVisit(PatientWrapper patient,
        ClinicWrapper clinic, Date dateDrawn, Date dateProcessed,
        Date dateReceived) throws Exception {
        PatientVisitWrapper pv = newPatientVisit(patient, clinic, dateDrawn,
            dateProcessed, dateReceived);
        pv.persist();
        return pv;
    }
}
