package edu.ualberta.med.biobank.common.action.specimen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenAssignSaveAction.SpecimenAssignResInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class SpecimenAssignSaveAction implements Action<SpecimenAssignResInfo> {

    private static final long serialVersionUID = 1L;

    public static class SpecimenInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public Integer specimenId;
        public RowColPos position;
    }

    public static class SpecimenAssignResInfo implements Serializable,
        NotAProxy {
        private static final long serialVersionUID = 1L;

        public Integer parentContainerId;
        public String parentBarcode;
        public String parentTypeName;
        public String parentLabel;
        public String siteName;

        public List<SpecimenResInfo> specimens;
    }

    public static class SpecimenResInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public Integer specimenId;
        public String position;
        public String inventoryId;
        public String typeName;
        public String patientPNumber;
        public String visitNumber;
        public String centerName;
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
        return true;
    }

    @Override
    public SpecimenAssignResInfo run(User user, Session session)
        throws ActionException {
        SpecimenAssignResInfo res = new SpecimenAssignResInfo();
        if (containerId == null && containerInfo == null)
            throw new ActionException("problem in data sent"); //$NON-NLS-1$
        if (containerId == null) {
            containerId = new ContainerSaveAction(containerInfo).run(user,
                session);
        }
        res.parentContainerId = containerId;
        Container container = ActionUtil.sessionGet(session, Container.class,
            containerId);
        res.parentBarcode = container.getProductBarcode();
        res.parentTypeName = container.getContainerType().getName();
        res.parentLabel = container.getLabel();
        res.siteName = container.getSite().getNameShort();

        res.specimens = new ArrayList<SpecimenAssignSaveAction.SpecimenResInfo>();
        for (SpecimenInfo si : specInfos) {
            Specimen specimen = ActionUtil.sessionGet(session, Specimen.class,
                si.specimenId);
            SpecimenActionHelper.setPosition(session, specimen, si.position,
                containerId);
            session.saveOrUpdate(specimen);

            SpecimenResInfo rInfo = new SpecimenResInfo();
            rInfo.specimenId = specimen.getId();
            rInfo.position = SpecimenActionHelper.getPositionString(specimen,
                true, false);
            rInfo.inventoryId = specimen.getInventoryId();
            rInfo.typeName = specimen.getSpecimenType().getName();
            rInfo.patientPNumber = specimen.getCollectionEvent().getPatient()
                .getPnumber();
            rInfo.visitNumber = specimen.getCollectionEvent().getVisitNumber()
                .toString();
            rInfo.centerName = specimen.getCurrentCenter().getNameShort();
            res.specimens.add(rInfo);
        }

        return res;
    }
}
