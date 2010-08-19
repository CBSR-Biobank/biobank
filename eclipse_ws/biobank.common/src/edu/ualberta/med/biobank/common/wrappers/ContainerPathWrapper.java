package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.model.Container;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerPathWrapper extends ModelWrapper<ContainerPath> {

    private ContainerWrapper container;

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
    protected String[] getPropertyChangeNames() {
        return new String[] { "path", "container" };
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
            propertyChangeSupport.firePropertyChange("path", oldLabel, path);
        }
    }

    public ContainerWrapper getContainer() {
        if (container == null) {
            Container c = wrappedObject.getContainer();
            if (c == null)
                return null;
            container = new ContainerWrapper(appService, c);
        }
        return container;
    }

    protected void setContainer(Container container) {
        if (container == null)
            this.container = null;
        else
            this.container = new ContainerWrapper(appService, container);
        Container oldContainer = wrappedObject.getContainer();
        wrappedObject.setContainer(container);
        propertyChangeSupport.firePropertyChange("container", oldContainer,
            container);
    }

    public void setContainer(ContainerWrapper container) {
        if (container == null) {
            setContainer((Container) null);
        } else {
            setContainer(container.getWrappedObject());
        }
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

    public static ContainerPathWrapper getContainerPath(
        WritableApplicationService appService, ContainerWrapper container)
        throws BiobankCheckException, ApplicationException {
        if (container.isNew())
            return null;

        HQLCriteria criteria = new HQLCriteria("from "
            + ContainerPath.class.getName() + " where container.id = ?",
            Arrays.asList(new Object[] { container.getId() }));
        List<ContainerPath> paths = appService.query(criteria);
        if (paths.size() > 1) {
            throw new BiobankCheckException("container should have only 1 path");
        } else if (paths.size() == 0) {
            return null;
        }
        return new ContainerPathWrapper(appService, paths.get(0));
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        container = null;
    }

}
