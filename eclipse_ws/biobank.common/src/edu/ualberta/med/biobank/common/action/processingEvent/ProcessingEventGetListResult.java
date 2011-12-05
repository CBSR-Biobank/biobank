package edu.ualberta.med.biobank.common.action.processingEvent;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventGetListResult implements ActionResult {
    public static ProcessingEventGetListResult NOT_FOUND =
        new ProcessingEventGetListResult(new ArrayList<ProcessingEvent>(),
            false);

    private static final long serialVersionUID = 1L;

    private final List<ProcessingEvent> processingEvents;
    private final boolean patientExists;

    public ProcessingEventGetListResult(List<ProcessingEvent> processingEvents,
        boolean patientExists) {
        this.processingEvents = processingEvents;
        this.patientExists = patientExists;
    }

    public List<ProcessingEvent> getProcessingEvents() {
        return processingEvents;
    }

    public boolean isPatientExists() {
        return patientExists;
    }
}
