package edu.ualberta.med.biobank.test.action.helper;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.info.AddressSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupSaveAction;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class ResearchGroupHelper extends Helper {

    public static Integer createResearchGroup(
        BiobankApplicationService appService,
        String name, String nameShort, Integer studyId) throws Exception {
        AddressSaveInfo addressSaveInfo =
            new AddressSaveInfo(null, "test", "test", "test", "test", "test",
                "test", "test", "test", "test", "test");
        ResearchGroupSaveInfo save =
            new ResearchGroupSaveInfo(null, name + "rg", name + "rg",
                studyId, "comment", addressSaveInfo,
                ActivityStatusEnum.ACTIVE.getId());
        ResearchGroupSaveAction rgSave = new ResearchGroupSaveAction(save);

        return appService.doAction(rgSave).getId();
    }
}
