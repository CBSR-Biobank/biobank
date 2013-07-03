package edu.ualberta.med.biobank.common.action.study;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.SetResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
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

    @SuppressWarnings("nls")
    private static final String EXCLUDE_ALIQUOT_HQL =
        " AND srce.specimenType.id NOT IN"
            + " (SELECT aliq.specimenType.id"
            + " FROM " + AliquotedSpecimen.class.getName() + " AS aliq "
            + " WHERE aliq.study.id=?)";

    private final Integer studyId;
    private final boolean excludeAliquots;

    public StudyGetSourceSpecimensAction(Integer studyId, boolean excludeAliquots) {
        this.studyId = studyId;
        this.excludeAliquots = excludeAliquots;
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

        Query query;
        if (this.excludeAliquots) {
            query = context.getSession().createQuery(SELECT_SOURCE_SPCS_HQL + EXCLUDE_ALIQUOT_HQL);
        }
        else {
            query = context.getSession().createQuery(SELECT_SOURCE_SPCS_HQL);
        }
        query.setParameter(0, studyId);
        if (this.excludeAliquots) {
            query.setParameter(1, studyId);
        }

        result.addAll(query.list());
        return new SetResult<SourceSpecimen>(result);
    }
}
