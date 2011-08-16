package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipBaseWrapper;
import edu.ualberta.med.biobank.model.Membership;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class MembershipWrapper extends MembershipBaseWrapper {

    public MembershipWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipWrapper(WritableApplicationService appService,
        Membership wrappedObject) {
        super(appService, wrappedObject);
    }
}
