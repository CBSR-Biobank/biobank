package edu.ualberta.med.biobank.common.action.specimen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.User;

public class SpecimenLinkSaveAction implements Action<List<Integer>> {

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

    public SpecimenLinkSaveAction(Integer centerId,
        List<AliquotedSpecimenInfo> asiList) {
        this.centerId = centerId;
        this.aliquotedSpecInfoList = asiList;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Return the number of saved specimen
     */
    @Override
    public List<Integer> doAction(Session session) throws ActionException {
        Center currentCenter = (Center) session.get(Center.class, centerId);
        Date currentDate = new Date();

        OriginInfo originInfo = new OriginInfo();
        originInfo.setCenter(currentCenter);
        session.saveOrUpdate(originInfo);

        List<Integer> specimenIds = new ArrayList<Integer>();
        for (AliquotedSpecimenInfo asi : aliquotedSpecInfoList) {
            // in specimen link, this is always a new specimen
            Specimen specimen = new Specimen();
            specimen.setInventoryId(asi.inventoryId);
            specimen.setCreatedAt(currentDate);
            specimen.setSpecimenType((SpecimenType) session.get(
                SpecimenType.class, asi.typeId));
            specimen.setActivityStatus((ActivityStatus) session.get(
                ActivityStatus.class, asi.statusId));
            specimen.setCurrentCenter(currentCenter);
            specimen.setOriginInfo(originInfo);

            SpecimenActionHelper.setParent(session, specimen,
                asi.parentSpecimenId);
            SpecimenActionHelper.setPosition(session, specimen, asi.position,
                asi.containerId);
            SpecimenActionHelper.setQuantityFromType(specimen);

            session.save(specimen);

            specimenIds.add(specimen.getId());
        }

        return specimenIds;
    }
}
