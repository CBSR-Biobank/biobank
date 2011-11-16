package edu.ualberta.med.biobank.common.action.sourcespecimen;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class SourceSpecimenSaveAction implements Action<Integer> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private Boolean needOriginalVolume;
    private Integer studyId;
    private Integer specimenTypeId;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNeedOriginalVolume(Boolean needOriginalVolume) {
        this.needOriginalVolume = needOriginalVolume;
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
        if (needOriginalVolume == null) {
            throw new NullPointerException("needOriginalVolume cannot be null");
        }
        if (studyId == null) {
            throw new NullPointerException("study id cannot be null");
        }
        if (specimenTypeId == null) {
            throw new NullPointerException("specimen type id cannot be null");
        }

        SessionUtil sessionUtil = new SessionUtil(session);
        SourceSpecimen srcSpc =
            sessionUtil.get(SourceSpecimen.class, id, new SourceSpecimen());
        srcSpc.setNeedOriginalVolume(needOriginalVolume);

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
