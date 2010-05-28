package edu.ualberta.med.biobank.server.logging.logger;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvAttr;

public class PatientVisitStateLogger extends BiobankObjectStateLogger {

    protected PatientVisitStateLogger() {
    }

    @Override
    protected Log getLogObject(Object obj) {
        if (obj instanceof PatientVisit) {
            PatientVisit visit = (PatientVisit) obj;
            Log log = new Log();
            log.setPatientNumber(visit.getPatient().getPnumber());
            String worksheet = "";
            for (PvAttr pvAttr : visit.getPvAttrCollection()) {
                if (pvAttr.getStudyPvAttr().getLabel().equals("Worksheet")) {
                    worksheet = " - Worksheet: " + pvAttr.getValue();
                }
            }
            log.setDetails("Date Processed: "
                + dateTimeFormatter.format(visit.getDateProcessed())
                + worksheet);
            log.setType("Visit");
            return log;
        }
        return null;
    }

}
