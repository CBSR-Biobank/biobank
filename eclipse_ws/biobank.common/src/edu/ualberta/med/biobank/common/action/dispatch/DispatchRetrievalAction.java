package edu.ualberta.med.biobank.common.action.dispatch;

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

    /**
     * 
     */
    private static final long serialVersionUID = -5948955536772801969L;
    private DispatchState state;
    private Integer centerId;
    private Boolean isSender;
    private Boolean noErrors;

    private static String DISPATCH_HQL_STATE_SELECT =
        "select distinct d from "
            + Dispatch.class.getName()
            + " d left join fetch d.dispatchSpecimenCollection "
            + "inner join fetch d.senderCenter inner join fetch d.receiverCenter "
            + "left join fetch d.shipmentInfo where d.state=? and ";

    private static String sender_HQL = "d.senderCenter.id=?";
    private static String receiver_HQL = "d.receiverCenter.id=?";

    private static String NOTEMPTY_HQL = " and exists ";
    private static String EMPTY_HQL = " and not exists ";

    private static String noErrors_HQL = "(from "
        + DispatchSpecimen.class.getName() + " ds where (ds.state="
        + DispatchSpecimenState.MISSING.getId()
        + " or ds.state=" + DispatchSpecimenState.EXTRA.getId()
        + ") and ds.dispatch.id=d.id) ";

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
        String QRY = DISPATCH_HQL_STATE_SELECT;
        if (isSender)
            QRY += sender_HQL;
        else
            QRY += receiver_HQL;
        if (noErrors)
            QRY += EMPTY_HQL;
        else
            QRY += NOTEMPTY_HQL;
        QRY += noErrors_HQL;
        Query q = context.getSession().createQuery(QRY);
        q.setParameter(0, state.getId());
        q.setParameter(1, centerId);
        return new ListResult<Dispatch>(q.list());
    }
}
