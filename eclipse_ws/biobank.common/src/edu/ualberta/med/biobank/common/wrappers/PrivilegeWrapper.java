package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.PrivilegeBaseWrapper;
import edu.ualberta.med.biobank.model.Privilege;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PrivilegeWrapper extends PrivilegeBaseWrapper {

    public PrivilegeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public PrivilegeWrapper(WritableApplicationService appService,
        Privilege wrappedObject) {
        super(appService, wrappedObject);
    }

}
