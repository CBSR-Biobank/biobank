package edu.ualberta.med.biobank.permission.reports;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class ReportsPermission implements Permission {

    /**
     * 
     */
    private static final long serialVersionUID = -8879361517599287366L;

    public ReportsPermission() {

    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.REPORTS.isAllowed(context.getUser());
    }

}
