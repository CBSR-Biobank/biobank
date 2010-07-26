package edu.ualberta.med.biobank.server.logging.logger;

import java.util.Map;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;

public class PatientStateLogger extends BiobankObjectStateLogger {

    protected PatientStateLogger() {
    }

    @Override
    protected Log getLogObject(Object obj, Map<String, Object> statesMap) {
        if (obj instanceof Patient) {
            Log log = new Log();
            Study study = (Study) statesMap.get("study");
            log.setSite(study.getSite().getNameShort());
            log.setPatientNumber((String) statesMap.get("pNumber"));
            log.setType("Patient");
            return log;
        }
        return null;
    }

}
