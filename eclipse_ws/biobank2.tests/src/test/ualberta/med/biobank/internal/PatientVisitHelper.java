package test.ualberta.med.biobank.internal;

import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;

public class PatientVisitHelper extends DbHelper {

    public static PatientVisitWrapper newPatientVisit(PatientWrapper patient,
        Date dateDrawn, Date dateProcessed, Date dateReceived) {
        PatientVisitWrapper pv = new PatientVisitWrapper(appService);
        pv.setPatient(patient);
        pv.setDateDrawn(dateDrawn);
        pv.setDateProcessed(dateProcessed);
        pv.setDateReceived(dateReceived);
        return pv;
    }

    public static PatientVisitWrapper addPatientVisit(PatientWrapper patient,
        Date dateDrawn, Date dateProcessed, Date dateReceived) throws Exception {
        PatientVisitWrapper pv = newPatientVisit(patient, dateDrawn,
            dateProcessed, dateReceived);
        pv.persist();
        return pv;
    }

}
