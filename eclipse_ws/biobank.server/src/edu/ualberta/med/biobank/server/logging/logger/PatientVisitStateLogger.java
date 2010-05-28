package edu.ualberta.med.biobank.server.logging.logger;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientVisitStateLogger extends BiobankObjectStateLogger {

    private static PatientVisitStateLogger instance = null;

    private PatientVisitStateLogger() {

    }

    @Override
    protected Log getLogObject(Object obj) {
        if (obj instanceof PatientVisit) {
            PatientVisit visit = (PatientVisit) obj;
            Log log = new Log();
            log.setPatientNumber(visit.getPatient().getPnumber());
            // FIXME add worksheet ?
            log.setDetails("date processed="
                + dateTimeFormatter.format(visit.getDateProcessed()));
            log.setType("Visit");
            return log;
        }
        return null;
    }

    public static PatientVisitStateLogger getInstance() {
        if (instance == null) {
            instance = new PatientVisitStateLogger();
        }
        return instance;
    }
}
