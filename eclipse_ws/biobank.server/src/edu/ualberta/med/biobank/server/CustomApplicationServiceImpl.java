package edu.ualberta.med.biobank.server;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.query.hibernate.HQLCriteria;
import gov.nih.nci.system.util.ClassCache;

import java.util.List;

public class CustomApplicationServiceImpl extends
    WritableApplicationServiceImpl implements CustomApplicationService {

    public CustomApplicationServiceImpl(ClassCache classCache) {
        super(classCache);
    }

    @Override
    public boolean canAccess(Class<?> clazz, String userLogin,
        Integer objectId, String privilegeName) throws ApplicationException {
        try {
            AuthorizationManager am = SecurityServiceProvider
                .getAuthorizationManager("biobank2");
            return am
                .checkPermission(userLogin, clazz.getName(), privilegeName);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    // public <E> List<E> query(HQLCriteria hqlCriteria, String targetClassName)
    // throws ApplicationException {
    // return query(hqlCriteria);
    // }

    public List querySQL(HQLCriteria criteria) throws ApplicationException {
        Request request = new Request(criteria);
        request.setIsCount(Boolean.FALSE);
        request.setFirstRow(0);
        // request.setDomainObjectName(targetClassName);

        return null;
    }
}
