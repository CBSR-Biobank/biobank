package edu.ualberta.med.biobank.common.action.study;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
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
        Study study =
            new ActionContext(user, session).load(Study.class, studyId);

        new CollectionIsEmptyCheck<Study>(
            Study.class, study, StudyPeer.PATIENT_COLLECTION,
            study.getNameShort(), null).run(user, session);

        // cascades delete all source specimens, aliquoted specimens and
        // study event attributes

        session.delete(study);
        return new EmptyResult();
    }
}
