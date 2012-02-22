package edu.ualberta.med.biobank.common.action.shipment;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.shipment.ShippingMethodPermission;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class ShippingMethodGetInfoAction implements
    Action<ListResult<ShippingMethod>> {

    /**
     * 
     */
    private static final long serialVersionUID = -2969994320245657734L;
    private static final String SHIPPING_METHOD_HQL = "from "
        + ShippingMethod.class.getName();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ShippingMethodPermission().isAllowed(context);
    }

    @Override
    public ListResult<ShippingMethod> run(ActionContext context)
        throws ActionException {
        Query query = context.getSession().createQuery(SHIPPING_METHOD_HQL);
        return new ListResult<ShippingMethod>(query.list());
    }
}
