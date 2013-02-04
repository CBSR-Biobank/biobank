package edu.ualberta.med.biobank.common.action.shipment;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.shipment.ShippingMethodGetInfoAction.ShippingMethodInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class ShippingMethodGetInfoAction implements Action<ShippingMethodInfo> {
    private static final long serialVersionUID = 1L;

    public static class ShippingMethodInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        private ShippingMethod shippingMethod;

        public ShippingMethod getShippingMethod() {
            return shippingMethod;
        }

    }

    private final String name;

    public ShippingMethodGetInfoAction(String name) {
        this.name = name;
    }

    @SuppressWarnings("nls")
    private static final String SHIPPING_METHOD_HQL = "FROM " + ShippingMethod.class.getName()
    + " WHERE name=?";

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // all users are allowed to get the shipping methods
        return true;
    }

    @Override
    public ShippingMethodInfo run(ActionContext context) throws ActionException {
        Query query = context.getSession().createQuery(SHIPPING_METHOD_HQL);
        query.setParameter(0, name);

        ShippingMethod shippingMethod = (ShippingMethod) query.uniqueResult();
        if (shippingMethod != null) {
            ShippingMethodInfo result = new ShippingMethodInfo();
            result.shippingMethod = shippingMethod;
            return result;
        }

        return null;
    }

}
