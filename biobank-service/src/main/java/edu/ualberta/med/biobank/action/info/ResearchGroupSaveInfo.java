package edu.ualberta.med.biobank.action.info;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.model.ActivityStatus;


public class ResearchGroupSaveInfo implements ActionResult{

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
    public ActivityStatus activityStatus;
    
    
    public ResearchGroupSaveInfo(Integer id, String name, String nameShort, Integer studyId, String comment, AddressSaveInfo address, ActivityStatus activityStatus) {
        this.id = id;
        this.name=name;
        this.nameShort=nameShort;
        this.studyId=studyId;
        this.comment=comment;
        this.address=address;
        this.activityStatus=activityStatus;
    }
    
}
