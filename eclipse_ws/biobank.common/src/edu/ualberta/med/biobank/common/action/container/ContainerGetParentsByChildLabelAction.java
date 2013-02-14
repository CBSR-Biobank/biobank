package edu.ualberta.med.biobank.common.action.container;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

public class ContainerGetParentsByChildLabelAction implements Action<ListResult<Container>> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ContainerGetParentsByChildLabelAction.class
        .getName());

    private final String childLabel;

    private final Integer containerTypeId;

    private final Integer siteId;

    private Session session;

    /**
     * 
     * @param label
     * @param site
     * @param containerType optional
     */
    @SuppressWarnings("nls")
    public ContainerGetParentsByChildLabelAction(String label, Site site,
        ContainerType containerType) {
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

    public ContainerGetParentsByChildLabelAction(String label, Site site) {
        this(label, site, null);
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        boolean result = new ContainerReadPermission(siteId).isAllowed(context);
        log.debug("isAllowed: containerId={} allowed={}", childLabel, result);
        return result;
    }

    @SuppressWarnings("nls")
    @Override
    public ListResult<Container> run(ActionContext context) throws ActionException {
        log.debug("run: containerId={} containerTypeId={}", childLabel, containerTypeId);
        this.session = context.getSession();
        List<Container> containers = getPossibleParents();
        for (Container container : containers) {
            container.getParentContainer().getContainerType().getChildLabelingScheme().getMaxRows();
            container.getChildPositions().size();
            container.getSpecimenPositions().size();
            container.getContainerType().getSpecimenTypes().size();
            for (ContainerType ctype : container.getContainerType().getChildContainerTypes()) {
                ctype.getSpecimenTypes().size();
            }
        }
        return new ListResult<Container>(containers);
    }

    /**
     * Get containers with a given label that can have a child (container or specimen) with label
     * 'childLabel'. If child is not null and is a container, then will check that the parent can
     * contain this type of container
     */
    @SuppressWarnings("nls")
    private List<Container> getPossibleParents() {
        List<Integer> minMaxLengths = getMinMaxLabelLengths();
        if (minMaxLengths.size() != 2) {
            throw new IllegalStateException("invalid size in list");
        }

        List<String> possibleParentLabels = new ArrayList<String>();
        int childLabelLength = childLabel.length();
        for (int i = minMaxLengths.get(0), n = minMaxLengths.get(1); i <= n; ++i) {
            if (i >= childLabelLength) break;
            possibleParentLabels.add(childLabel.substring(0, childLabelLength - i));
        }

        List<Container> filteredContainers = new ArrayList<Container>();
        if (possibleParentLabels.isEmpty()) {
            return filteredContainers;
        }

        Criteria criteria = session.createCriteria(Container.class, "container")
            .add(Restrictions.in("container.label", possibleParentLabels))
            .add(Restrictions.eq("container.site.id", siteId));
        if (containerTypeId != null) {
            criteria.createAlias("container.containerType", "ctype");
            criteria.add(Restrictions.eq("ctype.id", containerTypeId));
        }

        @SuppressWarnings("unchecked")
        List<Container> containers = criteria.list();
        for (Container c : containers) {
            ContainerType ct = c.getContainerType();
            try {
                if (ct.getRowColFromPositionString(childLabel.substring(c.getLabel().length())) != null) {
                    filteredContainers.add(c);
                }
            } catch (Exception e) {
                // an exception means that this label is not possible for this container.
                // Maybe the next one in the list is ok
            }
        }
        return filteredContainers;
    }

    @SuppressWarnings({ "nls", "unchecked" })
    private List<Integer> getMinMaxLabelLengths() {
        List<Object[]> minMax = session.createCriteria(ContainerLabelingScheme.class)
            .setProjection(Projections.projectionList()
                .add(Projections.min("minChars"))
                .add(Projections.max("maxChars"))).list();

        // String POS_LABEL_LEN_QRY =
        // "select min(minChars), max(maxChars) from "
        // + ContainerLabelingScheme.class.getName();
        // List<Object[]> minMax = session.createQuery(POS_LABEL_LEN_QRY).list();

        if (minMax.isEmpty()) {
            throw new IllegalStateException("could not get values from container labeling schemes");
        }

        List<Integer> possibleLengths = new ArrayList<Integer>();
        for (Object obj : minMax.get(0)) {
            possibleLengths.add((Integer) obj);
        }
        return possibleLengths;
    }
}
