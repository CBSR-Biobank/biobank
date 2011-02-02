package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ContainerPathPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPath;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerPathWrapper extends ModelWrapper<ContainerPath> {

    public ContainerPathWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ContainerPathWrapper(WritableApplicationService appService,
        ContainerPath wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ContainerPathPeer.PROP_NAMES;
    }

    @Override
    public Class<ContainerPath> getWrappedClass() {
        return ContainerPath.class;
    }

    public String getPath() {
        return wrappedObject.getPath();
    }

    private void setPath() throws Exception {
        String oldLabel = getPath();
        ContainerWrapper container = getContainer();
        if ((container != null) && !container.isNew()) {
            String path;
            ContainerWrapper parent = container.getParent();
            if (parent == null) {
                path = "" + container.getId();
            } else {
                String parentPath = container.getParent().getPath();
                if (parentPath == null) {
                    throw new Exception("parent container does not have a path");
                }
                path = parentPath + "/" + container.getId();
            }

            wrappedObject.setPath(path);

            ContainerWrapper topContainer = container.getTop();
            if (topContainer == null) {
                throw new Exception("no top container");
            }

            wrappedObject.setTopContainer(topContainer.getWrappedObject());

            propertyChangeSupport.firePropertyChange("path", oldLabel, path);
        }
    }

    public ContainerWrapper getContainer() {
        ContainerWrapper container = (ContainerWrapper) propertiesMap
            .get("container");
        if (container == null) {
            Container c = wrappedObject.getContainer();
            if (c == null)
                return null;
            container = new ContainerWrapper(appService, c);
            propertiesMap.put("container", container);
        }
        return container;
    }

    public void setContainer(ContainerWrapper container) {
        propertiesMap.put("container", container);
        Container oldContainer = wrappedObject.getContainer();
        Container newContainer = null;
        if (container != null) {
            newContainer = container.getWrappedObject();
        }
        wrappedObject.setContainer(newContainer);
        propertyChangeSupport.firePropertyChange("container", oldContainer,
            newContainer);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        ContainerWrapper container = getContainer();
        if (container == null) {
            throw new BiobankCheckException("container is null");
        }
        if (container.isNew()) {
            throw new BiobankCheckException("container is not in database");
        }

        ContainerPathWrapper path = getContainerPath(appService, container);
        if ((path != null) && (isNew() || !getId().equals(path.getId()))) {
            throw new BiobankCheckException("path already in database");
        }
    }

    @Override
    public void persist() throws Exception {
        setPath();
        super.persist();
    }

    @Override
    public int compareTo(ModelWrapper<ContainerPath> arg0) {
        return 0;
    }

    @Override
    public String toString() {
        return getPath();
    }

    private static final String CONTAINER_PATH_QRY = "from "
        + ContainerPath.class.getName() + " where "
        + Property.concatNames(ContainerPathPeer.CONTAINER, ContainerPeer.ID)
        + "=?";

    public static ContainerPathWrapper getContainerPath(
        WritableApplicationService appService, ContainerWrapper container)
        throws BiobankCheckException, ApplicationException {
        if (container.isNew())
            return null;

        HQLCriteria criteria = new HQLCriteria(CONTAINER_PATH_QRY,
            Arrays.asList(new Object[] { container.getId() }));
        List<ContainerPath> paths = appService.query(criteria);
        if (paths.size() > 1) {
            throw new BiobankCheckException("container should have only 1 path");
        } else if (paths.size() == 0) {
            return null;
        }
        return new ContainerPathWrapper(appService, paths.get(0));
    }

}
