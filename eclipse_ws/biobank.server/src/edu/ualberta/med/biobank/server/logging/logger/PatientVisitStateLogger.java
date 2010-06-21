package edu.ualberta.med.biobank.server.logging.logger;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvAttr;

public class PatientVisitStateLogger extends BiobankObjectStateLogger {

    protected PatientVisitStateLogger() {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Log getLogObject(Object obj, Map<String, Object> statesMap) {
        if (obj instanceof PatientVisit) {
            Log log = new Log();
            Patient patient = (Patient) statesMap.get("patient");
            log.setPatientNumber(patient.getPnumber());
            String details = "";
            Date dateProcesssed = (Date) statesMap.get("dateProcessed");
            if (dateProcesssed != null) {
                details = "Date Processed: "
                    + dateTimeFormatter.format(dateProcesssed);
            }
            Collection<PvAttr> pvAttrs = (Collection<PvAttr>) statesMap
                .get("pvAttrCollection");
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
