package edu.ualberta.med.biobank.common.action.site;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.center.CenterDeleteAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.site.SiteDeletePermission;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class SiteDeleteAction extends CenterDeleteAction {
    private static final long serialVersionUID = 1L;

    public SiteDeleteAction(Integer id) {
        super(id);
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new SiteDeletePermission(centerId).isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        Site site = ActionUtil.sessionGet(session, Site.class, centerId);
        return super.run(user, session, site);
    }
}
