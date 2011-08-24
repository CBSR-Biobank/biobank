package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankFailedQueryException;
import edu.ualberta.med.biobank.common.wrappers.base.PrivilegeBaseWrapper;
import edu.ualberta.med.biobank.model.Privilege;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PrivilegeWrapper extends PrivilegeBaseWrapper {

    public PrivilegeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public PrivilegeWrapper(WritableApplicationService appService,
        Privilege wrappedObject) {
        super(appService, wrappedObject);
    }

    private static final String ALL_PRIVILEGES_QRY = "from "
        + Privilege.class.getName();

    public static List<PrivilegeWrapper> getAllPrivileges(
        WritableApplicationService appService) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(ALL_PRIVILEGES_QRY,
            new ArrayList<Object>());

        List<Privilege> privileges = appService.query(criteria);
        return ModelWrapper.wrapModelCollection(appService, privileges,
            PrivilegeWrapper.class);
    }

    private static final String PRIVILEGE_QRY = "from "
        + Privilege.class.getName() + " where name = ?";

    public static PrivilegeWrapper getPrivilege(
        WritableApplicationService appService, String name)
        throws ApplicationException, BiobankFailedQueryException {

        HQLCriteria c = new HQLCriteria(PRIVILEGE_QRY,
            Arrays.asList(new Object[] { name }));

        List<Privilege> result = appService.query(c);
        if (result.size() != 1)
            throw new BiobankFailedQueryException(
                "unexpected results from query");
        return new PrivilegeWrapper(appService, result.get(0));
    }

    public static PrivilegeWrapper getReadPrivilege(
        WritableApplicationService appService)
        throws BiobankFailedQueryException, ApplicationException {
        return getPrivilege(appService, "Read");
    }

    public static PrivilegeWrapper getUpdatePrivilege(
        WritableApplicationService appService)
        throws BiobankFailedQueryException, ApplicationException {
        return getPrivilege(appService, "Update");
    }

    public static PrivilegeWrapper getDeletePrivilege(
        WritableApplicationService appService)
        throws BiobankFailedQueryException, ApplicationException {
        return getPrivilege(appService, "Delete");
    }

    public static PrivilegeWrapper getCreatePrivilege(
        WritableApplicationService appService)
        throws BiobankFailedQueryException, ApplicationException {
        return getPrivilege(appService, "Create");
    }

}
