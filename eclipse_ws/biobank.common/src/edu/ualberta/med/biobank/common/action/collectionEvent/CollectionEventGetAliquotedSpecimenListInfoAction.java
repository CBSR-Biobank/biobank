package edu.ualberta.med.biobank.common.action.collectionEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenListGetInfoAction;

public class CollectionEventGetAliquotedSpecimenListInfoAction extends
    SpecimenListGetInfoAction {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String ALIQUOTED_SPEC_QRY =
        SpecimenListGetInfoAction.SPEC_BASE_QRY
            + " LEFT JOIN FETCH spec.parentSpecimen parentSpec"
            + " LEFT JOIN FETCH parentSpec.processingEvent"
            + " WHERE spec.collectionEvent.id=?"
            + " AND spec.parentSpecimen IS NOT null"
            + SpecimenListGetInfoAction.SPEC_BASE_END;

    private Integer ceventId;

    public CollectionEventGetAliquotedSpecimenListInfoAction(Integer cevenId) {
        this.ceventId = cevenId;
    }

    @Override
    public ListResult<SpecimenInfo> run(ActionContext context)
        throws ActionException {
        return run(context, ALIQUOTED_SPEC_QRY, ceventId);
    }

}
