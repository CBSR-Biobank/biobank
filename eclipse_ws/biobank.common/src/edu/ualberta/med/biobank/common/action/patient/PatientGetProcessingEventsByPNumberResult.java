package edu.ualberta.med.biobank.common.action.patient;

import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class PatientGetProcessingEventsByPNumberResult implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final boolean patientExists;
    private final List<ProcessingEvent> processingEvents;

    public PatientGetProcessingEventsByPNumberResult(boolean patientExists,
        List<ProcessingEvent> processingEvents) {
        this.patientExists = patientExists;
        this.processingEvents = processingEvents;
    }

    public List<ProcessingEvent> getProcessingEvents() {
        return processingEvents;
    }

    public boolean isPatientExists() {
        return patientExists;
    }
}
