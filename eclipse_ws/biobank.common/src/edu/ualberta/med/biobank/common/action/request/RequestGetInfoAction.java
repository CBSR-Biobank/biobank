package edu.ualberta.med.biobank.common.action.request;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.RequestPeer;
import edu.ualberta.med.biobank.common.permission.request.RequestReadPermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Request;

/**
 *
 * Action object that queries the database for the Request and it's relevant associations
 *
 * Code Changes -
 * 		1> Add the join for getting the Request Specimens so that the data exists in the RequestWrapper
 * 		2> Add the join for getting the Specimen info from the Request Specimens
 *
 * @author OHSDEV
 *
 */
public class RequestGetInfoAction implements Action<RequestReadInfo> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr REQUEST_NOT_FOUND =
        bundle.tr("No request found with id \"{0}\".");

    private final Integer id;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String REQUEST_HQL = "select distinct request from "
    + Request.class.getName() 
    + " request join fetch request." + RequestPeer.RESEARCH_GROUP.getName()
    + " rg left join fetch request.dispatches " 
    + " dispatches left join fetch dispatches.dispatchSpecimens ds"
    + " left join fetch request.requestSpecimens "								//OHSDEV
    + " requestSpecimens left join fetch requestSpecimens.specimen sp"			//OHSDEV
    + " left join fetch dispatches.senderCenter "
    + " left join fetch dispatches.receiverCenter "
    + " left join fetch dispatches.shipmentInfo "
    + " LEFT JOIN fetch dispatches.comments comments"
    + " LEFT JOIN fetch comments.user"
    + " join fetch request." + RequestPeer.ADDRESS.getName()
    + " where request." + DispatchPeer.ID.getName()
    +"=?";
    // @formatter:on

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
