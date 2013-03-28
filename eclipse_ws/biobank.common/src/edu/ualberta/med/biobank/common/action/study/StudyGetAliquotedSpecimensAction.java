package edu.ualberta.med.biobank.common.action.study;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.SetResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Study;

public class StudyGetAliquotedSpecimensAction implements
    Action<SetResult<AliquotedSpecimen>> {
    private static final long serialVersionUID = 1L;

    private final Integer studyId;

    private final ActivityStatus activityStatus;

    public StudyGetAliquotedSpecimensAction(Integer studyId) {
        this.studyId = studyId;
        this.activityStatus = null;
    }

    public StudyGetAliquotedSpecimensAction(Integer studyId, ActivityStatus activityStatus) {
        this.studyId = studyId;
        this.activityStatus = activityStatus;
    }

    public StudyGetAliquotedSpecimensAction(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public SetResult<AliquotedSpecimen> run(ActionContext context)
        throws ActionException {
        Set<AliquotedSpecimen> result = new HashSet<AliquotedSpecimen>();

        Criteria criteria = context.getSession().createCriteria(AliquotedSpecimen.class);
        criteria.add(Restrictions.eq("study.id", studyId));

        if (activityStatus != null) {
            criteria.add(Restrictions.eq("activityStatus", activityStatus));
        }

        @SuppressWarnings("unchecked")
        List<AliquotedSpecimen> aliquotedSpecimens = criteria.list();
        for (AliquotedSpecimen aqspc : aliquotedSpecimens) {
            result.add(aqspc);
            aqspc.getSpecimenType().getName();
        }

        return new SetResult<AliquotedSpecimen>(result);
    }

}
