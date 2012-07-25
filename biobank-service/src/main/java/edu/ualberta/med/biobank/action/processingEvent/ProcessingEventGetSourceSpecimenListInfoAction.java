package edu.ualberta.med.biobank.action.processingEvent;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.action.specimen.SpecimenListGetInfoAction;

public class ProcessingEventGetSourceSpecimenListInfoAction extends
    SpecimenListGetInfoAction {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SOURCE_SPEC_QRY =
        SpecimenListGetInfoAction.SPEC_BASE_QRY
            + " INNER JOIN FETCH spec.processingEvent"
            + " LEFT JOIN FETCH spec.childSpecimens"
            + " WHERE spec.processingEvent.id=?"
            + SpecimenListGetInfoAction.SPEC_BASE_END;

    private final Integer peventId;

    public ProcessingEventGetSourceSpecimenListInfoAction(Integer pevenId) {
        this.peventId = pevenId;
    }

    @Override
    public ListResult<SpecimenInfo> run(ActionContext context)
        throws ActionException {
        return run(context, SOURCE_SPEC_QRY, peventId);
    }
}
