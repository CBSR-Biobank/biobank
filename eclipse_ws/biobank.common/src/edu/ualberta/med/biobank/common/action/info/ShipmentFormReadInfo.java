package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;
import java.util.Collection;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;


public class ShipmentFormReadInfo implements Serializable, NotAProxy {
    private static final long serialVersionUID = 1L;

    public OriginInfo oi;
    public Collection<Specimen> specimens;

}
