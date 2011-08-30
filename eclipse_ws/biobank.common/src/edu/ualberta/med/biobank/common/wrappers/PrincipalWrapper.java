package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.peer.PrincipalPeer;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.PrincipalBaseWrapper;
import edu.ualberta.med.biobank.model.Principal;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class PrincipalWrapper<T extends Principal> extends
    PrincipalBaseWrapper<T> {

    public PrincipalWrapper(WritableApplicationService appService,
        T wrappedObject) {
        super(appService, wrappedObject);
    }

    public PrincipalWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.deleteRemoved(this, PrincipalPeer.MEMBERSHIP_COLLECTION);
        super.addPersistTasks(tasks);
    }

}
