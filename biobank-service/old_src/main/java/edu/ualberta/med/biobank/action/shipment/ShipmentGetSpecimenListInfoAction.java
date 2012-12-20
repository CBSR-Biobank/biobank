package edu.ualberta.med.biobank.action.shipment;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.action.specimen.SpecimenListGetInfoAction;
import edu.ualberta.med.biobank.permission.shipment.OriginInfoReadPermission;

public class ShipmentGetSpecimenListInfoAction extends
    SpecimenListGetInfoAction {

    @SuppressWarnings("nls")
    private static final String SPEC_QRY =
        SpecimenListGetInfoAction.SPEC_BASE_QRY
            + " WHERE originInfo.id=?"
            + SpecimenListGetInfoAction.SPEC_BASE_END;

    private static final long serialVersionUID = 1L;
    private Integer oiId;

    public ShipmentGetSpecimenListInfoAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new OriginInfoReadPermission(oiId).isAllowed(context);
    }

    @Override
    public ListResult<SpecimenInfo> run(ActionContext context)
        throws ActionException {
        return run(context, SPEC_QRY, oiId);
    }
}
