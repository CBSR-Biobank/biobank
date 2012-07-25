package edu.ualberta.med.biobank.permission.processingEvent;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventUpdatePermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer peId;

    public ProcessingEventUpdatePermission(Integer peId) {
        this.peId = peId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        ProcessingEvent pe = context.load(ProcessingEvent.class, peId);
        return PermissionEnum.PROCESSING_EVENT_UPDATE
            .isAllowed(context.getUser(), pe.getCenter());
    }
}
