package edu.ualberta.med.biobank.common.action.dispatch;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.common.permission.dispatch.DispatchReadPermission;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.User;

public class DispatchGetInfoAction implements Action<DispatchReadInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String DISPATCH_HQL =
        "select dispatch from "
            + Dispatch.class.getName()
            + " dispatch left join fetch dispatch.shipmentMethod"
            + " si left join fetch si.shippingMethod"
            + " join fetch dispatch.receiverCenter"
            + " join fetch dispatch.senderCenter"
            + " left join fetch dispatch.commentCollection"
            + " commentCollection where dispatch.id=?"
            + " group by dispatch";

    private Integer id;

    public DispatchGetInfoAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new DispatchReadPermission(id).isAllowed(user, session);
    }

    @Override
    public DispatchReadInfo run(User user, Session session)
        throws ActionException {
        DispatchReadInfo sInfo = new DispatchReadInfo();

        Query query = session.createQuery(DISPATCH_HQL);
        query.setParameter(0, id);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        if (rows.size() == 1) {
            Object row = rows.get(0);

            sInfo.dispatch = (Dispatch) row;
            sInfo.specimens =
                new DispatchGetSpecimenInfosAction(id).run(user, session)
                    .getList();

        } else {
            throw new ActionException("No dispatch specimens found for id:" + id); //$NON-NLS-1$
        }

        return sInfo;
    }

}
