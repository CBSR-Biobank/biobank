package edu.ualberta.med.biobank.common.action.site;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;

public class SiteGetTopContainersAction implements
    Action<ListResult<Container>> {
    private static final long serialVersionUID = 1L;

    // This query has to initialise specimenPositions due to the
    // tree adapter needing to know this to display additional menu selections
    // when a right click is done on a container node.
    //
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SELECT_TOP_CONTAINERS_HQL = 
        "SELECT container"
            + " FROM " + Container.class.getName() + " container"
            + " INNER JOIN FETCH container.containerType containerType"
            + " INNER JOIN FETCH container.site site"
            + " LEFT JOIN FETCH container.specimenPositions"
            + " WHERE site.id = ?"
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
        return new ContainerReadPermission().isAllowed(context);
    }

    @Override
    public ListResult<Container> run(ActionContext context)
        throws ActionException {
        ArrayList<Container> topContainers = new ArrayList<Container>(0);

        Query query =
            context.getSession().createQuery(SELECT_TOP_CONTAINERS_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Container> results = query.list();
        if (results != null) {
            topContainers.addAll(results);
        }

        return new ListResult<Container>(topContainers);
    }
}
