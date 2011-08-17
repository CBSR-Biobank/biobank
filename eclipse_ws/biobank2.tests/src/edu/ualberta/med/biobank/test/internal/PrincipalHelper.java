package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipRoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class PrincipalHelper extends DbHelper {

    public static MembershipRoleWrapper addMembershipRole(
        PrincipalWrapper<?> principal, CenterWrapper<?> center,
        StudyWrapper study) throws Exception {
        MembershipRoleWrapper mw = MembershipHelper.newMembershipRole(
            principal, center, study);
        principal.persist();
        return mw;
    }

    public static MembershipRightWrapper addMembershipRight(
        PrincipalWrapper<?> principal, CenterWrapper<?> center,
        StudyWrapper study) throws Exception {
        MembershipRightWrapper mw = MembershipHelper.newMembershipRight(
            principal, center, study);
        principal.persist();
        return mw;
    }
}
