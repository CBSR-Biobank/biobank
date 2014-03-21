package edu.ualberta.med.biobank.common.action.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.model.Entity;

public class EntitiesGetAction implements Action<ListResult<Entity>> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ReportsPermission().isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<Entity> run(ActionContext context) throws ActionException {
        List<Entity> entities = context.getSession().createCriteria(Entity.class).list();
        return new ListResult<Entity>(entities);
    }

}
