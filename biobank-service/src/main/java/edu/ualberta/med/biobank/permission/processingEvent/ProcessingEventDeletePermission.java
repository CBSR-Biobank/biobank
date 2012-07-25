package edu.ualberta.med.biobank.permission.processingEvent;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventDeletePermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer peId;

    public ProcessingEventDeletePermission(Integer peId) {
        this.peId = peId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        ProcessingEvent pe = context.load(ProcessingEvent.class, peId);
        return PermissionEnum.PROCESSING_EVENT_DELETE
            .isAllowed(context.getUser(), pe.getCenter());
    }
}
