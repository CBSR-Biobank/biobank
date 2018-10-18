package edu.ualberta.med.biobank.common.action.info;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;

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
    public Set<Integer> studyIds;
    public String comment;
    public AddressSaveInfo address;
    public ActivityStatus activityStatus;

    public ResearchGroupSaveInfo(Integer id,
                                 String name,
                                 String nameShort,
                                 Set<Integer> studyIds,
                                 String comment,
                                 AddressSaveInfo address,
                                 ActivityStatus activityStatus) {
        this.id = id;
        this.name = name;
        this.nameShort = nameShort;
        this.studyIds = new HashSet<>(studyIds);
        this.comment = comment;
        this.address = address;
        this.activityStatus = activityStatus;
    }

    public static ResearchGroupSaveInfo createFromResearchGroup(ResearchGroup rg) {
        Set<Integer> studyIds = new HashSet<>(0);
        for (Study study : rg.getStudies()) {
            studyIds.add(study.getId());
        }
        return new ResearchGroupSaveInfo(null,
                                         rg.getName(),
                                         rg.getNameShort(),
                                         studyIds,
                                         null,
                                         AddressSaveInfo.createFromAddress(rg.getAddress()),
                                         ActivityStatus.ACTIVE);

    }
}