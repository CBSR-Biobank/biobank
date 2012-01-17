package edu.ualberta.med.biobank.common.permission.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;

public class ProcessingEventReadPermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer peventId;

    public ProcessingEventReadPermission(Integer peventId) {
        this.peventId = peventId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        // FIXME specific study or center ?
        return PermissionEnum.PROCESSING_EVENT_READ
            .isAllowed(context.getUser());
    }
}
