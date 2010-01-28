package edu.ualberta.med.biobank.server;

import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.dao.orm.WritableORMDAOImpl;

/**
 * Hibernate calls are made in this server side class. It extends the default
 * WritableORMDAOImpl class.
 * 
 * 
 * See build.properties of the sdk for the generator configuration +
 * application-config*.xml for the generated files.
 */
public class BiobankORMDAOImpl extends WritableORMDAOImpl {

    @Override
    public Response query(Request request) throws DAOException {
        System.out
            .println("*************************************************************");
        System.out.println("********************" + getClass().getName()
            + " query method");
        System.out.println("********************" + "getRequest:"
            + request.getRequest());
        System.out.println("********************" + "getDomainObjectName:"
            + request.getDomainObjectName());
        System.out
            .println("*************************************************************");
        return super.query(request);
    }
}
