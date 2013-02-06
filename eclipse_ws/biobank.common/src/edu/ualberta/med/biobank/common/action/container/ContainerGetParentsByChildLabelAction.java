package edu.ualberta.med.biobank.common.action.container;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

public class ContainerGetParentsByChildLabelAction implements Action<ListResult<Container>> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ContainerGetParentsByChildLabelAction.class
        .getName());

    @SuppressWarnings("nls")
    private static final String POSSIBLE_PARENTS_BASE_QRY = "SELECT DISTINCT(c) FROM "
        + Container.class.getName() + " c" + " LEFT JOIN c.containerType.childContainerTypes ct"
        + " WHERE c.site.id=? AND c.label in (";

    @SuppressWarnings("nls")
    private static final String POS_LABEL_LEN_QRY = "SELECT MIN(minChars), MAX(maxChars) FROM "
        + ContainerLabelingScheme.class.getName();

    @SuppressWarnings("nls")
    private static final String CONTAINER_TYPE_APPEND_QRY = " AND ctype.id=?";

    private final String childLabel;

    private final Integer containerTypeId;

    private final Integer siteId;

    private ActionContext context;

    /**
     * 
     * @param label
     * @param site
     * @param containerType optional
     */
    @SuppressWarnings("nls")
    public ContainerGetParentsByChildLabelAction(String label, Site site, ContainerType containerType) {
        log.debug("containerId={}", label);
        if (label == null) {
            throw new IllegalArgumentException();
        }
        if (site == null) {
            throw new IllegalArgumentException();
        }
        this.childLabel = label;
        this.siteId = site.getId();

        if (containerType != null) {
            this.containerTypeId = containerType.getId();
        } else {
            this.containerTypeId = null;
        }
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        boolean result = new ContainerReadPermission(siteId).isAllowed(context);
        log.debug("isAllowed: containerId={} allowed={}", childLabel, result);
        return result;
    }

    @SuppressWarnings({ "nls", "unchecked" })
    @Override
    public ListResult<Container> run(ActionContext context) throws ActionException {
        this.context = context;
        log.debug("run: containerId={} containerTypeId={}", childLabel, containerTypeId);

        StringBuffer queryStrBuf = new StringBuffer();
        queryStrBuf.append(CONTAINER_INFO_BASE_HQL);
        if (containerTypeId != null) {
            queryStrBuf.append(CONTAINER_TYPE_APPEND_QRY);
        } else {
            queryStrBuf.append(SITE_APPEND_QRY);
        }

        Query query = context.getSession().createQuery(queryStrBuf.toString());
        query.setParameter(0, childLabel);
        query.setParameter(1, childLabel.substring(0, childLabel.length() - 2));
        if (containerTypeId != null) {
            query.setParameter(2, containerTypeId);
        } else {
            query.setParameter(2, siteId);
        }
        return new ListResult<Container>(query.list());
    }

    /**
     * Get containers with a given label that can have a child (container or
     * specimen) with label 'childLabel'. If child is not null and is a
     * container, then will check that the parent can contain this type of
     * container
     * 
     * @param type if the child is a container, this is its type (if available)
     * @throws BiobankException
     */
    @SuppressWarnings("nls")
    private List<Container> getPossibleParents(ActionContext context) {
        List<Integer> validLengths = getPossibleLabelLength(context);
        List<String> validParents = new ArrayList<String>();

        for (Integer crop : validLengths) {
            if (crop < childLabel.length()) {
                validParents.add(new StringBuilder("'")
                .append(childLabel.substring(0, childLabel.length() - crop)).append("'")
                .toString());
            }
        }

        List<Container> filteredContainers = new ArrayList<Container>();
        if (validParents.size() > 0) {
            List<Object> params = new ArrayList<Object>();
            params.add(siteId);
            StringBuilder parentQuery = new StringBuilder(POSSIBLE_PARENTS_BASE_QRY)
            .append(StringUtil.join(validParents, ",")).append(")");
            if (containerTypeId != null) {
                parentQuery.append(" AND ct.id=?");
                params.add(containerTypeId);
            }

            Query query = context.getSession().createQuery(parentQuery.toString());

            int paramCount = 0;
            for (Object param : params) {
                query.setParameter(paramCount, param);
            }

            @SuppressWarnings("unchecked")
            List<Container> containers = query.list();
            for (Container c : containers) {
                ContainerType ct = c.getContainerType();
                try {
                    if (ct.getRowColFromPositionString(childLabel.substring(c.getLabel().length())) != null)
                        filteredContainers.add(c);
                } catch (Exception e) {
                    // can't throw an exception: it means that this label is not
                    // possible in this parent.
                    // Maybe the next one in the list is ok
                }
            }
        }
        return filteredContainers;
    }

    @SuppressWarnings("nls")
    private List<Integer> getPossibleLabelLength(ActionContext context) {
        Query query = context.getSession().createQuery(POS_LABEL_LEN_QRY);

        Object[] minMax = (Object[]) query.list().get(0);
        List<Integer> validLengths = new ArrayList<Integer>();
        for (int i = (Integer) minMax[0]; i < (Integer) minMax[1] + 1; i++) {
            validLengths.add(i);
        }
        return validLengths;
    }

}
