package edu.ualberta.med.biobank.permission.specimen;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Site;

public class SpecimenSiteReadPermission implements Permission {

    private static final long serialVersionUID = 1L;
    private Integer site;

    public SpecimenSiteReadPermission(Integer site) {
        this.site = site;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return PermissionEnum.SPECIMEN_READ.isAllowed(context.getUser(),
            context.load(Site.class,
                site));
    }

}
