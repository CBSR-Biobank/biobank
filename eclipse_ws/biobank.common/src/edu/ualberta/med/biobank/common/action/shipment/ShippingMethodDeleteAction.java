package edu.ualberta.med.biobank.common.action.shipment;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.shipment.ShippingMethodPermission;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class ShippingMethodDeleteAction implements Action<EmptyResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 1945867287858294885L;
    private Integer id;

    public ShippingMethodDeleteAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ShippingMethodPermission().isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        context.getSession().delete(context.load(ShippingMethod.class, id));
        return new EmptyResult();
    }

}
