package edu.ualberta.med.biobank.server.logging.logger;

import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Log;

public class AliquotStateLogger extends BiobankObjectStateLogger {

    private static AliquotStateLogger instance = null;

    private AliquotStateLogger() {

    }

    @Override
    protected Log getLogObject(Object obj) {
        if (obj instanceof Aliquot) {
            Aliquot aliquot = (Aliquot) obj;
            Log log = new Log();
            log.setPatientNumber(aliquot.getPatientVisit().getPatient()
                .getPnumber());
            log.setInventoryId(aliquot.getInventoryId());
            log.setLocationLabel(aliquot.getAliquotPosition().getContainer()
                .getLabel());
            log.setType("Aliquot");
            return log;
        }
        return null;
    }

    public static AliquotStateLogger getInstance() {
        if (instance == null) {
            instance = new AliquotStateLogger();
        }
        return instance;
    }
}
