package edu.ualberta.med.biobank.common.permission.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer peventId;

    public ProcessingEventReadPermission(ProcessingEvent pevent) {
        this.peventId = pevent.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.PROCESSING_EVENT_READ.isAllowed(
            context.getUser(), context.load(ProcessingEvent.class, peventId)
                .getCenter());
    }
}
