package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipRoleBaseWrapper;
import edu.ualberta.med.biobank.model.MembershipRole;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class MembershipRoleWrapper extends MembershipRoleBaseWrapper {

    public MembershipRoleWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipRoleWrapper(WritableApplicationService appService,
        MembershipRole m) {
        super(appService, m);
    }

    @Override
    public String getMembershipObjectsListString() {
        StringBuffer sb = new StringBuffer();
        for (RoleWrapper r : getRoleCollection(true)) {
            sb.append(r.getName());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Don't use HQL because this user method will be call a lot. It is better
     * to call getter, they will be loaded once and the kept in memory
     */
    @Override
    protected List<PrivilegeWrapper> getPrivilegesForRightInternal(
        BbRightWrapper right, CenterWrapper<?> center, StudyWrapper study)
        throws ApplicationException {
        List<PrivilegeWrapper> privileges = new ArrayList<PrivilegeWrapper>();
        for (RoleWrapper r : getRoleCollection(false)) {
            for (RightPrivilegeWrapper rp : r
                .getRightPrivilegeCollection(false)) {
                if (rp.getRight().equals(right))
                    privileges.addAll(rp.getPrivilegeCollection(false));
            }
        }
        return privileges;
    }

    @Override
    protected MembershipRoleWrapper createDuplicate() {
        MembershipRoleWrapper newMs = new MembershipRoleWrapper(appService);
        newMs.addToRoleCollection(getRoleCollection(false));
        return newMs;
    }
}
