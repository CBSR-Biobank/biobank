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
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.container.ContainerGetContainerOrParentsByLabelAction.ContainerData;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

public class ContainerGetContainerOrParentsByLabelAction implements Action<ContainerData> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ContainerGetContainerOrParentsByLabelAction.class
        .getName());

    private final String label;

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
    public ContainerGetContainerOrParentsByLabelAction(String label, Site site,
        ContainerType containerType) {
        log.debug("containerId={}", label);
        if (label == null) {
            throw new IllegalArgumentException();
        }
        if (site == null) {
            throw new IllegalArgumentException();
        }
        this.label = label;
        this.siteId = site.getId();

        if (containerType != null) {
            this.containerTypeId = containerType.getId();
        } else {
            this.containerTypeId = null;
        }
    }

    public ContainerGetContainerOrParentsByLabelAction(String label, Site site) {
        this(label, site, null);
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        boolean result = new ContainerReadPermission(siteId).isAllowed(context);
        log.debug("isAllowed: containerId={} allowed={}", label, result);
        return result;
    }

    @SuppressWarnings({ "nls", "unchecked" })
    @Override
    public ContainerData run(ActionContext context) throws ActionException {
        log.debug("run: containerId={} containerTypeId={}", label, containerTypeId);
        this.session = context.getSession();

        // first check if the label is for a top level container
        List<Container> containers = session.createCriteria(Container.class)
            .add(Restrictions.eq("label", label))
            .add(Restrictions.eq("site.id", siteId)).list();

        boolean isTopLevel = !containers.isEmpty();

        if (!isTopLevel) {
            // label was not for top level container, find possible parent containers
            containers = getPossibleParents();
        }

        for (Container container : containers) {
            if (!isTopLevel) {
                Container parentContainer = container.getParentContainer();
                if (parentContainer != null) {
                    parentContainer.getContainerType().getChildLabelingScheme().getMaxRows();
                }
            }
            container.getChildPositions().size();
            container.getSpecimenPositions().size();
            container.getContainerType().getSpecimenTypes().size();
            for (ContainerType ctype : container.getContainerType().getChildContainerTypes()) {
                ctype.getSpecimenTypes().size();
            }
        }

        ContainerData containerData;

        if (isTopLevel) {
            containerData = new ContainerData(containers.get(0));
        } else {
            containerData = new ContainerData(containers);
        }

        return containerData;
    }

    /**
     * Get containers with a given label that can have a child (container or specimen) with label
     * 'childLabel'. If child is not null and is a container, then will check that the parent can
     * contain this type of container
     */
    @SuppressWarnings("nls")
    private List<Container> getPossibleParents() {
        MinMax minMax = getMinMaxLabelLengths();

        List<String> possibleParentLabels = new ArrayList<String>();
        int childLabelLength = label.length();
        for (int i = minMax.getMin(), n = minMax.getMax(); i <= n; ++i) {
            if (i >= childLabelLength) break;
            possibleParentLabels.add(label.substring(0, childLabelLength - i));
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
                if (ct.getRowColFromPositionString(label.substring(c.getLabel().length())) != null) {
                    filteredContainers.add(c);
                }
            } catch (Exception e) {
                // an exception means that this label is not possible for this container.
                // Maybe the next one in the list is ok
            }
        }
        return filteredContainers;
    }

    @SuppressWarnings("nls")
    private MinMax getMinMaxLabelLengths() {
        Object[] minMaxArr = (Object[]) session.createCriteria(ContainerLabelingScheme.class)
            .setProjection(Projections.projectionList()
                .add(Projections.min("minChars"))
                .add(Projections.max("maxChars"))).uniqueResult();

        if (minMaxArr == null) {
            throw new IllegalStateException("could not get values from container labeling schemes");
        }

        if (minMaxArr.length != 2) {
            throw new IllegalStateException("query returned wrong number of results");
        }
        return new MinMax((Integer) minMaxArr[0], (Integer) minMaxArr[1]);
    }

    private static class MinMax {
        private final Integer min;
        private final Integer max;

        public MinMax(Integer min, Integer max) {
            this.min = min;
            this.max = max;
        }

        public Integer getMin() {
            return min;
        }

        public Integer getMax() {
            return max;
        }
    }

    public static class ContainerData implements ActionResult {
        private static final long serialVersionUID = 1L;

        private final Container container;
        private final List<Container> possibleParentContainers;

        public ContainerData(Container container) {
            this.container = container;
            this.possibleParentContainers = null;
        }

        public ContainerData(List<Container> possibleParents) {
            this.container = null;
            this.possibleParentContainers = possibleParents;
        }

        public Container getContainer() {
            return container;
        }

        public List<Container> getPossibleParentContainers() {
            return possibleParentContainers;
        }

    }
}
