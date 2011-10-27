package edu.ualberta.med.biobank.common.action.site;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.site.SiteGetContainerTypeInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.permission.site.SiteReadPermission;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class SiteGetContainerTypeInfoAction implements
    Action<ArrayList<ContainerTypeInfo>> {
    public static class ContainerTypeInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        private final ContainerType containerType;
        private final Long containerCount;

        public ContainerTypeInfo(ContainerType containerType,
            Long containerCount) {
            this.containerType = containerType;
            this.containerCount = containerCount;
        }

        public ContainerType getContainerType() {
            return containerType;
        }

        public Long getContainerCount() {
            return containerCount;
        }
    }

    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SELECT_CONTAINER_TYPE_INFO_HQL = "SELECT containerType,"
        + " (SELECT COUNT(*) FROM "
        + Container.class.getName()
        + " c WHERE c.containerType = containerType)"
        + " FROM " + ContainerType.class.getName() + " containerType"
        + " INNER JOIN FETCH containerType.activityStatus AS activityStatus"
        + " INNER JOIN FETCH containerType.capacity capacity"
        + " WHERE containerType.site.id = ?"
        + " ORDER BY containerType.nameShort";
    // @formatter:on

    private final Integer siteId;

    public SiteGetContainerTypeInfoAction(Integer siteId) {
        this.siteId = siteId;
    }

    public SiteGetContainerTypeInfoAction(Site site) {
        this(site.getId());
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new SiteReadPermission(siteId).isAllowed(user, session);
    }

    @Override
    public ArrayList<ContainerTypeInfo> run(User user, Session session)
        throws ActionException {

        ArrayList<ContainerTypeInfo> containerTypes = new ArrayList<ContainerTypeInfo>();

        Query query = session.createQuery(SELECT_CONTAINER_TYPE_INFO_HQL);
        query.setParameter(0, siteId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.list();
        for (Object[] row : results) {
            ContainerTypeInfo containerTypeInfo = new ContainerTypeInfo(
                (ContainerType) row[0], (Long) row[1]);
            containerTypes.add(containerTypeInfo);
        }

        return containerTypes;
    }
}
