package edu.ualberta.med.biobank.common.permission.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventUpdatePermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer peventId;

    public ProcessingEventUpdatePermission(ProcessingEvent pevent) {
        this.peventId = pevent.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        ProcessingEvent pe = context.load(ProcessingEvent.class, peventId);
        return PermissionEnum.PROCESSING_EVENT_UPDATE
            .isAllowed(context.getUser(), pe.getCenter());
    }
}
