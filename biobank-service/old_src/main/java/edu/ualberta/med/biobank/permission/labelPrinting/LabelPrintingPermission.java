package edu.ualberta.med.biobank.permission.labelPrinting;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class LabelPrintingPermission implements Permission {
    private static final long serialVersionUID = 1L;

    public LabelPrintingPermission() {
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.LABEL_PRINTING.isAllowed(context.getUser());
    }
}
