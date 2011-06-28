package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class ResearchGroupWrapper extends ResearchGroupBaseWrapper {

    public ResearchGroupWrapper(WritableApplicationService appService,
        ResearchGroup rg) {
        super(appService, rg);
    }

    public static Collection<? extends ModelWrapper<?>> getAllResearchGroups(
        BiobankApplicationService appService) {
        // TODO Auto-generated method stub
        return null;
    }

    public static int getCount(BiobankApplicationService appService) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void reload() {
        // TODO Auto-generated method stub

    }

}