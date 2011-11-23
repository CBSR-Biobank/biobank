package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;

public class ResearchGroupAdapterInfo implements Serializable {

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
