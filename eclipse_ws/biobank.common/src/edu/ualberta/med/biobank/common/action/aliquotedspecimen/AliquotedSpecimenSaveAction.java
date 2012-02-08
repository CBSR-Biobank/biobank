package edu.ualberta.med.biobank.common.action.aliquotedspecimen;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

public class AliquotedSpecimenSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private Integer quantity;
    private Double volume;
    private Integer studyId;
    private ActivityStatus activityStatus;
    private Integer specimenTypeId;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public void setStudyId(Integer id) {
        this.studyId = id;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public void setSpecimenTypeId(Integer specimenTypeId) {
        this.specimenTypeId = specimenTypeId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // FIXME: needs implementation
        return true;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        if (quantity == null) {
            throw new NullPointerException("needOriginalVolume cannot be null");
        }
        if (studyId == null) {
            throw new NullPointerException("study id cannot be null");
        }
        if (specimenTypeId == null) {
            throw new NullPointerException("specimen type id cannot be null");
        }

        if (activityStatus == null) {
            throw new NullPointerException("activity status not specified");
        }

        AliquotedSpecimen aqSpc =
            context.get(AliquotedSpecimen.class, id, new AliquotedSpecimen());
        aqSpc.setQuantity(quantity);
        aqSpc.setVolume(volume);

        Study study = context.get(Study.class, studyId, new Study());
        aqSpc.setStudy(study);
        aqSpc.setActivityStatus(activityStatus);

        SpecimenType specimenType =
            context.get(SpecimenType.class, specimenTypeId,
                new SpecimenType());
        aqSpc.setSpecimenType(specimenType);

        context.getSession().saveOrUpdate(aqSpc);
        context.getSession().flush();

        return new IdResult(aqSpc.getId());
    }
}
