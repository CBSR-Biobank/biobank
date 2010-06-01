package edu.ualberta.med.biobank.server.logging.logger;

import java.util.Collection;
import java.util.Date;

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
            String details = "";
            Date dateProcesssed = visit.getDateProcessed();
            if (dateProcesssed != null) {
                details = "Date Processed: "
                    + dateTimeFormatter.format(dateProcesssed);
            }
            Collection<PvAttr> pvAttrs = visit.getPvAttrCollection();
            if (pvAttrs != null) {
                for (PvAttr pvAttr : pvAttrs) {
                    if (pvAttr.getStudyPvAttr().getLabel().equals("Worksheet")) {
                        details += " - Worksheet: " + pvAttr.getValue();
                    }
                }
            }
            log.setDetails(details);
            log.setType("Visit");
            return log;
        }
        return null;
    }
}
