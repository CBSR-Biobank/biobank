package edu.ualberta.med.biobank.common.action.dispatch;


import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchFormReadInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentFormReadInfo;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetSpecimenInfosAction;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.User;

public class DispatchGetInfoAction implements Action<DispatchFormReadInfo> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Integer id;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String DISPATCH_HQL = "select dispatch from "
    + Dispatch.class.getName() 
    + " dispatch join fetch oi.shipmentInfo si join fetch si.shippingMethod join fetch dispatch.receivingCenter join fetch dispatch.sendingCenter left join fetch si.commentCollection where dispatch.id=? group by dispatch";
    // @formatter:on

    public DispatchGetInfoAction(Integer id) {
        this.id=id;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new DispatchReadPermission(id).isAllowed(user, session);
    }

    @Override
    public DispatchFormReadInfo run(User user, Session session) throws ActionException {
        DispatchFormReadInfo sInfo = new DispatchFormReadInfo();

        Query query = session.createQuery(DISPATCH_HQL);
        query.setParameter(0, id);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object row = rows.get(0);

            sInfo.dispatch = (Dispatch) row;
            sInfo.specimens =
                new DispatchGetSpecimenInfosAction(id).run(user, session);

        } else {
            throw new ActionException("No specimens found for id:" + id); //$NON-NLS-1$
        }

        return sInfo;
    }

}
