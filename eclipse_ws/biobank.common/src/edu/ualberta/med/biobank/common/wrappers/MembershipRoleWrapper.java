package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.BbRightPeer;
import edu.ualberta.med.biobank.common.peer.MembershipRolePeer;
import edu.ualberta.med.biobank.common.peer.RightPrivilegePeer;
import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.wrappers.base.MembershipRoleBaseWrapper;
import edu.ualberta.med.biobank.model.MembershipRole;
import edu.ualberta.med.biobank.model.Privilege;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

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

    private static final String PRIVILEGES_FOR_RIGHT_QRY = "select distinct(p) from "
        + MembershipRole.class.getName()
        + " as msrs join msrs."
        + MembershipRolePeer.ROLE_COLLECTION.getName()
        + " as roles join roles."
        + RolePeer.RIGHT_PRIVILEGE_COLLECTION.getName()
        + " as rps join rps."
        + RightPrivilegePeer.PRIVILEGE_COLLECTION.getName()
        + " as p where rps."
        + Property.concatNames(RightPrivilegePeer.RIGHT, BbRightPeer.ID)
        + "=? and msrs." + MembershipRolePeer.ID.getName() + "=?";

    @Override
    public List<PrivilegeWrapper> getPrivilegesForRight(BbRightWrapper right)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(PRIVILEGES_FOR_RIGHT_QRY,
            Arrays.asList(right.getId(), getId()));
        List<Privilege> res = appService.query(criteria);
        return wrapModelCollection(appService, res, PrivilegeWrapper.class);
    }

}
