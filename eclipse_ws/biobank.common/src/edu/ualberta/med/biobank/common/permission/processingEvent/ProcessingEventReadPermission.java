package edu.ualberta.med.biobank.common.permission.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class ProcessingEventReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) {
        // FIXME specific study or center ?
        return PermissionEnum.PROCESSING_EVENT_READ
            .isAllowed(context.getUser());
    }
}
