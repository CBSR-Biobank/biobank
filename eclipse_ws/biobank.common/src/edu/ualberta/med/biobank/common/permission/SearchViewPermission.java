package edu.ualberta.med.biobank.common.permission;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class SearchViewPermission implements Permission {
    private static final long serialVersionUID = 2458342314629727268L;

    private Integer type;

    @SuppressWarnings("unused")
    public SearchViewPermission(Integer type, Integer workingCenterId) {
        this.type = type;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        if (type.equals(1))
            return false;
        return PermissionEnum.SPECIMEN_READ.isAllowed(context.getUser())
            && PermissionEnum.CONTAINER_READ.isAllowed(context.getUser())
            && PermissionEnum.PROCESSING_EVENT_READ
                .isAllowed(context.getUser());
    }

}
