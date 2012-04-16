package edu.ualberta.med.biobank.common.permission.processingEvent;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;

public class ProcessingEventReadPermission implements Permission {
    private static final long serialVersionUID = 1L;
    private Integer peId;
    private Site site;

    public ProcessingEventReadPermission(Integer peId) {
        this.peId = peId;
    }

    public ProcessingEventReadPermission(Site site) {
        this.site = site;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (peId == null)
            return PermissionEnum.PROCESSING_EVENT_READ.isAllowed(
                context.getUser(), site
                );
        return PermissionEnum.PROCESSING_EVENT_READ.isAllowed(
            context.getUser(), context.load(ProcessingEvent.class, peId)
                .getCenter()
            );
    }
}
