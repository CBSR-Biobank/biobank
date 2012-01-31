package edu.ualberta.med.biobank.common.action.info;

import java.util.Collection;
import java.util.HashSet;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;

public class ShipmentReadInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    public OriginInfo oi;
    public Collection<Specimen> specimens = new HashSet<Specimen>();

}
