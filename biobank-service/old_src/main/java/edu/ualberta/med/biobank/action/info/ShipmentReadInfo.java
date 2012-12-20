package edu.ualberta.med.biobank.action.info;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.OriginInfo;

public class ShipmentReadInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    public OriginInfo originInfo;
    public List<SpecimenInfo> specimens = new ArrayList<SpecimenInfo>();

}
