package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.i18n.Msg;
import edu.ualberta.med.biobank.model.Dispatch;

public class DispatchGetInfoAction implements Action<DispatchReadInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String DISPATCH_HQL = "SELECT distinct dispatch "
        + "FROM " + Dispatch.class.getName() + " dispatch"
        + " LEFT JOIN FETCH dispatch.shipmentInfo si"
        + " LEFT JOIN FETCH si.shippingMethod"
        + " INNER JOIN FETCH dispatch.receiverCenter"
        + " INNER JOIN FETCH dispatch.senderCenter"
        + " LEFT JOIN fetch dispatch.comments comments"
        + " LEFT JOIN fetch comments.user"
        + " WHERE dispatch.id=?";

    private Integer id;

    public DispatchGetInfoAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new DispatchReadPermission(id).isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public DispatchReadInfo run(ActionContext context) throws ActionException {
        DispatchReadInfo sInfo = new DispatchReadInfo();

        Query query = context.getSession().createQuery(DISPATCH_HQL);
        query.setParameter(0, id);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        if (rows.size() == 1) {
            Object row = rows.get(0);

            sInfo.dispatch = (Dispatch) row;
            sInfo.specimens =
                new DispatchGetSpecimenInfosAction(id).run(context).getSet();

        } else {
            throw new ActionException(
                Msg.tr("No dispatch found with id \"{0}\".", id));
        }

        return sInfo;
    }

}
