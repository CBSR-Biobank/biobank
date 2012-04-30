package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.PermissionEnum;

public class DispatchRetrievalAction implements Action<ListResult<Dispatch>> {
    private static final long serialVersionUID = 1L;

    private DispatchState state;
    private Integer centerId;
    private Boolean isSender;
    private Boolean noErrors;

    private static String DISPATCH_HQL_STATE_SELECT =
        "SELECT DISTINCT d FROM " + Dispatch.class.getName() + " d" //$NON-NLS-1$ //$NON-NLS-2$
            + " LEFT JOIN FETCH d.dispatchSpecimens" //$NON-NLS-1$
            + " INNER JOIN FETCH d.senderCenter" //$NON-NLS-1$
            + " INNER JOIN FETCH d.receiverCenter" //$NON-NLS-1$
            + " LEFT JOIN FETCH d.shipmentInfo si" //$NON-NLS-1$
            + " LEFT JOIN FETCH si.shippingMethod" //$NON-NLS-1$
            + " WHERE d.state=? AND "; //$NON-NLS-1$

    private static String SENDER_HQL = "d.senderCenter.id=?"; //$NON-NLS-1$
    private static String RECEIVER_HQL = "d.receiverCenter.id=?"; //$NON-NLS-1$

    private static String NOTEMPTY_HQL = " AND EXISTS "; //$NON-NLS-1$
    private static String EMPTY_HQL = " AND NOT EXISTS "; //$NON-NLS-1$

    private static String NO_ERRORS_HQL = "(FROM " //$NON-NLS-1$
        + DispatchSpecimen.class.getName() + " ds" //$NON-NLS-1$
        + " WHERE (ds.state=" + DispatchSpecimenState.MISSING.getId() //$NON-NLS-1$
        + " OR ds.state=" + DispatchSpecimenState.EXTRA.getId() //$NON-NLS-1$
        + ") AND ds.dispatch.id=d.id) "; //$NON-NLS-1$

    public DispatchRetrievalAction(DispatchState state, Integer centerId,
        Boolean isSender, Boolean noErrors) {
        this.state = state;
        this.centerId = centerId;
        this.isSender = isSender;
        this.noErrors = noErrors;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.DISPATCH_READ.isAllowed(context.getUser(),
            context.load(Center.class, centerId));
    }

    @Override
    public ListResult<Dispatch> run(ActionContext context)
        throws ActionException {
        StringBuffer qryBuf = new StringBuffer(DISPATCH_HQL_STATE_SELECT);
        if (isSender)
            qryBuf.append(SENDER_HQL);
        else
            qryBuf.append(RECEIVER_HQL);

        if (noErrors)
            qryBuf.append(EMPTY_HQL);
        else
            qryBuf.append(NOTEMPTY_HQL);

        qryBuf.append(NO_ERRORS_HQL);

        Query q = context.getSession().createQuery(qryBuf.toString());
        q.setParameter(0, state.getId());
        q.setParameter(1, centerId);

        ArrayList<Dispatch> dispatches = new ArrayList<Dispatch>();

        Query query = context.getSession().createQuery(qryBuf.toString());
        query.setParameter(0, state.getId());
        query.setParameter(1, centerId);

        @SuppressWarnings("unchecked")
        List<Dispatch> results = query.list();
        if (results != null) {
            dispatches.addAll(results);
        }

        return new ListResult<Dispatch>(dispatches);
    }
}
