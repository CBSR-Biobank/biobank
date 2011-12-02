package edu.ualberta.med.biobank.common.action.study;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.study.StudyDeletePermission;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudyDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer studyId = null;

    public StudyDeleteAction(Integer id) {
        this.studyId = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new StudyDeletePermission(studyId).isAllowed(user, session);
    }

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        Study study = ActionUtil.sessionGet(session, Study.class, studyId);
        new StudyPreDeleteChecks(study).run(user, session);

        // cascades delete all source specimens, aliquoted specimens and
        // study event attributes

        session.delete(study);
        return new EmptyResult();
    }
}
