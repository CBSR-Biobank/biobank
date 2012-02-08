package edu.ualberta.med.biobank.common.action.processingEvent;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventDeletePermission;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;

public class ProcessingEventDeleteAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private Integer peventId;

    public ProcessingEventDeleteAction(Integer peventId) {
        this.peventId = peventId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ProcessingEventDeletePermission(peventId).isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        ProcessingEvent pevent = (ProcessingEvent) context.load(
            ProcessingEvent.class, peventId);

        // if no aliquoted specimen, then ok to remove the specimens and to
        // delete the processing event
        for (Specimen sp : pevent.getSpecimenCollection()) {
            sp.setProcessingEvent(null);
            context.getSession().saveOrUpdate(sp);
        }

        context.getSession().delete(pevent);

        return new IdResult(peventId);
    }
}
