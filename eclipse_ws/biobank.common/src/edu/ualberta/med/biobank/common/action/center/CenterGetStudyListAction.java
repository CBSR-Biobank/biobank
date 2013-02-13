package edu.ualberta.med.biobank.common.action.center;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Study;

public class CenterGetStudyListAction implements Action<ListResult<Study>> {
    private static final long serialVersionUID = 1L;

    private final Integer centerId;

    public CenterGetStudyListAction(Center center) {
        if (center == null) {
            throw new IllegalArgumentException();
        }
        this.centerId = center.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public ListResult<Study> run(ActionContext context) throws ActionException {
        Center center = (Center) context.getSession().createCriteria(Center.class)
            .add(Restrictions.eq("id", centerId)).uniqueResult();

        if (center == null) {
            throw new ActionException("center with id does not exist: " + centerId);
        }

        return new ListResult<Study>(center.getStudiesInternal());
    }
}
