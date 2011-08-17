package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipBaseWrapper;
import edu.ualberta.med.biobank.model.Membership;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class MembershipWrapper<T extends Membership> extends
    MembershipBaseWrapper<T> {

    public MembershipWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipWrapper(WritableApplicationService appService,
        T wrappedObject) {
        super(appService, wrappedObject);
    }
}
