package edu.ualberta.med.biobank.common.permission.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventReadPermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer peId;
    private Integer centerId;

    public ProcessingEventReadPermission(Integer peId) {
        this.peId = peId;
    }

    public ProcessingEventReadPermission(Center center) {
        this.centerId = center.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (peId == null)
            return PermissionEnum.PROCESSING_EVENT_READ.isAllowed(
                context.getUser(), context.load(Center.class, centerId)
                );
        return PermissionEnum.PROCESSING_EVENT_READ.isAllowed(
            context.getUser(), context.load(ProcessingEvent.class, peId)
                .getCenter()
            );
    }
}
