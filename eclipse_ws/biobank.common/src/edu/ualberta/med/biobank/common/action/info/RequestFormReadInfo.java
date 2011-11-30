package edu.ualberta.med.biobank.common.action.info;

import java.util.Collection;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;

public class RequestFormReadInfo implements ActionResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Request request;
    public Collection<RequestSpecimen> specimens;

}
