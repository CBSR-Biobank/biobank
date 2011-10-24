package edu.ualberta.med.biobank.common.wrappers.loggers;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;

public class PatientLogProvider implements WrapperLogProvider<Patient> {
    private static final long serialVersionUID = 1L;

    @Override
    public Log getLog(Patient patient) {
        Log log = new Log();

        log.setPatientNumber(patient.getPnumber());

        return log;
    }

    @Override
    public Log getObjectLog(Object model) {
        return getLog((Patient) model);
    }

}
