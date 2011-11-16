package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;
// @author: aaron
public class ShippingMethodInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Integer id;
    
    public ShippingMethodInfo(Integer id) {
        this.id=id;
    }
    
}
