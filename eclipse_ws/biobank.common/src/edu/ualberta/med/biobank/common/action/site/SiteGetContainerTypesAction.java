package edu.ualberta.med.biobank.common.action.site;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.site.SiteGetContainerTypesAction.SiteGetContainerTypesResult;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeReadPermission;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

public class SiteGetContainerTypesAction implements
    Action<SiteGetContainerTypesResult> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SELECT_CONTAINER_TYPES_HQL =
        "SELECT ctype"
            + " FROM " + ContainerType.class.getName() + " ctype"
            + " INNER JOIN FETCH ctype.site site"
            + " WHERE site.id = ?";

    public static class SiteGetContainerTypesResult implements ActionResult {
        private static final long serialVersionUID = 1L;

        private final ArrayList<ContainerType> containerTypes;

        public SiteGetContainerTypesResult(
            ArrayList<ContainerType> containerTypes) {
            this.containerTypes = containerTypes;
        }

        public ArrayList<ContainerType> getContainerTypes() {
            return containerTypes;
        }
    }

    private final Integer siteId;

    public SiteGetContainerTypesAction(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Site site = context.load(Site.class, siteId);
        return new ContainerTypeReadPermission(site).isAllowed(context);
    }

    @Override
    public SiteGetContainerTypesResult run(ActionContext context)
        throws ActionException {
        ArrayList<ContainerType> containerTypes =
            new ArrayList<ContainerType>(0);

        Query query =
            context.getSession().createQuery(SELECT_CONTAINER_TYPES_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<ContainerType> results = query.list();
        if (results != null) {
            containerTypes.addAll(results);
        }

        return new SiteGetContainerTypesResult(containerTypes);
    }

}
