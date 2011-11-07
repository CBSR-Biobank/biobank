package edu.ualberta.med.biobank.common.action.aliquotedspecimen;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class AliquotedSpecimenSaveAction implements Action<Integer> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private Integer quantity;
    private Double volume;
    private Integer studyId;
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

    public void setSpecimenTypeId(Integer specimenTypeId) {
        this.specimenTypeId = specimenTypeId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // FIXME: needs implementation
        return true;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        if (quantity == null) {
            throw new NullPointerException("needOriginalVolume cannot be null");
        }
        if (studyId == null) {
            throw new NullPointerException("study id cannot be null");
        }
        if (specimenTypeId == null) {
            throw new NullPointerException("specimen type id cannot be null");
        }

        SessionUtil sessionUtil = new SessionUtil(session);
        AliquotedSpecimen srcSpc =
            sessionUtil.get(AliquotedSpecimen.class, id,
                new AliquotedSpecimen());
        srcSpc.setQuantity(quantity);
        srcSpc.setVolume(volume);

        Study study = sessionUtil.get(Study.class, studyId, new Study());
        srcSpc.setStudy(study);

        SpecimenType specimenType =
            sessionUtil.get(SpecimenType.class, specimenTypeId,
                new SpecimenType());
        srcSpc.setSpecimenType(specimenType);

        session.saveOrUpdate(srcSpc);
        session.flush();

        return srcSpc.getId();
    }
}
