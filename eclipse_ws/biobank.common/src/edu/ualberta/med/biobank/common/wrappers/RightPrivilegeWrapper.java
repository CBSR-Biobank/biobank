package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.RightPrivilegeBaseWrapper;
import edu.ualberta.med.biobank.model.RightPrivilege;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RightPrivilegeWrapper extends RightPrivilegeBaseWrapper {

    public RightPrivilegeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RightPrivilegeWrapper(WritableApplicationService appService,
        RightPrivilege wrappedObject) {
        super(appService, wrappedObject);
    }
}
