package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudyGetAliquotedSpecimensAction implements
    Action<ListResult<AliquotedSpecimen>> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SELECT_ALIQUOTED_SPCS_HQL = 
    "SELECT srcspc"
    + " FROM " + AliquotedSpecimen.class.getName() + " srcspc"
    + " INNER JOIN FETCH srcspc.specimenType"
    + " INNER JOIN FETCH srcspc.activityStatus"
    + " WHERE srcspc.study.id = ?";
    // @formatter:on

    private final Integer studyId;

    public StudyGetAliquotedSpecimensAction(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyGetAliquotedSpecimensAction(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public ListResult<AliquotedSpecimen> run(User user, Session session)
        throws ActionException {
        ArrayList<AliquotedSpecimen> result =
            new ArrayList<AliquotedSpecimen>();

        Query query = session.createQuery(SELECT_ALIQUOTED_SPCS_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<AliquotedSpecimen> aliquotedSpecimens = query.list();
        if (aliquotedSpecimens != null) {
            result.addAll(aliquotedSpecimens);
        }

        return new ListResult<AliquotedSpecimen>(result);
    }

}
