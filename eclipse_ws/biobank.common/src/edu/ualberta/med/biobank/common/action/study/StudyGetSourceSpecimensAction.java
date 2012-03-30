package edu.ualberta.med.biobank.common.action.study;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.SetResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
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

    @Override
    public SetResult<SourceSpecimen> run(ActionContext context)
        throws ActionException {
        Set<SourceSpecimen> result = new HashSet<SourceSpecimen>();

        Query query = context.getSession().createQuery(SELECT_SOURCE_SPCS_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<SourceSpecimen> srcspcs = query.list();
        if (srcspcs != null) {
            result.addAll(srcspcs);
        }

        return new SetResult<SourceSpecimen>(result);
    }
}
