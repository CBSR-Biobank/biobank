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
        "SELECT DISTINCT d FROM " + Dispatch.class.getName() + " d"
            + " LEFT JOIN FETCH d.dispatchSpecimenCollection"
            + " INNER JOIN FETCH d.senderCenter"
            + " INNER JOIN FETCH d.receiverCenter"
            + " LEFT JOIN FETCH d.shipmentInfo si"
            + " INNER JOIN FETCH si.shippingMethod"
            + " WHERE d.state=? AND ";

    private static String SENDER_HQL = "d.senderCenter.id=?";
    private static String RECEIVER_HQL = "d.receiverCenter.id=?";

    private static String NOTEMPTY_HQL = " AND EXISTS ";
    private static String EMPTY_HQL = " AND NOT EXISTS ";

    private static String NO_ERRORS_HQL = "(FROM "
        + DispatchSpecimen.class.getName() + " ds"
        + " WHERE (ds.state=" + DispatchSpecimenState.MISSING.getId()
        + " OR ds.state=" + DispatchSpecimenState.EXTRA.getId()
        + ") AND ds.dispatch.id=d.id) ";

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
            (Center) context.load(Center.class, centerId));
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
