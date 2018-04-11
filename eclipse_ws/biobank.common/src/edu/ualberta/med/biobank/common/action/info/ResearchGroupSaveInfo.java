package edu.ualberta.med.biobank.common.action.info;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.ActivityStatus;

/**
 *
 * Data object that holds the Research Group information when
 * the Research Group is being saved.
 *
 * This object is created by the ResearchGroupSaveAction class.
 *
 * Code Changes -
 * 		1> Remove the variable for holding the Study ID as the study is now a list
 * 		2> Update the constructor parameters for the same
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupSaveInfo implements ActionResult
{
    private static final long serialVersionUID = 1L;
    public Integer id;
    public String name;
    public String nameShort;
    public String comment;
    public AddressSaveInfo address;
    public ActivityStatus activityStatus;

    public ResearchGroupSaveInfo(Integer id, String name, String nameShort, String comment, AddressSaveInfo address, ActivityStatus activityStatus)
    {
        this.id = id;
        this.name=name;
        this.nameShort=nameShort;
        this.comment=comment;
        this.address=address;
        this.activityStatus=activityStatus;
    }
}