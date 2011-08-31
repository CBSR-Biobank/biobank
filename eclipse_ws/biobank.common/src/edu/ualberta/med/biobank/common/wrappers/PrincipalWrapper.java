package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Duplicate a principal: create a new one that will have the exact same
     * relations. This duplicated principal is not yet saved into the DB.
     */
    public PrincipalWrapper<T> duplicate() {
        PrincipalWrapper<T> newPrincipal = createDuplicate();
        List<MembershipWrapper<?>> msList = new ArrayList<MembershipWrapper<?>>();
        for (MembershipWrapper<?> ms : getMembershipCollection(false)) {
            msList.add(ms.duplicate());
        }
        newPrincipal.addToMembershipCollection(msList);
        return newPrincipal;
    }

    protected abstract PrincipalWrapper<T> createDuplicate();

}
