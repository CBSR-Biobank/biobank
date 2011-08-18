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

    @Override
    public int compareTo(ModelWrapper<RightPrivilege> rp2) {
        if (rp2 instanceof RightPrivilegeWrapper) {
            BbRightWrapper right1 = getRight();
            BbRightWrapper right2 = ((RightPrivilegeWrapper) rp2).getRight();
            if (right1 == null || right2 == null)
                return 0;
            return right1.compareTo(right2);
        }
        return 0;
    }
}
