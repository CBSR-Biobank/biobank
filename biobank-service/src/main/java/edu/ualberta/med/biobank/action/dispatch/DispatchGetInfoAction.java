package edu.ualberta.med.biobank.action.dispatch;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.center.Shipment;

public class DispatchGetInfoAction implements Action<DispatchReadInfo> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr CANNOT_FIND_DISPATCH_ERRMSG =
        bundle.tr("Cannot find a dispatch with id \"{0}\".");

    @SuppressWarnings("nls")
    private static final String DISPATCH_HQL = "SELECT distinct dispatch "
        + "FROM " + Shipment.class.getName() + " dispatch"
        + " LEFT JOIN FETCH dispatch.shipmentInfo si"
        + " LEFT JOIN FETCH si.shippingMethod"
        + " INNER JOIN FETCH dispatch.receiverCenter"
        + " INNER JOIN FETCH dispatch.senderCenter"
        + " LEFT JOIN fetch dispatch.comments comments"
        + " LEFT JOIN fetch comments.user"
        + " WHERE dispatch.id=?";

    private final Integer id;

    public DispatchGetInfoAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new DispatchReadPermission(id).isAllowed(context);
    }

    @Override
    public DispatchReadInfo run(ActionContext context) throws ActionException {
        DispatchReadInfo sInfo = new DispatchReadInfo();

        Query query = context.getSession().createQuery(DISPATCH_HQL);
        query.setParameter(0, id);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        if (rows.size() == 1) {
            Object row = rows.get(0);

            sInfo.dispatch = (Shipment) row;
            sInfo.specimens =
                new DispatchGetSpecimenInfosAction(id).run(context).getSet();

        } else {
            throw new LocalizedException(CANNOT_FIND_DISPATCH_ERRMSG.format(id));
        }

        return sInfo;
    }
}
