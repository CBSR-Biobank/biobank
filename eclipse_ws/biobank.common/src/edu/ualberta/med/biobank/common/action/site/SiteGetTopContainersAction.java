package edu.ualberta.med.biobank.common.action.site;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;

public class SiteGetTopContainersAction implements
    Action<SiteGetTopContainersResult> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SELECT_TOP_CONTAINERS_HQL = "SELECT container"
        + " FROM " + Container.class.getName() + " container"
        + " INNER JOIN FETCH container.containerType containerType"
        + " INNER JOIN FETCH container.activityStatus activityStatus"
        + " WHERE container.site.id = ?"
        + " AND containerType.topLevel IS TRUE"; // only select top-level
                                                 // Container-s
    // @formatter:on

    private final Integer siteId;

    public SiteGetTopContainersAction(Integer siteId) {
        this.siteId = siteId;
    }

    public SiteGetTopContainersAction(Site site) {
        this(site.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public SiteGetTopContainersResult run(ActionContext context)
        throws ActionException {
        ArrayList<Container> topContainers = new ArrayList<Container>();

        Query query =
            context.getSession().createQuery(SELECT_TOP_CONTAINERS_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Container> results = query.list();
        if (results != null) {
            topContainers.addAll(results);
        }

        return new SiteGetTopContainersResult(topContainers);
    }
}
