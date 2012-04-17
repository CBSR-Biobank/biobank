package edu.ualberta.med.biobank.common.action.processingEvent;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventDeletePermission;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;

public class ProcessingEventDeleteAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private final Integer peventId;

    public ProcessingEventDeleteAction(ProcessingEvent pevent) {
        if (pevent == null) {
            throw new IllegalArgumentException();
        }
        this.peventId = pevent.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ProcessingEventDeletePermission().isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public IdResult run(ActionContext context) throws ActionException {
        ProcessingEvent pevent = context.load(ProcessingEvent.class, peventId);

        // if no aliquoted specimen, then ok to remove the specimens and to
        // delete the processing event

        for (Specimen sp : pevent.getSpecimens()) {
            if (sp.getChildSpecimens().size() != 0)
                throw new ActionException(LString.tr("Delete failed." +
                    " There are child specimens linked through this" +
                    " processing event"));
            sp.setActivityStatus(ActivityStatus.ACTIVE);
            sp.setProcessingEvent(null);
            context.getSession().saveOrUpdate(sp);
        }

        context.getSession().delete(pevent);

        return new IdResult(peventId);
    }
}
