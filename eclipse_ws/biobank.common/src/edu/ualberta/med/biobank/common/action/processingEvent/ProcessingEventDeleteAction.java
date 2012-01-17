package edu.ualberta.med.biobank.common.action.processingEvent;

import java.text.MessageFormat;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.NotUsedCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventDeletePermission;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;

public class ProcessingEventDeleteAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private static final String HAS_DERIVED_SPECIMENS_MSG =
        "Unable to delete processing event '{0}' ({1}) since some of its specimens have already been derived into others specimens.";

    private Integer peventId;

    public ProcessingEventDeleteAction(Integer peventId) {
        this.peventId = peventId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ProcessingEventDeletePermission(peventId).isAllowed(null);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        ProcessingEvent pevent = (ProcessingEvent) context.load(
            ProcessingEvent.class, peventId);

        String hasDerivedSpecimensMsg = MessageFormat.format(
            HAS_DERIVED_SPECIMENS_MSG, pevent.getWorksheet(),
            DateFormatter.formatAsDateTime(pevent.getCreatedAt()));

        new NotUsedCheck<ProcessingEvent>(pevent,
            SpecimenPeer.PARENT_SPECIMEN
                .to(SpecimenPeer.PROCESSING_EVENT),
            Specimen.class, pevent.getWorksheet(), hasDerivedSpecimensMsg)
            .run(null);

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
