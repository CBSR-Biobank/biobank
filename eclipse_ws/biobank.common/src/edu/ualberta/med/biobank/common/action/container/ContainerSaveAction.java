package edu.ualberta.med.biobank.common.action.container;

import java.io.Serializable;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class ContainerSaveAction implements Action<Integer> {

    private static final long serialVersionUID = 1L;

    public static class ContainerInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public Integer containerId;
        public Integer statusId;
        public String barcode;
        public String label;
        public Integer siteId;
        public Integer typeId;
        public RowColPos position;
        public Integer parentId;
    }

    private ContainerInfo containerInfo;

    public ContainerSaveAction(ContainerInfo cinfo) {
        this.containerInfo = cinfo;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        // FIXME permissions
        // FIXME loggings
        // FIXME checks
        Container container;
        if (containerInfo.containerId != null) {
            container = ActionUtil.sessionGet(session, Container.class,
                containerInfo.containerId);
        } else {
            container = new Container();
        }
        container.setActivityStatus(ActionUtil.sessionGet(session,
            ActivityStatus.class, containerInfo.statusId));
        container.setSite(ActionUtil.sessionGet(session, Site.class,
            containerInfo.siteId));
        container.setProductBarcode(containerInfo.barcode);
        container.setContainerType(ActionUtil.sessionGet(session,
            ContainerType.class, containerInfo.typeId));
        container.setLabel(containerInfo.label);
        ContainerActionHelper.setPosition(session, container,
            containerInfo.position, containerInfo.parentId);

        session.saveOrUpdate(container);

        return container.getId();
    }

}
