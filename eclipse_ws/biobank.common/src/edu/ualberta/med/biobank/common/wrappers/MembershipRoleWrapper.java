package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipRoleBaseWrapper;
import edu.ualberta.med.biobank.model.MembershipRole;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class MembershipRoleWrapper extends MembershipRoleBaseWrapper {

    public MembershipRoleWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipRoleWrapper(WritableApplicationService appService,
        MembershipRole m) {
        super(appService, m);
    }

}
