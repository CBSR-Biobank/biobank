package edu.ualberta.med.biobank.common.permission.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ProcessingEventReadPermissionByCenter implements Permission {
    private static final long serialVersionUID = 1L;

    private final Center center;

    public ProcessingEventReadPermissionByCenter(Center center) {
        this.center = center;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.PROCESSING_EVENT_READ.isAllowed(
            context.getUser(), center);
    }

}
