package edu.ualberta.med.biobank.action.site;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.site.SiteReadPermission;
import edu.ualberta.med.biobank.model.Site;

public class SiteGetAllAction implements Action<ListResult<Site>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String ALL_SITES = "from "
        + Site.class.getName();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // any user can call this, but will only receive permitted results
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<Site> run(ActionContext context) throws ActionException {
        Query q = context.getSession().createQuery(ALL_SITES);
        List<Site> sites = q.list();
        List<Site> readableSites = new ArrayList<Site>();
        for (Site site : sites)
            if (new SiteReadPermission(site).isAllowed(context))
                readableSites.add(site);
        return new ListResult<Site>(readableSites);
    }
}
