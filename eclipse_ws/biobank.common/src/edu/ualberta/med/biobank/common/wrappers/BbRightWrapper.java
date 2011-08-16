package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.BbRightBaseWrapper;
import edu.ualberta.med.biobank.model.BbRight;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class BbRightWrapper extends BbRightBaseWrapper {

    public BbRightWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public BbRightWrapper(WritableApplicationService appService,
        BbRight wrappedObject) {
        super(appService, wrappedObject);
    }
}
