package edu.ualberta.med.biobank.permission.processingEvent;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

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
