package edu.ualberta.med.biobank.common.permission.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ProcessingEventCreatePermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer centerId;

    public ProcessingEventCreatePermission() {

    }

    public ProcessingEventCreatePermission(Center center) {
        this.centerId = center.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (centerId == null)
            return PermissionEnum.PROCESSING_EVENT_CREATE
                .isAllowed(context.getUser());
        return PermissionEnum.PROCESSING_EVENT_CREATE.isAllowed(
            context.getUser(), context.load(Center.class, centerId));
    }
}
