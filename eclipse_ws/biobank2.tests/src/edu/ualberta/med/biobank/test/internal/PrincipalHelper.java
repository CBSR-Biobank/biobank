package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class PrincipalHelper extends DbHelper {

    public static MembershipWrapper addMembership(
        PrincipalWrapper<?> principal, CenterWrapper<?> center,
        StudyWrapper study) throws Exception {
        MembershipWrapper mw = MembershipHelper.newMembership(principal,
            center, study);
        principal.persist();
        return mw;
    }

}
