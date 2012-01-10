package edu.ualberta.med.biobank.common.action.request;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.RequestFormReadInfo;
import edu.ualberta.med.biobank.common.peer.DispatchPeer;
import edu.ualberta.med.biobank.common.peer.RequestPeer;
import edu.ualberta.med.biobank.common.permission.request.RequestReadPermission;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.User;

public class RequestGetInfoAction implements Action<RequestFormReadInfo> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Integer id;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String REQUEST_HQL = "select request from "
    + Request.class.getName() 
    + " request join fetch request." + RequestPeer.RESEARCH_GROUP.getName()
    + " rg left join fetch request." + RequestPeer.DISPATCH_COLLECTION.getName()
    + " dispatchCollection join fetch request." + RequestPeer.ADDRESS.getName()
    + " where request." + DispatchPeer.ID.getName()
    +"=? group by request";
    // @formatter:on

    public RequestGetInfoAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new RequestReadPermission().isAllowed(user, session);
    }

    @Override
    public RequestFormReadInfo run(User user, Session session)
        throws ActionException {
        RequestFormReadInfo sInfo = new RequestFormReadInfo();

        Query query = session.createQuery(REQUEST_HQL);
        query.setParameter(0, id);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        if (rows.size() == 1) {
            Object row = rows.get(0);

            sInfo.request = (Request) row;
            sInfo.specimens =
                new RequestGetSpecimenInfosAction(id).run(user, session)
                    .getList();

        } else {
            throw new ActionException(
                "No dispatch specimens found for id:" + id); //$NON-NLS-1$
        }

        return sInfo;
    }

}
