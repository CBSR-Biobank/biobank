package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.base.PrincipalBaseWrapper;
import edu.ualberta.med.biobank.model.Principal;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class PrincipalWrapper<T extends Principal> extends
    PrincipalBaseWrapper<T> {

    private List<MembershipWrapper<?>> removedMemberships = new ArrayList<MembershipWrapper<?>>();

    public PrincipalWrapper(WritableApplicationService appService,
        T wrappedObject) {
        super(appService, wrappedObject);
    }

    public PrincipalWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public void removeFromMembershipCollection(
        List<? extends MembershipWrapper<?>> membershipCollection) {
        super.removeFromMembershipCollection(membershipCollection);
        removedMemberships.addAll(membershipCollection);
    }

    @Override
    public void removeFromMembershipCollectionWithCheck(
        List<? extends MembershipWrapper<?>> membershipCollection)
        throws BiobankCheckException {
        super.removeFromMembershipCollectionWithCheck(membershipCollection);
        removedMemberships.addAll(membershipCollection);
    }

    @Override
    protected void resetInternalFields() {
        removedMemberships.clear();
    }

    @Override
    protected void persistDependencies(Principal origObject) throws Exception {
        for (MembershipWrapper<?> ms : removedMemberships) {
            if (!ms.isNew())
                ms.delete();
        }
    }
}
