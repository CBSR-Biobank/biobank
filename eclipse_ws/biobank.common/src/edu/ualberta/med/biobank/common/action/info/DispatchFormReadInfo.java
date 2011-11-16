package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;
import java.util.List;

import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;

public class DispatchFormReadInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Dispatch dispatch;
    public List<DispatchSpecimen> specimens;
    
}
