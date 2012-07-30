package edu.ualberta.med.biobank.action.specimen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.specimen.SpecimenLinkSaveAction.AliquotedSpecimenResInfo;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.permission.specimen.SpecimenLinkPermission;

public class SpecimenLinkSaveAction implements
    Action<ListResult<AliquotedSpecimenResInfo>> {

    private static final long serialVersionUID = 1L;

    private final Integer centerId;

    private final Collection<AliquotedSpecimenInfo> aliquotedSpecInfoList;

    private final Integer studyId;

    /**
     * The fields containerId and position can be null.
     * 
     */
    public static class AliquotedSpecimenInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public String inventoryId;
        public Integer typeId;
        public ActivityStatus activityStatus;
        public Integer parentSpecimenId;
        public Integer containerId;
        public RowColPos position;
    }

    public static class AliquotedSpecimenResInfo implements ActionResult {
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

    public SpecimenLinkSaveAction(Integer centerId, Integer studyId,
        Collection<AliquotedSpecimenInfo> asiList) {
        this.centerId = centerId;
        this.studyId = studyId;
        this.aliquotedSpecInfoList = asiList;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new SpecimenLinkPermission(centerId, studyId).isAllowed(context);
    }

    /**
     * Return the number of saved specimen
     */
    @Override
    public ListResult<AliquotedSpecimenResInfo> run(ActionContext context)
        throws ActionException {
        Center currentCenter = context.load(Center.class, centerId);
        Date currentDate = new Date();

        // FIXME Checks needed before saving the specimens? (see specimen
        // persist checks)

        OriginInfo originInfo = new OriginInfo();
        originInfo.setCenter(currentCenter);
        context.getSession().saveOrUpdate(originInfo);

        ArrayList<AliquotedSpecimenResInfo> resList =
            new ArrayList<AliquotedSpecimenResInfo>();
        for (AliquotedSpecimenInfo asi : aliquotedSpecInfoList) {
            // in specimen link, this is always a new specimen
            Specimen specimen = new Specimen();
            specimen.setInventoryId(asi.inventoryId);
            specimen.setTimeCreated(currentDate);
            specimen.setSpecimenType(context.load(SpecimenType.class,
                asi.typeId));
            specimen.setActivityStatus(asi.activityStatus);
            specimen.setCurrentCenter(currentCenter);
            specimen.setOriginInfo(originInfo);

            Specimen parentSpc =
                context.load(Specimen.class, asi.parentSpecimenId);

            SpecimenActionHelper.setParent(specimen, parentSpc);
            SpecimenActionHelper.setQuantityFromType(specimen);
            SpecimenActionHelper.setPosition(context, specimen, asi.position,
                asi.containerId);

            context.getSession().save(specimen);

            AliquotedSpecimenResInfo res = new AliquotedSpecimenResInfo();
            res.id = specimen.getId();
            res.inventoryId = specimen.getInventoryId();
            res.typeName = specimen.getSpecimenType().getName();
            res.parentTypeName = specimen.getParentSpecimen().getSpecimenType()
                .getName();
            res.parentInventoryId = specimen.getParentSpecimen()
                .getInventoryId();
            res.patientPNumber = specimen.getParentSpecimen()
                .getCollectionEvent().getPatient().getPnumber();
            res.visitNumber = specimen.getParentSpecimen().getCollectionEvent()
                .getVisitNumber().toString();
            res.currentCenterName = specimen.getCurrentCenter().getName();
            res.position = SpecimenActionHelper.getPositionString(specimen,
                true, false);
            resList.add(res);
        }

        return new ListResult<AliquotedSpecimenResInfo>(resList);
    }
}
