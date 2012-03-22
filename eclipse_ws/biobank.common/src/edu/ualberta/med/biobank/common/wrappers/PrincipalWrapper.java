package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.base.PrincipalBaseWrapper;
import edu.ualberta.med.biobank.model.Principal;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class PrincipalWrapper<T extends Principal> extends
    PrincipalBaseWrapper<T> {

    private static final String WORKING_CENTERS_KEY = "workingCenters"; //$NON-NLS-1$

    public PrincipalWrapper(WritableApplicationService appService,
        T wrappedObject) {
        super(appService, wrappedObject);
    }

    public PrincipalWrapper(WritableApplicationService appService) {
        super(appService);
    }

    /**
     * Duplicate a principal: create a new one that will have the exact same
     * relations. This duplicated principal is not yet saved into the DB.
     */
    public PrincipalWrapper<T> duplicate() {
        PrincipalWrapper<T> newPrincipal = createDuplicate();
        List<MembershipWrapper> msList = new ArrayList<MembershipWrapper>();
        for (MembershipWrapper ms : getMembershipCollection(false)) {
            msList.add(ms.duplicate());
        }
        newPrincipal.addToMembershipCollection(msList);
        return newPrincipal;
    }

    protected abstract PrincipalWrapper<T> createDuplicate();

    protected List<CenterWrapper<?>> getAllCentersInvolved() throws Exception {
        List<CenterWrapper<?>> centers = new ArrayList<CenterWrapper<?>>();
        for (MembershipWrapper ms : getMembershipCollection(false)) {
            CenterWrapper<?> center = ms.getCenter();
            if (center == null)
                centers.addAll(CenterWrapper.getCenters(appService));
            else
                centers.add(center);
        }
        return centers;
    }

    @SuppressWarnings("unchecked")
    public List<CenterWrapper<?>> getWorkingCenters() throws Exception {
        List<CenterWrapper<?>> workingCenters = (List<CenterWrapper<?>>) cache
            .get(WORKING_CENTERS_KEY);
        if (workingCenters == null) {
            workingCenters = new ArrayList<CenterWrapper<?>>();
            List<CenterWrapper<?>> setOfWorkingCenter =
                getAllCentersInvolved();
            workingCenters.addAll(setOfWorkingCenter);
            cache.put(WORKING_CENTERS_KEY, workingCenters);
        }
        return workingCenters;
    }

}
