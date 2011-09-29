package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.base.BbRightBaseWrapper;
import edu.ualberta.med.biobank.model.BbRight;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class BbRightWrapper extends BbRightBaseWrapper {

    /**
     * This class is like an enum, there is no user modification of its content,
     * so we can use a cache.
     */
    private static Map<String, BbRightWrapper> rights;

    public BbRightWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public BbRightWrapper(WritableApplicationService appService,
        BbRight wrappedObject) {
        super(appService, wrappedObject);
    }

    private static final String ALL_RIGHT_QRY = "from " //$NON-NLS-1$
        + BbRight.class.getName();

    private static synchronized Map<String, BbRightWrapper> getRights(
        WritableApplicationService appService) throws ApplicationException {
        if (rights == null) {
            rights = new HashMap<String, BbRightWrapper>();
            HQLCriteria criteria = new HQLCriteria(ALL_RIGHT_QRY,
                new ArrayList<Object>());
            List<BbRight> rList = appService.query(criteria);
            for (BbRight r : rList) {
                rights.put(r.getKeyDesc(), new BbRightWrapper(appService, r));
            }
        }
        return rights;
    }

    public static List<BbRightWrapper> getAllRights(
        WritableApplicationService appService) throws ApplicationException {
        return new ArrayList<BbRightWrapper>(getRights(appService).values());
    }

    public static BbRightWrapper getRightWithKeyDesc(
        WritableApplicationService appService, String keyDesc)
        throws ApplicationException {
        return getRights(appService).get(keyDesc);
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
