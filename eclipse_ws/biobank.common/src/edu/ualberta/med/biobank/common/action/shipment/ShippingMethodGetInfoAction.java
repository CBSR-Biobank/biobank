package edu.ualberta.med.biobank.common.action.shipment;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.shipment.ShippingMethodPermission;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class ShippingMethodGetInfoAction implements
    Action<ListResult<ShippingMethod>> {
    private static final long serialVersionUID = -2969994320245657734L;

    private static final String SHIPPING_METHOD_HQL =
        "FROM " + ShippingMethod.class.getName();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ShippingMethodPermission().isAllowed(context);
    }

    @Override
    public ListResult<ShippingMethod> run(ActionContext context)
        throws ActionException {
        ArrayList<ShippingMethod> methods = new ArrayList<ShippingMethod>();
        Query query = context.getSession().createQuery(SHIPPING_METHOD_HQL);

        @SuppressWarnings("unchecked")
        List<ShippingMethod> results = query.list();
        if (results != null) {
            methods.addAll(results);
        }
        return new ListResult<ShippingMethod>(methods);
    }
}
