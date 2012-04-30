package edu.ualberta.med.biobank.common.permission.reports;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;

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
