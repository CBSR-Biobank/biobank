package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.PermissionBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.PermissionPostCheck;
import edu.ualberta.med.biobank.model.Permission;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PermissionWrapper extends PermissionBaseWrapper {

    public PermissionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public PermissionWrapper(WritableApplicationService appService,
        Permission wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public int compareTo(ModelWrapper<Permission> rp2) {
        if (rp2 instanceof PermissionWrapper) {
            BbRightWrapper right1 = getRight();
            BbRightWrapper right2 = ((PermissionWrapper) rp2).getRight();
            if (right1 == null || right2 == null)
                return 0;
            return right1.compareTo(right2);
        }
        return 0;
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        super.addPersistTasks(tasks);
        tasks.add(new PermissionPostCheck(this));
    }

}
