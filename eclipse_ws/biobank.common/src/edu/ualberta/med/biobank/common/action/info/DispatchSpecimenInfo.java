package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.util.NotAProxy;

public class DispatchSpecimenInfo implements Serializable, NotAProxy {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Integer id;
    public Integer specimenId;
    public Integer state;
    
    public DispatchSpecimenInfo(Integer id, Integer specimenId, Integer state) {
        this.id=id;
        this.specimenId=specimenId;
        this.state=state;
    }

}
