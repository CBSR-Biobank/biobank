package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.BbGroupBaseWrapper;
import edu.ualberta.med.biobank.model.BbGroup;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class BbGroupWrapper extends BbGroupBaseWrapper {

    public BbGroupWrapper(WritableApplicationService appService,
        BbGroup wrappedObject) {
        super(appService, wrappedObject);
    }

    public BbGroupWrapper(WritableApplicationService appService) {
        super(appService);
    }

}
