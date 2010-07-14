package edu.ualberta.med.biobank.server.logging.logger;

import java.util.Map;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;

public class PatientStateLogger extends BiobankObjectStateLogger {

    protected PatientStateLogger() {
    }

    @Override
    protected Log getLogObject(Object obj, Map<String, Object> statesMap) {
        if (obj instanceof Patient) {
            Log log = new Log();
            log.setPatientNumber((String) statesMap.get("pNumber"));
            log.setType("Patient");
            return log;
        }
        return null;
    }

}
