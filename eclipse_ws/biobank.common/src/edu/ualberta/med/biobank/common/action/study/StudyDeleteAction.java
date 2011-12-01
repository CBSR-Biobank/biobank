package edu.ualberta.med.biobank.common.action.study;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.study.StudyDeletePermission;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
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

    private static final String PATIENT_COUNT_HQL =
        "SELECT COUNT(patients) FROM " + Study.class.getName()
            + " s INNER JOIN s.patientCollection patients"
            + " WHERE s.id=?"; //$NON-NLS-1$

    @Override
    public EmptyResult run(User user, Session session) throws ActionException {
        Study study = ActionUtil.sessionGet(session, Study.class, studyId);
        // TODO: checks
        // FIXME permissions

        // if study has patients then do not allow delete

        Query query = session.createQuery(PATIENT_COUNT_HQL);
        query.setParameter(0, studyId);
        if (!HibernateUtil.getCountFromQuery(query).equals(0L)) {
            throw new ActionCheckException("cannot delete: has patients");
        }

        // cascades delete all source specimens, aliquoted specimens and
        // study event attributes

        session.delete(study);
        return new EmptyResult();
    }
}
