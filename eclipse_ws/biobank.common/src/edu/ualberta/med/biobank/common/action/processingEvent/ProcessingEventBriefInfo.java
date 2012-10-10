package edu.ualberta.med.biobank.common.action.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventBriefInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    public ProcessingEvent pevent;
    public String studyNameShort;
    public Long sourceSpcCount;
    public Long aliquotSpcCount;

    public ProcessingEventBriefInfo(ProcessingEvent pevent,
        String studyNameShort, Long sourceSpcCount, Long aliquotSpcCount) {
        this.pevent = pevent;
        this.studyNameShort = studyNameShort;
        this.sourceSpcCount = sourceSpcCount;
        this.aliquotSpcCount = aliquotSpcCount;
    }

}
