package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.wrappers.base.PrincipalBaseWrapper;
import edu.ualberta.med.biobank.model.Domain;
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

    protected Set<CenterWrapper<?>> getAllCentersInvolved() throws Exception {
        Set<CenterWrapper<?>> centers = new HashSet<CenterWrapper<?>>();
        for (MembershipWrapper ms : getMembershipCollection(false)) {
            Domain domain = ms.getWrappedObject().getDomain();
            if (domain.isAllCenters())
                centers.addAll(CenterWrapper.getCenters(appService));
            else {
                List<CenterWrapper<?>> wrappedCenters =
                    ModelWrapper.wrapModelCollection(
                        appService, domain.getCenters(), null);
                centers.addAll(wrappedCenters);
            }
        }
        return centers;
    }

    @SuppressWarnings("unchecked")
    public List<CenterWrapper<?>> getWorkingCenters() throws Exception {
        List<CenterWrapper<?>> workingCenters = (List<CenterWrapper<?>>) cache
            .get(WORKING_CENTERS_KEY);
        if (workingCenters == null) {
            workingCenters = new ArrayList<CenterWrapper<?>>();
            Set<CenterWrapper<?>> setOfWorkingCenter =
                getAllCentersInvolved();
            workingCenters.addAll(setOfWorkingCenter);
            cache.put(WORKING_CENTERS_KEY, workingCenters);
        }
        return workingCenters;
    }

}
