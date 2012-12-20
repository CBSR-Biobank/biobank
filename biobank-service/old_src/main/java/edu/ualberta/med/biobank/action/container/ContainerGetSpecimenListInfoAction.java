package edu.ualberta.med.biobank.action.container;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.action.specimen.SpecimenListGetInfoAction;
import edu.ualberta.med.biobank.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.center.Container;

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
    private final Integer containerId;

    public ContainerGetSpecimenListInfoAction(Integer containerId) {
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Container c = context.load(Container.class, containerId);
        return new ContainerReadPermission(c).isAllowed(context);
    }

    @Override
    public ListResult<SpecimenInfo> run(ActionContext context)
        throws ActionException {
        return run(context, SPEC_QRY, containerId);
    }
}
