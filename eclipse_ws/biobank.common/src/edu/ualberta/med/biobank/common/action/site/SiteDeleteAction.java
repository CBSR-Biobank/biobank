package edu.ualberta.med.biobank.common.action.site;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.SiteEditPermission;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class SiteDeleteAction implements Action<Site> {
    private static final long serialVersionUID = 1L;

    private final Site site;

    public SiteDeleteAction(Site site) {
        this.site = site;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new SiteEditPermission(site).isAllowed(user, session);
    }

    @Override
    public Site run(User user, Session session) throws ActionException {
        // TODO: checks
        session.delete(site);
        return null;
    }
}
