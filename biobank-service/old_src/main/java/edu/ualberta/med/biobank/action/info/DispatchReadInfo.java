package edu.ualberta.med.biobank.action.info;

import java.util.Set;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.model.center.Shipment;
import edu.ualberta.med.biobank.model.center.ShipmentSpecimen;

public class DispatchReadInfo implements ActionResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Shipment dispatch;
    public Set<ShipmentSpecimen> specimens;

}
