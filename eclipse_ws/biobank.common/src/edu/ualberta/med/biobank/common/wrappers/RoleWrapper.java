package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.MembershipRolePeer;
import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.wrappers.base.RoleBaseWrapper;
import edu.ualberta.med.biobank.model.MembershipRole;
import edu.ualberta.med.biobank.model.Role;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class RoleWrapper extends RoleBaseWrapper {

    public RoleWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public RoleWrapper(WritableApplicationService appService, Role wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public int compareTo(ModelWrapper<Role> role2) {
        if (role2 instanceof RoleWrapper) {
            String name1 = getName();
            String name2 = ((RoleWrapper) role2).getName();

            if (name1 == null || name2 == null)
                return 0;
            return name1.compareTo(name2);
        }
        return 0;
    }

    private static final String ALL_ROLES_QRY = " from " + Role.class.getName();

    public static List<RoleWrapper> getAllRoles(
        BiobankApplicationService appService) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(ALL_ROLES_QRY,
            new ArrayList<Object>());

        List<Role> roles = appService.query(criteria);
        return ModelWrapper.wrapModelCollection(appService, roles,
            RoleWrapper.class);
    }

    public RoleWrapper duplicate() {
        RoleWrapper newRole = new RoleWrapper(appService);
        newRole.setName(getName());
        // FIXME also copy relations
        return newRole;
    }

    public List<BbRightWrapper> getRightsinUse() {
        List<BbRightWrapper> rights = new ArrayList<BbRightWrapper>();
        for (RightPrivilegeWrapper rp : getRightPrivilegeCollection(false)) {
            rights.add(rp.getRight());
        }
        return rights;
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsedInMembership()) {
            throw new BiobankDeleteException(
                "This role is used in at least one membership. Please remove it first.");
        }
    }

    private static final String USED_IN_MEMBERSHIP_QRY = "select count(ms) from "
        + MembershipRole.class.getName()
        + " as ms join ms."
        + MembershipRolePeer.ROLE_COLLECTION.getName()
        + " as roles where roles." + RolePeer.ID.getName() + "=?";

    private boolean isUsedInMembership()
        throws BiobankQueryResultSizeException, ApplicationException {
        HQLCriteria criteria = new HQLCriteria(USED_IN_MEMBERSHIP_QRY,
            Arrays.asList(new Object[] { getId() }));
        Long res = getCountResult(appService, criteria);
        return res > 0;
    }
}
