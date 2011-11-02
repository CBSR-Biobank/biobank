package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class StudyGetStudyEventAttrsAction implements
    Action<ArrayList<StudyEventAttr>> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SELECT_STUDY_EVENT_ATTR_HQL =
        " FROM " + StudyEventAttr.class.getName() + " AS seattr"
        + " INNER JOIN FETCH seattr.eventAttrType"
        + " WHERE seattr.study.id=?";
    // @formatter:on

    private final Integer studyId;

    public StudyGetStudyEventAttrsAction(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyGetStudyEventAttrsAction(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public ArrayList<StudyEventAttr> run(User user, Session session)
        throws ActionException {
        ArrayList<StudyEventAttr> result = new ArrayList<StudyEventAttr>();

        Query query = session.createQuery(SELECT_STUDY_EVENT_ATTR_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<StudyEventAttr> attrs = query.list();
        if (attrs != null) {
            result.addAll(attrs);
        }

        return result;
    }

}
