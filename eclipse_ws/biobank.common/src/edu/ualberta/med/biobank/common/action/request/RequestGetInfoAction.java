package edu.ualberta.med.biobank.common.action.request;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.common.peer.RequestPeer;
import edu.ualberta.med.biobank.common.permission.request.RequestReadPermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;

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
    private static final String REQUEST_HQL =
        "SELECT DISTINCT request "
        + " FROM "+ Request.class.getName() + " request"
        + " JOIN FETCH request." + RequestPeer.RESEARCH_GROUP.getName() + " rg "
        + " LEFT JOIN FETCH request.dispatches dispatches"
        + " LEFT JOIN FETCH dispatches.dispatchSpecimens ds"
        + " LEFT JOIN FETCH request.requestSpecimens requestSpecimens"   //OHSDEV
        + " LEFT JOIN FETCH requestSpecimens.specimen sp"                //OHSDEV
        + " LEFT JOIN FETCH dispatches.senderCenter "
        + " LEFT JOIN FETCH dispatches.receiverCenter "
        + " LEFT JOIN FETCH dispatches.shipmentInfo "
        + " LEFT JOIN FETCH dispatches.comments comments"
        + " LEFT JOIN FETCH comments.user"
        + " JOIN FETCH request." + RequestPeer.ADDRESS.getName()
        + " WHERE request." + RequestPeer.ID.getName() +"=?";
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
            sInfo.request = (Request) rows.get(0);

            // load required associations
            for (RequestSpecimen rs : sInfo.request.getRequestSpecimens()) {
                rs.getSpecimen().getCollectionEvent().getPatient().getStudy().getId();
            }
        } else {
            throw new LocalizedException(REQUEST_NOT_FOUND.format(id));
        }

        return sInfo;
    }
}
