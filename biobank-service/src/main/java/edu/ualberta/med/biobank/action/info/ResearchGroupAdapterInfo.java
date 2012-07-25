package edu.ualberta.med.biobank.action.info;

import edu.ualberta.med.biobank.action.ActionResult;

public class ResearchGroupAdapterInfo implements ActionResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Integer id;
    public String nameShort;
    
    public ResearchGroupAdapterInfo(Integer id, String nameShort) {
        this.id=id;
        this.nameShort=nameShort;
    }
    
}
