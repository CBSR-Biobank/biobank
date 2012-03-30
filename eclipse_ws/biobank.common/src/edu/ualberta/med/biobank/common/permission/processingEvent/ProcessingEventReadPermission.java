package edu.ualberta.med.biobank.common.permission.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Site;

public class ProcessingEventReadPermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer site;

    public ProcessingEventReadPermission(Integer center) {
        this.site = center;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        // get is intended
        return PermissionEnum.PROCESSING_EVENT_READ.isAllowed(
            context.getUser(),
            context.get(Site.class, site));
    }
}
