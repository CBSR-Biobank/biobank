package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankFailedQueryException;
import edu.ualberta.med.biobank.common.peer.BbRightPeer;
import edu.ualberta.med.biobank.common.wrappers.base.BbRightBaseWrapper;
import edu.ualberta.med.biobank.model.BbRight;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class BbRightWrapper extends BbRightBaseWrapper {

    public BbRightWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public BbRightWrapper(WritableApplicationService appService,
        BbRight wrappedObject) {
        super(appService, wrappedObject);
    }

    private static final String ALL_RIGHT_QRY = "from "
        + BbRight.class.getName();

    public static List<BbRightWrapper> getAllRights(
        WritableApplicationService appService) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(ALL_RIGHT_QRY,
            new ArrayList<Object>());

        List<BbRight> rights = appService.query(criteria);
        return ModelWrapper.wrapModelCollection(appService, rights,
            BbRightWrapper.class);
    }

    private static final String RIGHT_QRY = "from " + BbRight.class.getName()
        + " where " + BbRightPeer.KEY_DESC.getName() + " = ?";

    public static BbRightWrapper getRightWithKeyDesc(
        WritableApplicationService appService, String keyDesc)
        throws ApplicationException, BiobankFailedQueryException {

        HQLCriteria c = new HQLCriteria(RIGHT_QRY,
            Arrays.asList(new Object[] { keyDesc }));

        List<BbRight> result = appService.query(c);
        if (result.size() != 1)
            throw new BiobankFailedQueryException(
                "unexpected results from query");
        return new BbRightWrapper(appService, result.get(0));
    }

    @Override
    public int compareTo(ModelWrapper<BbRight> r2) {
        if (r2 instanceof BbRightWrapper) {
            String name1 = getName();
            String name2 = ((BbRightWrapper) r2).getName();
            if (name1 == null || name2 == null)
                return 0;
            return name1.compareTo(name2);
        }
        return 0;
    }

    public boolean isForSite() {
        return getForSite() != null && getForSite();
    }

    public boolean isForResearchGroup() {
        return getForResearchGroup() != null && getForResearchGroup();
    }

    public boolean isForClinic() {
        return getForClinic() != null && getForClinic();
    }

    public boolean isForStudy() {
        return getForStudy() != null && getForStudy();
    }

}
