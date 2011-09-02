package edu.ualberta.med.biobank.test.internal;

import java.util.Arrays;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class MembershipHelper extends DbHelper {

    public static MembershipWrapper newMembership(
        PrincipalWrapper<?> principal, CenterWrapper<?> center,
        StudyWrapper study) throws Exception {
        MembershipWrapper mw = new MembershipWrapper(appService);
        mw.setPrincipal(principal);
        principal.addToMembershipCollection(Arrays.asList(mw));
        mw.setCenter(center);
        mw.setStudy(study);
        return mw;
    }

    public static MembershipWrapper addMembership(
        PrincipalWrapper<?> principal, CenterWrapper<?> center,
        StudyWrapper study) throws Exception {
        MembershipWrapper ms = newMembership(principal, center, study);
        ms.persist();
        return ms;
    }

}
