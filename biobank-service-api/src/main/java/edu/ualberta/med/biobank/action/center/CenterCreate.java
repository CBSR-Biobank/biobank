package edu.ualberta.med.biobank.action.center;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.Mutable;
import edu.ualberta.med.biobank.dto.center.CenterId;

public class CenterCreate
    implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private final Mutable<CenterId> id = new Mutable<CenterId>();
    private String name;
    private String description;
    
    
}
