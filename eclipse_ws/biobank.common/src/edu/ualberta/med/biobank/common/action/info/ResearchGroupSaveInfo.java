package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;


public class ResearchGroupSaveInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Integer id;
    public String name;
    public String nameShort;
    public Integer studyId;
    public String comment;
    public AddressSaveInfo address;
    public Integer activityStatusId;
    
    
    public ResearchGroupSaveInfo(Integer id, String name, String nameShort, Integer studyId, String comment, AddressSaveInfo address, Integer activityStatusId) {
        this.id = id;
        this.name=name;
        this.nameShort=nameShort;
        this.studyId=studyId;
        this.comment=comment;
        this.address=address;
        this.activityStatusId=activityStatusId;
    }
    
}
