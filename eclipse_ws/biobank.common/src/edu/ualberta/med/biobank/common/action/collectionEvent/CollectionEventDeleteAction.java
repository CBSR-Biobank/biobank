package edu.ualberta.med.biobank.common.action.collectionEvent;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventDeletePermission;
import edu.ualberta.med.biobank.model.CollectionEvent;

public class CollectionEventDeleteAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer ceventId;

    public CollectionEventDeleteAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new CollectionEventDeletePermission(ceventId).isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        CollectionEvent cevent = context.load(CollectionEvent.class, ceventId);

        context.getSession().delete(cevent);

        return new IdResult(ceventId);
    }

}
