package edu.ualberta.med.biobank.common.action.shipment;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ShipmentFormReadInfo;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoReadPermission;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.User;

/**
 * Retrieve a patient information using a patient id
 * 
 * @author aaron
 * 
 */
public class ShipmentGetInfoAction implements Action<ShipmentFormReadInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String ORIGIN_INFO_HQL = "select oi from "
    + OriginInfo.class.getName() 
    + " oi join fetch oi." + OriginInfoPeer.SHIPMENT_INFO.getName()
    + " si join fetch si." + ShipmentInfoPeer.SHIPPING_METHOD.getName()
    + " join fetch oi." + OriginInfoPeer.CENTER.getName()
    + " left join fetch si." + ShipmentInfoPeer.COMMENT_COLLECTION.getName()
    + " where oi." + OriginInfoPeer.ID.getName()+"=? group by oi";
    // @formatter:on

    private final Integer oiId;

    public ShipmentGetInfoAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new OriginInfoReadPermission(oiId).isAllowed(user, session);
    }

    @Override
    public ShipmentFormReadInfo run(User user, Session session) throws ActionException {
        ShipmentFormReadInfo sInfo = new ShipmentFormReadInfo();

        Query query = session.createQuery(ORIGIN_INFO_HQL);
        query.setParameter(0, oiId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object row = rows.get(0);

            sInfo.oi = (OriginInfo) row;
            sInfo.specimens =
                new ShipmentGetSpecimenInfosAction(oiId).run(user, session);

        } else {
            throw new ActionException("No patient found with id:" + oiId); //$NON-NLS-1$
        }

        return sInfo;
    }

}
