package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.RolePeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.RoleBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.RolePreDeleteChecks;
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

    /**
     * Duplicate this role. The resulting role is not saved yet in the database
     */
    public RoleWrapper duplicate() {
        RoleWrapper newRole = new RoleWrapper(appService);
        newRole.setName(getName());
        List<RightPrivilegeWrapper> newRpList = new ArrayList<RightPrivilegeWrapper>();
        for (RightPrivilegeWrapper rp : getRightPrivilegeCollection(false)) {
            RightPrivilegeWrapper newRp = new RightPrivilegeWrapper(appService);
            newRp.setRight(rp.getRight());
            newRp.addToPrivilegeCollection(rp.getPrivilegeCollection(false));
            newRpList.add(newRp);
        }
        newRole.addToRightPrivilegeCollection(newRpList);
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
    protected void addDeleteTasks(TaskList tasks) {
        tasks.add(new RolePreDeleteChecks(this));

        super.addDeleteTasks(tasks);
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        // if a rightprivilege is removed, it should be deleted.
        tasks.deleteRemoved(this, RolePeer.RIGHT_PRIVILEGE_COLLECTION);
        super.addPersistTasks(tasks);
    }

}
