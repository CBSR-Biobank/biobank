package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
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
        // TODO Auto-generated method stub

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
                path = container.getParent().getPath() + "/"
                    + container.getId();
            }

            wrappedObject.setPath(path);
            propertyChangeSupport.firePropertyChange("path", oldLabel, path);
        }
    }

    public ContainerWrapper getContainer() {
        Container container = wrappedObject.getContainer();
        if (container == null) {
            return null;
        }
        return new ContainerWrapper(appService, container);
    }

    public void setContainer(Container container) {
        Container oldContainer = wrappedObject.getContainer();
        wrappedObject.setContainer(container);
        propertyChangeSupport.firePropertyChange("container", oldContainer,
            container);
    }

    public void setContainer(ContainerWrapper site) {
        if (site == null) {
            setContainer((Container) null);
        } else {
            setContainer(site.getWrappedObject());
        }
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        ContainerWrapper container = getContainer();
        if (container == null) {
            throw new BiobankCheckException("container is null");
        }

        // if (container.isNew()) {
        // throw new BiobankCheckException("container is not in database");
        // }
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
        throws Exception {
        if (container.isNew())
            return null;

        HQLCriteria criteria = new HQLCriteria(ContainerPath.class.getName()
            + " where container = ?", Arrays.asList(new Object[] { container }));
        List<ContainerPath> paths = appService.query(criteria);
        if (paths.size() > 1) {
            throw new Exception("container should have only 1 path");
        } else if (paths.size() == 0) {
            return null;
        }
        return new ContainerPathWrapper(appService, paths.get(0));
    }

}
