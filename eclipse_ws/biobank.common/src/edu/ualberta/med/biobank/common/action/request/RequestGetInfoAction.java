package edu.ualberta.med.biobank.common.action.request;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.RequestPeer;
import edu.ualberta.med.biobank.common.permission.request.RequestReadPermission;
import edu.ualberta.med.biobank.model.Request;

public class RequestGetInfoAction implements Action<RequestReadInfo> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Integer id;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String REQUEST_HQL = "select distinct request from "
    + Request.class.getName() 
    + " request join fetch request." + RequestPeer.RESEARCH_GROUP.getName()
    + " rg left join fetch request.dispatches " 
    + " dispatches left join fetch dispatches.dispatchSpecimens ds" 
    + " left join fetch dispatches.senderCenter left join fetch dispatches.receiverCenter" 
    + " left join fetch dispatches.shipmentInfo "
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
            throw new ActionException(
                "No request found with id:" + id); //$NON-NLS-1$
        }

        return sInfo;
    }

}
