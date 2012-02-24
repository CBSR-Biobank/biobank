package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Study;

public class StudyGetAliquotedSpecimensAction implements
    Action<ListResult<AliquotedSpecimen>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SELECT_ALIQUOTED_SPCS_HQL =
        "SELECT srcspc"
            + " FROM " + AliquotedSpecimen.class.getName() + " srcspc"
            + " INNER JOIN FETCH srcspc.specimenType specimenType"
            // + " LEFT JOIN FETCH specimenType.childSpecimenTypeCollection"
            + " WHERE srcspc.study.id = ?";

    private final Integer studyId;

    public StudyGetAliquotedSpecimensAction(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyGetAliquotedSpecimensAction(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public ListResult<AliquotedSpecimen> run(ActionContext context)
        throws ActionException {
        ArrayList<AliquotedSpecimen> result =
            new ArrayList<AliquotedSpecimen>();

        Query query =
            context.getSession().createQuery(SELECT_ALIQUOTED_SPCS_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<AliquotedSpecimen> aliquotedSpecimens = query.list();
        if (aliquotedSpecimens != null) {
            result.addAll(aliquotedSpecimens);
        }

        return new ListResult<AliquotedSpecimen>(result);
    }

}
