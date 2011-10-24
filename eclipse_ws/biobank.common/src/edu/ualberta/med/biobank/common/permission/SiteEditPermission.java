package edu.ualberta.med.biobank.common.permission;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

// TODO: what about creating new sites? Only able to create a new site with 

// TODO: need a "dbFirst" interceptor that tries to load the object from the database, but defaults to memory if not found in the database?
public class SiteEditPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Site site;

    public SiteEditPermission(Site site) {
        this.site = site;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return PermissionUtil.isAllowed(user, this, site);
    }
}
