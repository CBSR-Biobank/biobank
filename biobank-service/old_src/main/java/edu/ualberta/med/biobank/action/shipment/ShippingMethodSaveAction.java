package edu.ualberta.med.biobank.action.shipment;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.GlobalAdminPermission;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class ShippingMethodSaveAction implements Action<IdResult> {

    /**
     * 
     */
    private static final long serialVersionUID = -2262223420008351254L;
    private Integer id;
    private String name;

    public ShippingMethodSaveAction(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new GlobalAdminPermission().isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        ShippingMethod sm =
            context.load(ShippingMethod.class, id, new ShippingMethod());
        sm.setName(name);
        context.getSession().saveOrUpdate(sm);
        return new IdResult(sm.getId());
    }
}
