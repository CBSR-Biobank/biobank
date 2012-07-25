package edu.ualberta.med.biobank.action.processingEvent;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.processingEvent.ProcessingEventDeletePermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.ActivityStatus;

public class ProcessingEventDeleteAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString HAS_CHILD_SPECIMENS_ERRMSG =
        bundle.tr("Delete failed. There are child specimens linked through" +
            " this processing event").format();

    private final Integer peventId;

    public ProcessingEventDeleteAction(ProcessingEvent pevent) {
        if (pevent == null) {
            throw new IllegalArgumentException();
        }
        this.peventId = pevent.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ProcessingEventDeletePermission(peventId).isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        ProcessingEvent pevent = context.load(ProcessingEvent.class, peventId);

        // if no aliquoted specimen, then ok to remove the specimens and to
        // delete the processing event

        for (Specimen sp : pevent.getSpecimens()) {
            if (sp.getChildSpecimens().size() != 0)
                throw new LocalizedException(HAS_CHILD_SPECIMENS_ERRMSG);
            sp.setActivityStatus(ActivityStatus.ACTIVE);
            sp.setProcessingEvent(null);
            context.getSession().saveOrUpdate(sp);
        }

        context.getSession().delete(pevent);

        return new IdResult(peventId);
    }
}
