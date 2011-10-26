package edu.ualberta.med.biobank.common.action.specimen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenResInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.User;

public class SpecimenLinkSaveAction implements
    Action<ArrayList<AliquotedSpecimenResInfo>> {

    private static final long serialVersionUID = 1L;

    private Integer centerId;

    private List<AliquotedSpecimenInfo> aliquotedSpecInfoList;

    public static class AliquotedSpecimenInfo implements Serializable,
        NotAProxy {
        private static final long serialVersionUID = 1L;
        public String inventoryId;
        public Integer typeId;
        public Integer statusId;
        public Integer parentSpecimenId;
        public Integer containerId;
        public RowColPos position;
    }

    public static class AliquotedSpecimenResInfo implements Serializable,
        NotAProxy {
        private static final long serialVersionUID = 1L;
        public Integer id;
        public String inventoryId;
        public String typeName;
        public String parentTypeName;
        public String parentInventoryId;
        public String patientPNumber;
        public String visitNumber;
        public String currentCenterName;
        public String position;
    }

    public SpecimenLinkSaveAction(Integer centerId,
        List<AliquotedSpecimenInfo> asiList) {
        this.centerId = centerId;
        this.aliquotedSpecInfoList = asiList;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * Return the number of saved specimen
     */
    @Override
    public ArrayList<AliquotedSpecimenResInfo> run(User user, Session session)
        throws ActionException {
        Center currentCenter = ActionUtil.sessionGet(session, Center.class,
            centerId);
        Date currentDate = new Date();

        // FIXME permissions?
        // FIXME Checks needed before saving the specimens? (see specimen
        // persist checks)

        OriginInfo originInfo = new OriginInfo();
        originInfo.setCenter(currentCenter);
        session.saveOrUpdate(originInfo);

        ArrayList<AliquotedSpecimenResInfo> resList = new ArrayList<SpecimenLinkSaveAction.AliquotedSpecimenResInfo>();
        for (AliquotedSpecimenInfo asi : aliquotedSpecInfoList) {
            // in specimen link, this is always a new specimen
            Specimen specimen = new Specimen();
            specimen.setInventoryId(asi.inventoryId);
            specimen.setCreatedAt(currentDate);
            specimen.setSpecimenType(ActionUtil.sessionGet(session,
                SpecimenType.class, asi.typeId));
            specimen.setActivityStatus(ActionUtil.sessionGet(session,
                ActivityStatus.class, asi.statusId));
            specimen.setCurrentCenter(currentCenter);
            specimen.setOriginInfo(originInfo);

            SpecimenActionHelper.setParent(session, specimen,
                asi.parentSpecimenId);
            SpecimenActionHelper.setQuantityFromType(specimen);
            SpecimenActionHelper.setPosition(session, specimen, asi.position,
                asi.containerId);

            session.save(specimen);

            AliquotedSpecimenResInfo res = new AliquotedSpecimenResInfo();
            res.id = specimen.getId();
            res.inventoryId = specimen.getInventoryId();
            res.typeName = specimen.getSpecimenType().getName();
            res.parentTypeName = specimen.getParentSpecimen().getSpecimenType()
                .getNameShort();
            res.parentInventoryId = specimen.getParentSpecimen()
                .getInventoryId();
            res.patientPNumber = specimen.getParentSpecimen()
                .getCollectionEvent().getPatient().getPnumber();
            res.visitNumber = specimen.getParentSpecimen().getCollectionEvent()
                .getVisitNumber().toString();
            res.currentCenterName = specimen.getCurrentCenter().getNameShort();
            res.position = SpecimenActionHelper.getPositionString(specimen,
                true, false);
            resList.add(res);
        }

        return resList;
    }
}
