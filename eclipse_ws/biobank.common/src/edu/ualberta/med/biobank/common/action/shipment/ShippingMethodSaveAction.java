package edu.ualberta.med.biobank.common.action.shipment;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
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
        return true;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        ShippingMethod sm = new ShippingMethod();
        sm.setId(id);
        sm.setName(name);
        return new IdResult((Integer) context.getSession().save(sm));
    }
}
