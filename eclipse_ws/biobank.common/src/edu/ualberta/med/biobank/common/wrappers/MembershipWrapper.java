package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.MembershipPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.MembershipBaseWrapper;
import edu.ualberta.med.biobank.model.Membership;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class MembershipWrapper extends MembershipBaseWrapper {

    public MembershipWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipWrapper(WritableApplicationService appService,
        Membership wrappedObject) {
        super(appService, wrappedObject);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public int compareTo(ModelWrapper<Membership> o2) {
        if (o2 instanceof MembershipWrapper) {
            CenterWrapper c1 = getCenter();
            CenterWrapper c2 = ((MembershipWrapper) o2).getCenter();
            int compare = 0;
            if (c1 != null && c2 != null) {
                compare += c1.compareTo(c2);
            }
            StudyWrapper s1 = getStudy();
            StudyWrapper s2 = ((MembershipWrapper) o2).getStudy();
            if (s1 != null && s2 != null) {
                compare += s1.compareTo(s2);
            }
            return compare;
        }
        return 0;
    }

    public String getMembershipObjectsListString() {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (PermissionWrapper rp : getPermissionCollection(true)) {
            if (first)
                first = false;
            else
                sb.append(";");
            sb.append(rp.getRight().getName());
        }
        for (RoleWrapper r : getRoleCollection(true)) {
            if (first)
                first = false;
            else
                sb.append(";");
            sb.append(r.getName());

        }
        return sb.toString();
    }

    public List<PrivilegeWrapper> getPrivilegesForRight(BbRightWrapper right,
        CenterWrapper<?> center, StudyWrapper study)
        throws ApplicationException {
        // if this membership center is null, then can apply to all centers.
        // Otherwise it should be the same center.
        // if this membership study is null, then can apply to all studies.
        // Otherwise it should be the same study
        if ((getCenter() == null || getCenter().equals(center))
            && (getStudy() == null || getStudy().equals(study))) {
            return getPrivilegesForRightInternal(right, center, study);
        }
        return new ArrayList<PrivilegeWrapper>();
    }

    /**
     * Don't use HQL because this user method will be call a lot. It is better
     * to call getter, they will be loaded once and the kept in memory
     */
    protected List<PrivilegeWrapper> getPrivilegesForRightInternal(
        BbRightWrapper right, CenterWrapper<?> center, StudyWrapper study) {
        List<PrivilegeWrapper> privileges = new ArrayList<PrivilegeWrapper>();
        for (PermissionWrapper rp : getPermissionCollection(false)) {
            if (rp.getRight().equals(right))
                privileges.addAll(rp.getPrivilegeCollection(false));
        }
        for (RoleWrapper r : getRoleCollection(false)) {
            for (PermissionWrapper rp : r.getPermissionCollection(false)) {
                if (rp.getRight().equals(right))
                    privileges.addAll(rp.getPrivilegeCollection(false));
            }
        }
        return privileges;
    }

    /**
     * Duplicate a membership: create a new one that will have the exact same
     * relations, center, study. This duplicated membership is not yet saved
     * into the DB. Principal is not copied because a new one will be set
     */
    public MembershipWrapper duplicate() {
        MembershipWrapper newMs = new MembershipWrapper(appService);
        newMs.setCenter(getCenter());
        newMs.setStudy(getStudy());
        newMs.addToRoleCollection(getRoleCollection(false));
        List<PermissionWrapper> newRpList = new ArrayList<PermissionWrapper>();
        for (PermissionWrapper rp : getPermissionCollection(false)) {
            PermissionWrapper newRp = new PermissionWrapper(appService);
            newRp.setRight(rp.getRight());
            newRp.addToPrivilegeCollection(rp.getPrivilegeCollection(false));
            newRpList.add(newRp);
        }
        newMs.addToPermissionCollection(newRpList);
        return newMs;
    }

    public boolean isUsingRight(BbRightWrapper right) {
        if (right != null) {
            for (PermissionWrapper rp : getPermissionCollection(false)) {
                if (rp.getRight().equals(right))
                    return true;
            }
            for (RoleWrapper r : getRoleCollection(false)) {
                if (r.isUsingRight(right))
                    return true;
            }
        }
        return false;
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        // if a permission is removed, it should be deleted.
        tasks.deleteRemoved(this, MembershipPeer.PERMISSION_COLLECTION);
        super.addPersistTasks(tasks);
    }

}
