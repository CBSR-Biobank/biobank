package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.wrappers.base.EventAttrBaseWrapper;
import edu.ualberta.med.biobank.model.EventAttr;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class EventAttrWrapper extends EventAttrBaseWrapper {

    public EventAttrWrapper(WritableApplicationService appService,
        EventAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public EventAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

}
