package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.base.MembershipBaseWrapper;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
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
        newMs.addToPermissionCollection(getPermissionCollection());
        return newMs;
    }

    public boolean isCenterStudyAlreadyUsed(CenterWrapper<?> center,
        StudyWrapper study) {
        for (MembershipWrapper ms : getPrincipal().getMembershipCollection(
            false)) {
            if (!this.equals(ms)
                && ((ms.getCenter() == null && center == null) || (ms
                    .getCenter() != null && ms.getCenter().equals(center)))
                && ((ms.getStudy() == null && study == null) || (ms.getStudy() != null && ms
                    .getStudy().equals(study))))
                return true;
        }
        return false;
    }

    public void addToPermissionCollection(Collection<PermissionEnum> addedPermissions) {
        wrappedObject.getPermissionCollection().addAll(addedPermissions);
    }

    public void removeFromPermissionCollection(
        Collection<PermissionEnum> removedPermissions) {
        wrappedObject.getPermissionCollection().removeAll(removedPermissions);
    }

    public Collection<PermissionEnum> getPermissionCollection() {
        return wrappedObject.getPermissionCollection();
    }
}
