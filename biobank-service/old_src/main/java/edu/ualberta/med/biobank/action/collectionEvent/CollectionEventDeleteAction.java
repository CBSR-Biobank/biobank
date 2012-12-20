package edu.ualberta.med.biobank.action.collectionEvent;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.collectionEvent.CollectionEventDeletePermission;
import edu.ualberta.med.biobank.model.study.CollectionEvent;

public class CollectionEventDeleteAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private final Integer ceventId;

    public CollectionEventDeleteAction(CollectionEvent cevent) {
        if (cevent == null) {
            throw new IllegalArgumentException();
        }
        this.ceventId = cevent.getId();
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
