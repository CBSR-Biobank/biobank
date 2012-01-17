package edu.ualberta.med.biobank.common.action.center;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.check.CollectionIsEmptyCheck;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.model.Center;

public abstract class CenterDeleteAction implements Action<EmptyResult> {
    private static final long serialVersionUID = 1L;

    protected Integer centerId = null;

    public CenterDeleteAction(Integer id) {
        this.centerId = id;
    }

    public EmptyResult run(ActionContext context, Center center)
        throws ActionException {
        new CollectionIsEmptyCheck<Center>(
            Center.class, center, CenterPeer.SRC_DISPATCH_COLLECTION,
            center.getNameShort(), null).run(context);

        new CollectionIsEmptyCheck<Center>(
            Center.class, center, CenterPeer.DST_DISPATCH_COLLECTION,
            center.getNameShort(), null).run(context);

        context.getSession().delete(center);
        return new EmptyResult();
    }
}
