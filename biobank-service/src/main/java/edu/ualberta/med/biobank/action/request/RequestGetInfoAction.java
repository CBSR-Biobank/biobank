package edu.ualberta.med.biobank.action.request;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.permission.request.RequestReadPermission;

public class RequestGetInfoAction implements Action<RequestReadInfo> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr REQUEST_NOT_FOUND =
        bundle.tr("No request found with id \"{0}\".");

    private final Integer id;

    @SuppressWarnings("nls")
    private static final String REQUEST_HQL =
        "select distinct request from "
            + Request.class.getName()
            + " request join fetch request.researchGroup"
            + " rg left join fetch request.dispatches "
            + " dispatches left join fetch dispatches.dispatchSpecimens ds"
            + " left join fetch dispatches.senderCenter left join fetch dispatches.receiverCenter"
            + " left join fetch dispatches.shipmentInfo "
            + " join fetch request.addres"
            + " where request.id=?";

    public RequestGetInfoAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new RequestReadPermission().isAllowed(context);
    }

    @Override
    public RequestReadInfo run(ActionContext context)
        throws ActionException {
        RequestReadInfo sInfo = new RequestReadInfo();

        Query query = context.getSession().createQuery(REQUEST_HQL);
        query.setParameter(0, id);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        if (rows.size() == 1) {
            Object row = rows.get(0);

            sInfo.request = (Request) row;

        } else {
            throw new LocalizedException(REQUEST_NOT_FOUND.format(id));
        }

        return sInfo;
    }
}
