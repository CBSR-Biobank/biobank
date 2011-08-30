package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipBaseWrapper;
import edu.ualberta.med.biobank.model.Membership;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class MembershipWrapper<T extends Membership> extends
    MembershipBaseWrapper<T> {

    public MembershipWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipWrapper(WritableApplicationService appService,
        T wrappedObject) {
        super(appService, wrappedObject);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public int compareTo(ModelWrapper<T> o2) {
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

    public abstract String getMembershipObjectsListString();

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

    protected abstract List<PrivilegeWrapper> getPrivilegesForRightInternal(
        BbRightWrapper right, CenterWrapper<?> center, StudyWrapper study)
        throws ApplicationException;
}
