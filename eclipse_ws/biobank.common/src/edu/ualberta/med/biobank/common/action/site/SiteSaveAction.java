package edu.ualberta.med.biobank.common.action.site;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.SiteEditPermission;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class SiteSaveAction implements Action<Site> {
    private static final long serialVersionUID = 1L;

    private final Site site;

    public SiteSaveAction(Site site) {
        this.site = site;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Permission permission = new SiteEditPermission(site);
        return permission.isAllowed(user);
    }

    @Override
    public Site doAction(Session session) throws ActionException {
        // PreCheck<Site> preCheck = new PreCheck<Site>(session, site);
        //
        // preCheck.notNull(CenterPeer.NAME);
        // preCheck.notNull(CenterPeer.NAME_SHORT);
        //
        // preCheck.unique(CenterPeer.NAME);
        // preCheck.unique(CenterPeer.NAME_SHORT);
        //
        // ModelDiff<Site> diff = new ModelDiff<Site>(session, site);
        //
        // diff.persistRemoved(SitePeer.STUDY_COLLECTION);

        session.saveOrUpdate(site);

        return site;
    }
}
