package edu.ualberta.med.biobank.common.action.specimen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveHelper;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class SpecimenAssignSaveAction implements Action<List<Integer>> {

    private static final long serialVersionUID = 1L;

    public static class SpecimenInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public Integer specimenId;
        public RowColPos position;
    }

    public static class ContainerInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public String productBarcode;
        public Integer typeId;
        public String label;
        public RowColPos position;
        public Integer parentId;
        public Integer statusId;
        public Integer siteId;
    }

    private List<SpecimenInfo> specInfos;
    private ContainerInfo containerInfo;
    private Integer containerId;

    public SpecimenAssignSaveAction(ContainerInfo containerInfo,
        List<SpecimenInfo> specInfos) {
        this.specInfos = specInfos;
        this.containerInfo = containerInfo;
    }

    public SpecimenAssignSaveAction(Integer containerId,
        List<SpecimenInfo> specInfos) {
        this.specInfos = specInfos;
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Integer> doAction(Session session) throws ActionException {
        List<Integer> ids = new ArrayList<Integer>();
        if (containerId == null && containerInfo == null)
            throw new ActionException("problem in data sent"); //$NON-NLS-1$
        Container container;
        if (containerId == null) {
            // FIXME action to create a container...
            container = new Container();
            container.setActivityStatus((ActivityStatus) session.get(
                ActivityStatus.class, containerInfo.statusId));
            container.setSite((Site) session.get(Site.class,
                containerInfo.siteId));
            container.setProductBarcode(containerInfo.productBarcode);
            container.setContainerType((ContainerType) session.get(
                ContainerType.class, containerInfo.typeId));
            container.setLabel(containerInfo.label);
            ContainerSaveHelper.setPosition(session, container,
                containerInfo.position, containerInfo.parentId);
            session.save(container);
        } else {
            container = (Container) session.get(Container.class, containerId);
        }

        for (SpecimenInfo si : specInfos) {
            Specimen specimen = (Specimen) session.get(Specimen.class,
                si.specimenId);
            SpecimenActionHelper.setPosition(session, specimen, si.position,
                container);
            session.saveOrUpdate(specimen);
            ids.add(specimen.getId());
        }
        return ids;
    }
}
