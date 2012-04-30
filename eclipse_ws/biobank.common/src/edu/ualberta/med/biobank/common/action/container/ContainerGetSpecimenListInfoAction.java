package edu.ualberta.med.biobank.common.action.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenListGetInfoAction;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoReadPermission;

public class ContainerGetSpecimenListInfoAction extends
    SpecimenListGetInfoAction {

    @SuppressWarnings("nls")
    private static final String SPEC_QRY =
        SpecimenListGetInfoAction.SPEC_BASE_QRY
            + " INNER JOIN FETCH spec.specimenPosition p"
            + " INNER JOIN FETCH p.container c"
            + " WHERE c.id=?"
            + SpecimenListGetInfoAction.SPEC_BASE_END;

    private static final long serialVersionUID = 1L;
    private Integer containerId;

    public ContainerGetSpecimenListInfoAction(Integer containerId) {
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new OriginInfoReadPermission(containerId).isAllowed(context);
    }

    @Override
    public ListResult<SpecimenInfo> run(ActionContext context)
        throws ActionException {
        return run(context, SPEC_QRY, containerId);
    }
}
