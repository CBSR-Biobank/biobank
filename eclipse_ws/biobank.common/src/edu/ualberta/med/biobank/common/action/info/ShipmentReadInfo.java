package edu.ualberta.med.biobank.common.action.info;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;

public class ShipmentReadInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    public OriginInfo originInfo;
    public Set<Specimen> specimens = new HashSet<Specimen>();

}
