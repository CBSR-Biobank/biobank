package edu.ualberta.med.biobank.common.action.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventBriefInfo implements ActionResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public ProcessingEvent e;
    public String study;
    public Long svs;
    public Long aliquots;

    public ProcessingEventBriefInfo(ProcessingEvent e, String study, Long svs,
        Long aliquots) {
        this.e = e;
        this.study = study;
        this.svs = svs;
        this.aliquots = aliquots;
    }

}
