package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipRightBaseWrapper;
import edu.ualberta.med.biobank.model.MembershipRight;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class MembershipRightWrapper extends MembershipRightBaseWrapper {

    public MembershipRightWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipRightWrapper(WritableApplicationService appService,
        MembershipRight m) {
        super(appService, m);
    }

}
