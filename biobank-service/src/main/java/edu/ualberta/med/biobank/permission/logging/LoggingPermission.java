package edu.ualberta.med.biobank.permission.logging;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class LoggingPermission implements Permission {

    /**
     * 
     */
    private static final long serialVersionUID = -8879361517599287366L;

    public LoggingPermission() {

    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.LOGGING.isAllowed(context.getUser());
    }

}
