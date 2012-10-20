package edu.ualberta.med.biobank.permission.processingEvent;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class ProcessingEventCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer centerId;

    public ProcessingEventCreatePermission(Integer centerId) {
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.PROCESSING_EVENT_CREATE.isAllowed(
            context.getUser(), context.load(Center.class, centerId));
    }
}
