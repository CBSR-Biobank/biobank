package edu.ualberta.med.biobank.common.action.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenListGetInfoAction;

public class ProcessingEventGetSourceSpecimenListInfoAction extends
    SpecimenListGetInfoAction {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SOURCE_SPEC_QRY =
        SpecimenListGetInfoAction.SPEC_BASE_QRY
            + " INNER JOIN FETCH spec.processingEvent"
            + " WHERE spec.processingEvent.id=?";

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
