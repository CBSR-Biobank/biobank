package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipRoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class MembershipHelper extends DbHelper {

    public static MembershipRoleWrapper newMembershipRole(
        PrincipalWrapper<?> principal, CenterWrapper<?> center,
        StudyWrapper study) throws Exception {
        MembershipRoleWrapper mw = new MembershipRoleWrapper(appService);
        mw.setPrincipal(principal);
        principal.addToMembershipCollection(Arrays.asList(mw));
        mw.setCenter(center);
        mw.setStudy(study);
        return mw;
    }

    public static MembershipRightWrapper newMembershipRight(
        PrincipalWrapper<?> principal, CenterWrapper<?> center,
        StudyWrapper study) throws Exception {
        MembershipRightWrapper mw = new MembershipRightWrapper(appService);
        mw.setPrincipal(principal);
        principal.addToMembershipCollection(Arrays.asList(mw));
        mw.setCenter(center);
        mw.setStudy(study);
        return mw;
    }
}
