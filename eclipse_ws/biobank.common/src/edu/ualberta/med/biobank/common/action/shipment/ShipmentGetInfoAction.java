package edu.ualberta.med.biobank.common.action.shipment;

import java.util.ArrayList;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ShipmentReadInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoReadPermission;
import edu.ualberta.med.biobank.model.OriginInfo;

/**
 * Retrieve a patient information using a patient id
 * 
 * @author aaron
 * 
 */
public class ShipmentGetInfoAction implements Action<ShipmentReadInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String ORIGIN_INFO_HQL =
        "SELECT DISTINCT oi FROM " + OriginInfo.class.getName() + " oi"
            + " INNER JOIN FETCH oi.shipmentInfo si"
            + " INNER JOIN FETCH si.shippingMethod"
            + " INNER JOIN FETCH oi.center"
            + " LEFT JOIN FETCH oi.comments"
            + " WHERE oi.id=?";

    private final Integer oiId;

    public ShipmentGetInfoAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new OriginInfoReadPermission(oiId).isAllowed(context);
    }

    @Override
    public ShipmentReadInfo run(ActionContext context)
        throws ActionException {
        ShipmentReadInfo sInfo = new ShipmentReadInfo();

        Query query = context.getSession().createQuery(ORIGIN_INFO_HQL);
        query.setParameter(0, oiId);

        OriginInfo oi =
            ActionContext.singleResult(query, OriginInfo.class, oiId);

        sInfo.originInfo = oi;
        sInfo.specimens = new ArrayList<SpecimenInfo>(
            new ShipmentGetSpecimenListInfoAction(oiId).run(context)
                .getList());

        return sInfo;
    }

}
