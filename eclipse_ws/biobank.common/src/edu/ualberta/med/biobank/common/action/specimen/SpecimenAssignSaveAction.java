package edu.ualberta.med.biobank.common.action.specimen;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenAssignSaveAction.SpecimenAssignResInfo;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenAssignPermission;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class SpecimenAssignSaveAction implements Action<SpecimenAssignResInfo> {

    private static final long serialVersionUID = 1L;

    public static class SpecimenInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public Integer specimenId;
        public RowColPos position;
    }

    public static class SpecimenAssignResInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Integer parentContainerId;
        public String parentBarcode;
        public String parentTypeName;
        public String parentLabel;
        public String siteName;

        public List<SpecimenResInfo> specimens;
    }

    public static class SpecimenResInfo implements ActionResult {
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
    private Integer containerId;

    public SpecimenAssignSaveAction(Integer containerId,
        List<SpecimenInfo> specInfos) {
        this.specInfos = specInfos;
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        Container container =
            new ActionContext(user, session).load(
                Container.class, containerId);
        Integer centerId = container.getSite().getId();
        return new SpecimenAssignPermission(centerId).isAllowed(user,
            session);
    }

    @Override
    public SpecimenAssignResInfo run(User user, Session session)
        throws ActionException {
        SpecimenAssignResInfo res = new SpecimenAssignResInfo();
        if (containerId == null && containerId == null)
            throw new ActionException("problem in data sent"); //$NON-NLS-1$
        res.parentContainerId = containerId;

        ActionContext actionContext = new ActionContext(user, session);

        Container container = actionContext.load(Container.class, containerId);
        res.parentBarcode = container.getProductBarcode();
        res.parentTypeName = container.getContainerType().getName();
        res.parentLabel = container.getLabel();
        res.siteName = container.getSite().getNameShort();

        res.specimens =
            new ArrayList<SpecimenAssignSaveAction.SpecimenResInfo>();
        for (SpecimenInfo si : specInfos) {
            Specimen specimen =
                actionContext.load(Specimen.class, si.specimenId);
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
