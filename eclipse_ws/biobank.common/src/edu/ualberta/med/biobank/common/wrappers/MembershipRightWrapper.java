package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.peer.BbRightPeer;
import edu.ualberta.med.biobank.common.peer.MembershipRightPeer;
import edu.ualberta.med.biobank.common.peer.RightPrivilegePeer;
import edu.ualberta.med.biobank.common.wrappers.base.MembershipRightBaseWrapper;
import edu.ualberta.med.biobank.model.MembershipRight;
import edu.ualberta.med.biobank.model.Privilege;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class MembershipRightWrapper extends MembershipRightBaseWrapper {

    public MembershipRightWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipRightWrapper(WritableApplicationService appService,
        MembershipRight m) {
        super(appService, m);
    }

    @Override
    public String getMembershipObjectsListString() {
        // TODO Auto-generated method stub
        return "";
    }

    private static final String PRIVILEGES_FOR_RIGHT_QRY = "select p from "
        + MembershipRight.class.getName() + " as msrs join "
        + MembershipRightPeer.RIGHT_PRIVILEGE_COLLECTION + " as rps join "
        + RightPrivilegePeer.PRIVILEGE_COLLECTION + " as p where rp."
        + Property.concatNames(RightPrivilegePeer.RIGHT, BbRightPeer.ID) + "=?";

    @Override
    public List<PrivilegeWrapper> getPrivilegesForRight(BbRightWrapper right)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(PRIVILEGES_FOR_RIGHT_QRY,
            Arrays.asList(right.getId()));
        List<Privilege> res = appService.query(criteria);
        return wrapModelCollection(appService, res, PrivilegeWrapper.class);
    }
}
