package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.Collection;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.LoadModelAction;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class ContainerPersistChecks extends LoadModelAction<Container> {
    private static final long serialVersionUID = 1L;

    private static final String POSITION_NOT_ALLOWED_MSG = "Container {0} is a top-level container and is not allowed to have a parent or position.";
    private static final String MISSING_PARENT_MSG = "Container {0} does not have a parent container.";
    private static final String BAD_CONTAINER_TYPE_MSG = "Container {0} does not allow inserts of container type {1}.";
    private static final String MISSING_POSITION_MSG = "Child container {0} must have a position.";
    private static final String WRONG_PARENT_SITE_MSG = "Container {0} has a different site than its parent container. The sites must be the same.";
    private static final String WRONG_CONTAINER_TYPE_SITE_MSG = "Container {0} has a different site than its container type's site. The sites be the same.";

    public ContainerPersistChecks(ModelWrapper<Container> wrapper) {
        super(wrapper);
    }

    @Override
    public void doLoadModelAction(Session session, Container container)
        throws BiobankSessionException {
        checkParent(container);
        checkContainerType(container);
        checkHasPosition(container);
        checkSite(container);
    }

    private static boolean isTopLevel(Container container) {
        return container.getContainerType() != null
            && Boolean.TRUE.equals(container.getContainerType().getTopLevel());
    }

    private static boolean hasPosition(Container container) {
        return container.getPosition() != null;
    }

    private static Container getParent(Container container) {
        return hasPosition(container) ? container.getPosition()
            .getParentContainer() : null;
    }

    private static boolean hasParent(Container container) {
        return getParent(container) != null;
    }

    private void checkParent(Container container)
        throws BiobankSessionException {
        boolean hasPosition = hasPosition(container);
        boolean hasParent = hasParent(container);
        boolean isTopLevel = isTopLevel(container);

        if (isTopLevel && (hasPosition || hasParent)) {
            String label = container.getLabel();
            String msg = MessageFormat.format(POSITION_NOT_ALLOWED_MSG, label);
            throw new BiobankSessionException(msg);
        }

        if (!hasParent && !isTopLevel) {
            String label = container.getLabel();
            String msg = MessageFormat.format(MISSING_PARENT_MSG, label);
            throw new BiobankSessionException(msg);
        }
    }

    private void checkContainerType(Container container)
        throws BiobankSessionException {
        ContainerType containerType = container.getContainerType();
        Container parent = getParent(container);

        if (containerType != null && parent != null) {
            ContainerType parentType = parent.getContainerType();
            Collection<ContainerType> legalTypes = parentType
                .getChildContainerTypeCollection();

            if (legalTypes == null || !legalTypes.contains(containerType)) {
                String parentLabel = parent.getLabel();
                String containerTypeNameShort = containerType.getNameShort();
                String msg = MessageFormat.format(BAD_CONTAINER_TYPE_MSG,
                    parentLabel, containerTypeNameShort);

                throw new BiobankSessionException(msg);
            }
        }

        if (containerType != null) {
            Site containerSite = container.getSite();
            Site containerTypeSite = containerType.getSite();

            if (containerSite == null
                || !containerSite.equals(containerTypeSite)) {
                String label = container.getLabel();
                String msg = MessageFormat.format(
                    WRONG_CONTAINER_TYPE_SITE_MSG, label);
                throw new BiobankSessionException(msg);
            }
        }
    }

    private void checkHasPosition(Container container)
        throws BiobankSessionException {
        boolean isTopLevel = isTopLevel(container);
        boolean hasPosition = hasPosition(container);

        if (!isTopLevel && !hasPosition) {
            String label = container.getLabel();
            String msg = MessageFormat.format(MISSING_POSITION_MSG, label);
            throw new BiobankSessionException(msg);
        }
    }

    private void checkSite(Container container) throws BiobankSessionException {
        if (hasParent(container)) {
            Site parentSite = getParent(container).getSite();
            Site site = container.getSite();

            if (parentSite == null || !parentSite.equals(site)) {
                String label = container.getLabel();
                String msg = MessageFormat.format(WRONG_PARENT_SITE_MSG, label);
                throw new BiobankSessionException(msg);
            }
        }

    }
}
