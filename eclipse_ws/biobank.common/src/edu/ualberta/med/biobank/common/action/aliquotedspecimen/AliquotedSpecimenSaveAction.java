package edu.ualberta.med.biobank.common.action.aliquotedspecimen;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class AliquotedSpecimenSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private Integer quantity;
    private Double volume;
    private Integer studyId;
    private Integer aStatusId;
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

    public void setActivityStatusId(Integer activityStatusId) {
        this.aStatusId = activityStatusId;
    }

    public void setSpecimenTypeId(Integer specimenTypeId) {
        this.specimenTypeId = specimenTypeId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // FIXME: needs implementation
        return true;
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        if (quantity == null) {
            throw new NullPointerException("needOriginalVolume cannot be null");
        }
        if (studyId == null) {
            throw new NullPointerException("study id cannot be null");
        }
        if (specimenTypeId == null) {
            throw new NullPointerException("specimen type id cannot be null");
        }

        if (aStatusId == null) {
            throw new NullPointerException("activity status not specified");
        }

        SessionUtil sessionUtil = new SessionUtil(session);
        AliquotedSpecimen aqSpc =
            sessionUtil.get(AliquotedSpecimen.class, id,
                new AliquotedSpecimen());
        aqSpc.setQuantity(quantity);
        aqSpc.setVolume(volume);

        Study study = sessionUtil.get(Study.class, studyId, new Study());
        aqSpc.setStudy(study);

        ActivityStatus aStatus =
            sessionUtil.get(ActivityStatus.class, aStatusId);
        aqSpc.setActivityStatus(aStatus);

        SpecimenType specimenType =
            sessionUtil.get(SpecimenType.class, specimenTypeId,
                new SpecimenType());
        aqSpc.setSpecimenType(specimenType);

        session.saveOrUpdate(aqSpc);
        session.flush();

        return new IdResult(aqSpc.getId());
    }
}
