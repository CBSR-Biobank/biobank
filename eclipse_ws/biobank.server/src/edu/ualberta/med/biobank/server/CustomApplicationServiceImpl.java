package edu.ualberta.med.biobank.server;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.util.ClassCache;

public class CustomApplicationServiceImpl extends
    WritableApplicationServiceImpl implements CustomApplicationService {

    private static final String SITE_CLASS_NAME = "edu.ualberta.med.biobank.model.Site";

    public CustomApplicationServiceImpl(ClassCache classCache) {
        super(classCache);
    }

    @Override
    public boolean canReadObjects(String userLogin, Class<?> clazz)
        throws ApplicationException {
        return hasPrivilege(userLogin, clazz, null, "READ");
    }

    @Override
    public boolean canReadObject(String userLogin, Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(userLogin, clazz, id, "READ");
    }

    @Override
    public boolean canCreateObjects(String userLogin, Class<?> clazz)
        throws ApplicationException {
        return hasPrivilege(userLogin, clazz, null, "CREATE");
    }

    @Override
    public boolean canUpdateObjects(String userLogin, Class<?> clazz)
        throws ApplicationException {
        return hasPrivilege(userLogin, clazz, null, "UPDATE");
    }

    @Override
    public boolean canUpdateObject(String userLogin, Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(userLogin, clazz, id, "UPDATE");
    }

    @Override
    public boolean hasPrivilege(String userLogin, Class<?> clazz, Integer id,
        String privilegeName) throws ApplicationException {
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
    //
    // public List querySQL(HQLCriteria criteria) throws ApplicationException {
    // Request request = new Request(criteria);
    // request.setIsCount(Boolean.FALSE);
    // request.setFirstRow(0);
    // // request.setDomainObjectName(targetClassName);
    //
    // return null;
    // }

    @Override
    public void newSite(Integer id, String name) throws ApplicationException {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager("biobank2");
            // Create protection element for the site
            ProtectionElement pe = new ProtectionElement();
            pe.setApplication(upm.getApplication("biobank2"));
            pe.setProtectionElementName(SITE_CLASS_NAME + "/ID=" + id
                + "/Name=" + name);
            pe.setProtectionElementDescription(name);
            pe.setObjectId(SITE_CLASS_NAME);
            pe.setAttribute("id");
            pe.setValue(id.toString());
            upm.createProtectionElement(pe);
            // Add the new protection element to the protection group
            // "Site Admin PG"
            upm.addProtectionElements("11", new String[] { pe
                .getProtectionElementId().toString() });
        } catch (Exception e) {
            throw new ApplicationException("Error addind new Site " + id + ":"
                + name + "security");
        }
    }
}
