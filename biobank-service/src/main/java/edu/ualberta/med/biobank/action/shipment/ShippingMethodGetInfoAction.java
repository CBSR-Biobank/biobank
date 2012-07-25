package edu.ualberta.med.biobank.action.shipment;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class ShippingMethodGetInfoAction implements
    Action<ListResult<ShippingMethod>> {
    private static final long serialVersionUID = -2969994320245657734L;

    @SuppressWarnings("nls")
    private static final String SHIPPING_METHOD_HQL =
        "FROM " + ShippingMethod.class.getName();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // all users are allowed to get the shipping methods
        return true;
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
