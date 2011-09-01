package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.MembershipRightPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.MembershipRightBaseWrapper;
import edu.ualberta.med.biobank.model.MembershipRight;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class MembershipRightWrapper extends MembershipRightBaseWrapper {

    public MembershipRightWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipRightWrapper(WritableApplicationService appService,
        MembershipRight m) {
        super(appService, m);
    }

    @Override
    public String getMembershipObjectsListString() {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (RightPrivilegeWrapper rp : getRightPrivilegeCollection(true)) {
            if (first)
                first = false;
            else
                sb.append("\n");
            sb.append(rp.getRight().getName());
            sb.append("/");
            boolean firstP = true;
            for (PrivilegeWrapper p : rp.getPrivilegeCollection(true)) {
                if (firstP)
                    firstP = false;
                else
                    sb.append(",");
                sb.append(p.getName());
            }
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
        for (RightPrivilegeWrapper rp : getRightPrivilegeCollection(false)) {
            if (rp.getRight().equals(right))
                privileges.addAll(rp.getPrivilegeCollection(false));
        }
        return privileges;
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        // if a rightprivilege is removed, it should be deleted.
        tasks.deleteRemoved(this,
            MembershipRightPeer.RIGHT_PRIVILEGE_COLLECTION);
        super.addPersistTasks(tasks);
    }

    @Override
    protected MembershipRightWrapper createDuplicate() {
        MembershipRightWrapper newMs = new MembershipRightWrapper(appService);
        List<RightPrivilegeWrapper> newRpList = new ArrayList<RightPrivilegeWrapper>();
        for (RightPrivilegeWrapper rp : getRightPrivilegeCollection(false)) {
            RightPrivilegeWrapper newRp = new RightPrivilegeWrapper(appService);
            newRp.setRight(rp.getRight());
            newRp.addToPrivilegeCollection(rp.getPrivilegeCollection(false));
            newRpList.add(newRp);
        }
        newMs.addToRightPrivilegeCollection(newRpList);
        return newMs;
    }

    @Override
    public boolean isUsingRight(BbRightWrapper right) {
        if (right != null)
            for (RightPrivilegeWrapper rp : getRightPrivilegeCollection(false)) {
                if (rp.getRight().equals(right))
                    return true;
            }
        return false;
    }
}
