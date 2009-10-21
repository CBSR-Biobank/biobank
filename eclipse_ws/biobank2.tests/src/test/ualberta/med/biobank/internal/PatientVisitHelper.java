package test.ualberta.med.biobank.internal;

import java.text.ParseException;
import java.util.Date;

import test.ualberta.med.biobank.Utils;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;

public class PatientVisitHelper extends DbHelper {

    public static PatientVisitWrapper newPatientVisit(PatientWrapper patient,
        ClinicWrapper clinic, Date dateDrawn, Date dateProcessed,
        Date dateReceived) {
        PatientVisitWrapper pv = new PatientVisitWrapper(appService);
        pv.setPatient(patient);
        pv.setDateDrawn(dateDrawn);
        pv.setDateProcessed(dateProcessed);
        pv.setDateReceived(dateReceived);
        pv.setClinic(clinic);
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

    public static int addPatientVisits(PatientWrapper patient,
        ClinicWrapper clinic) throws ParseException, Exception {
        int nber = r.nextInt(15) + 1;
        for (int i = 0; i < nber; i++) {
            addPatientVisit(patient, clinic, Utils.getRandomDate(), null, null);
        }
        return nber;
    }

}
