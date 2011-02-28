package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.ResearchGroupBaseWrapper;
import edu.ualberta.med.biobank.model.ResearchGroup;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ResearchGroupWrapper extends ResearchGroupBaseWrapper {

    public ResearchGroupWrapper(WritableApplicationService appService,
        ResearchGroup rg) {
        super(appService, rg);
    }

}