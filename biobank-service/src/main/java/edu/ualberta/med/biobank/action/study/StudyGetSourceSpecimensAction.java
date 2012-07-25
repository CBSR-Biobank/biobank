package edu.ualberta.med.biobank.action.study;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.SetResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.SourceSpecimen;

public class StudyGetSourceSpecimensAction implements
    Action<SetResult<SourceSpecimen>> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SELECT_SOURCE_SPCS_HQL =
        " FROM " + SourceSpecimen.class.getName() + " AS srce"
            + " INNER JOIN FETCH srce.specimenType specimenType"
            + " LEFT JOIN FETCH specimenType.childSpecimenTypes"
            + " WHERE srce.study.id=?";

    private final Integer studyId;

    public StudyGetSourceSpecimensAction(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SetResult<SourceSpecimen> run(ActionContext context)
        throws ActionException {
        Set<SourceSpecimen> result = new HashSet<SourceSpecimen>();

        Query query = context.getSession().createQuery(SELECT_SOURCE_SPCS_HQL);
        query.setParameter(0, studyId);

        result.addAll(query.list());
        return new SetResult<SourceSpecimen>(result);
    }
}
