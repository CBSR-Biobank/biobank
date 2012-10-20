package edu.ualberta.med.biobank.action.site;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.info.SiteContainerTypeInfo;
import edu.ualberta.med.biobank.permission.site.SiteReadPermission;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.center.Container;
import edu.ualberta.med.biobank.model.center.ContainerType;

public class SiteGetContainerTypeInfoAction implements
    Action<ListResult<SiteContainerTypeInfo>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SELECT_CONTAINER_TYPE_INFO_HQL =
        "SELECT containerType,"
            + " (SELECT COUNT(*) FROM "
            + Container.class.getName()
            + " c WHERE c.containerType = containerType)"
            + " FROM " + ContainerType.class.getName() + " containerType"
            + " INNER JOIN FETCH containerType.capacity"
            + " WHERE containerType.site.id = ?"
            + " ORDER BY containerType.nameShort";

    private final Integer siteId;

    public SiteGetContainerTypeInfoAction(Integer siteId) {
        this.siteId = siteId;
    }

    public SiteGetContainerTypeInfoAction(Site site) {
        this(site.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new SiteReadPermission(siteId).isAllowed(context);
    }

    @Override
    public ListResult<SiteContainerTypeInfo> run(ActionContext context)
        throws ActionException {

        ArrayList<SiteContainerTypeInfo> containerTypes =
            new ArrayList<SiteContainerTypeInfo>();

        Query query =
            context.getSession().createQuery(SELECT_CONTAINER_TYPE_INFO_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {
            SiteContainerTypeInfo containerTypeInfo =
                new SiteContainerTypeInfo(
                    (ContainerType) row[0], (Long) row[1]);
            containerTypes.add(containerTypeInfo);
        }

        return new ListResult<SiteContainerTypeInfo>(containerTypes);
    }
}
