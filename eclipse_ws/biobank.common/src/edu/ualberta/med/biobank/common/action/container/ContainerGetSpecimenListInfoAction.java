package edu.ualberta.med.biobank.common.action.container;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenListGetInfoAction;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ContainerGetSpecimenListInfoAction extends SpecimenListGetInfoAction {

    @SuppressWarnings("nls")
    private static final String SPEC_QRY =
        SpecimenListGetInfoAction.SPEC_BASE_QRY
            + " INNER JOIN FETCH spec.specimenPosition p"
            + " INNER JOIN FETCH p.container c"
            + " WHERE c.id=?";

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
    public ListResult<SpecimenInfo> run(ActionContext context) throws ActionException {
        ListResult<SpecimenInfo> spcData = run(context, SPEC_QRY, containerId);
        for (SpecimenInfo spcInfo : spcData.getList()) {
            ProcessingEvent pevent = spcInfo.specimen.getProcessingEvent();
            if (pevent != null) {
                pevent.getWorksheet();
            }
        }
        return spcData;
    }
}
