package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;

public class StudyGetStudyEventAttrsAction implements
    Action<ListResult<StudyEventAttr>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SELECT_STUDY_EVENT_ATTR_HQL =
        " FROM " + StudyEventAttr.class.getName() + " AS seAttr"
            + " INNER JOIN FETCH seAttr.globalEventAttr geAttr"
            + " INNER JOIN FETCH geAttr.eventAttrType"
            + " WHERE seAttr.study.id=?";

    private final Integer studyId;

    public StudyGetStudyEventAttrsAction(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyGetStudyEventAttrsAction(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public ListResult<StudyEventAttr> run(ActionContext context)
        throws ActionException {
        ArrayList<StudyEventAttr> result = new ArrayList<StudyEventAttr>();

        Query query =
            context.getSession().createQuery(SELECT_STUDY_EVENT_ATTR_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<StudyEventAttr> attrs = query.list();
        if (attrs != null) {
            result.addAll(attrs);
        }

        return new ListResult<StudyEventAttr>(result);
    }

}
