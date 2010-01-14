package edu.ualberta.med.biobank.server;

import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.dao.orm.WritableORMDAOImpl;

public class CustomORMDAOImpl extends WritableORMDAOImpl {

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
