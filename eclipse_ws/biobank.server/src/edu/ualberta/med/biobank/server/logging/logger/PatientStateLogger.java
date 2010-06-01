package edu.ualberta.med.biobank.server.logging.logger;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;

public class PatientStateLogger extends BiobankObjectStateLogger {

    protected PatientStateLogger() {
    }

    @Override
    protected Log getLogObject(Object obj) {
        if (obj instanceof Patient) {
            Patient patient = (Patient) obj;
            Log log = new Log();
            log.setPatientNumber(patient.getPnumber());
            log.setType("Patient");
            return log;
        }
        return null;
    }

}
