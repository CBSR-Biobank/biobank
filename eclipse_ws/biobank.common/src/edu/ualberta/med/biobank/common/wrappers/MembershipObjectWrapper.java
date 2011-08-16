package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipObjectBaseWrapper;
import edu.ualberta.med.biobank.model.MembershipObject;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class MembershipObjectWrapper<T extends MembershipObject>
    extends MembershipObjectBaseWrapper<T> {

    public MembershipObjectWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipObjectWrapper(WritableApplicationService appService,
        T wrappedObject) {
        super(appService, wrappedObject);
    }
}
