package edu.ualberta.med.biobank.common.action.site;

import java.util.List;

import org.hibernate.criterion.Restrictions;

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

    /**
     * This action has to initialise specimenPositions due to the tree adapter needing to know it to
     * display additional menu selections when a right click is done on a container node.
     * 
     * Also has to initialise containerType.childContainerTypes to support container drag and drop.
     */
    @SuppressWarnings({ "unchecked", "nls" })
    @Override
    public ListResult<Container> run(ActionContext context) throws ActionException {

        List<Container> results = context.getSession().createCriteria(Container.class, "container")
            .createAlias("container.containerType", "ctype")
            .add(Restrictions.eq("ctype.topLevel", true))
            .add(Restrictions.eq("container.site.id", siteId))
            .list();

        for (Container container : results) {
            container.getContainerType().getChildContainerTypes().size();
            container.getSpecimenPositions().size();
        }

        return new ListResult<Container>(results);
    }
}
