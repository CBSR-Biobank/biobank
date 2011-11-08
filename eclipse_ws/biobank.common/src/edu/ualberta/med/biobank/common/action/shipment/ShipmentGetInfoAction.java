package edu.ualberta.med.biobank.common.action.shipment;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetInfoAction.ShipInfo;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoReadPermission;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

/**
 * Retrieve a patient information using a patient id
 * 
 * @author aaron
 * 
 */
public class ShipmentGetInfoAction implements Action<ShipInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String ORIGIN_INFO_HQL = "select oi from "
    + OriginInfo.class.getName() 
    + " oi join fetch oi.shipmentInfo si join fetch si.shippingMethod join fetch oi.center left join fetch si.commentCollection where oi.id=? group by oi";
    // @formatter:on

    private final Integer oiId;

    public static class ShipInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        public OriginInfo oi;
        public Collection<Specimen> specimens;

    }

    public ShipmentGetInfoAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new OriginInfoReadPermission(oiId).isAllowed(user, session);
    }

    @Override
    public ShipInfo run(User user, Session session) throws ActionException {
        ShipInfo sInfo = new ShipInfo();

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
