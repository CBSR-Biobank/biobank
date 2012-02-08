package edu.ualberta.med.biobank.common.action.site;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.center.CenterDeleteAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.site.SiteDeletePermission;
import edu.ualberta.med.biobank.model.Site;

public class SiteDeleteAction extends CenterDeleteAction {
    private static final long serialVersionUID = 1L;

    public SiteDeleteAction(Integer id) {
        super(id);
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new SiteDeletePermission(centerId).isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        Site site = context.load(Site.class, centerId);
        return super.run(context, site);
    }
}
