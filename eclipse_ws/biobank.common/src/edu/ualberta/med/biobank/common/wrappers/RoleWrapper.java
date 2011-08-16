package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.RoleBaseWrapper;
import edu.ualberta.med.biobank.model.Role;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class RoleWrapper extends RoleBaseWrapper {

    public RoleWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RoleWrapper(WritableApplicationService appService, Role wrappedObject) {
        super(appService, wrappedObject);
    }

}
