package edu.ualberta.med.biobank.common.action.collectionEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;

public class CollectionEventGetSourceSpecimenInfoAction extends
    SpecimenGetInfoAction {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SOURCE_SPEC_QRY =
        SpecimenGetInfoAction.SPEC_BASE_QRY
            + " LEFT JOIN FETCH spec.processingEvent"
            + " WHERE spec.originalCollectionEvent.id=?"
            + SpecimenGetInfoAction.SPEC_END_QRY;

    private Integer ceventId;

    public CollectionEventGetSourceSpecimenInfoAction(Integer cevenId) {
        this.ceventId = cevenId;
    }

    @Override
    public ListResult<SpecimenInfo> run(ActionContext context)
        throws ActionException {
        return run(context, SOURCE_SPEC_QRY, ceventId);
    }
}
