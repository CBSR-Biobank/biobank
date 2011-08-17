package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankFailedQueryException;
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
        + " where name = ?";

    public static BbRightWrapper getRight(
        WritableApplicationService appService, String name)
        throws ApplicationException, BiobankFailedQueryException {

        HQLCriteria c = new HQLCriteria(RIGHT_QRY,
            Arrays.asList(new Object[] { name }));

        List<BbRight> result = appService.query(c);
        if (result.size() != 1)
            throw new BiobankFailedQueryException(
                "unexpected results from query");
        return new BbRightWrapper(appService, result.get(0));
    }
}
