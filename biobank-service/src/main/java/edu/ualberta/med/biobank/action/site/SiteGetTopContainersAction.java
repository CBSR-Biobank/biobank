package edu.ualberta.med.biobank.action.site;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

public class SiteGetTopContainersAction implements
    Action<ListResult<Container>> {
    private static final long serialVersionUID = 1L;

    // only select top-level/ Container-s
    @SuppressWarnings("nls")
    private static final String SELECT_TOP_CONTAINERS_HQL =
        "SELECT container"
            + " FROM " + Container.class.getName() + " container"
            + " WHERE site.id = ?"
            + " AND containerType.topLevel IS TRUE";

    private final Integer siteId;

    public SiteGetTopContainersAction(Integer siteId) {
        this.siteId = siteId;
    }

    public SiteGetTopContainersAction(Site site) {
        this(site.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ContainerReadPermission(siteId).isAllowed(context);
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

            for (Container c : results) {
                c.getSite().getName();

                // need to initialize containerType.childContainerTypes to
                // support container drag and drop.
                for (ContainerType ct : c.getContainerType()
                    .getChildContainerTypes()) {
                    ct.getName();
                }

                // specimenPositions have to be initialized due to the
                // tree adapter needing to know this to display additional menu
                // selections when a right click is done on a container node.
                c.getSpecimenPositions().size();
            }
        }

        return new ListResult<Container>(topContainers);
    }
}
