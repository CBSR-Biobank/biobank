package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ContainerPathPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerPathBaseWrapper;
import edu.ualberta.med.biobank.model.ContainerPath;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerPathWrapper extends ContainerPathBaseWrapper {

    public ContainerPathWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ContainerPathWrapper(WritableApplicationService appService,
        ContainerPath wrappedObject) {
        super(appService, wrappedObject);
    }

    private void setPath() throws Exception {
        ContainerWrapper container = getContainer();
        if ((container == null) || container.isNew())
            return;

        StringBuffer path = new StringBuffer();
        ContainerWrapper parent = container.getParentContainer();
        if (parent == null) {
            path.append(container.getId());
        } else {
            String parentPath = container.getParentContainer().getPath();
            if (parentPath == null) {
                throw new Exception("parent container does not have a path");
            }
            path.append(parentPath).append("/").append(container.getId());
        }

        setPath(path.toString());
        ContainerWrapper topContainer = container.getTop();
        if (topContainer == null) {
            throw new Exception("no top container");
        }
        setTopContainer(topContainer);
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
