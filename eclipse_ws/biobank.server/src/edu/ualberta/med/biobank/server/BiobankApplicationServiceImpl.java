package edu.ualberta.med.biobank.server;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.util.ClassCache;

/**
 * Implementation of the BiobankApplicationService interface. This class will be
 * only on the server side.
 * 
 * See build.properties of the sdk for the generator configuration +
 * application-config*.xml for the generated files.
 */
public class BiobankApplicationServiceImpl extends
    WritableApplicationServiceImpl implements BiobankApplicationService {

    public static final String SITE_CLASS_NAME = "edu.ualberta.med.biobank.model.Site";

    private static final String APPLICATION_CONTEXT_NAME = "biobank2";

    public BiobankApplicationServiceImpl(ClassCache classCache) {
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
    public boolean canDeleteObjects(String userLogin, Class<?> clazz)
        throws ApplicationException {
        return hasPrivilege(userLogin, clazz, null, "DELETE");
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
                .getAuthorizationManager(APPLICATION_CONTEXT_NAME);
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
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
            // Create protection element for the site
            ProtectionElement pe = new ProtectionElement();
            pe.setApplication(upm.getApplication(APPLICATION_CONTEXT_NAME));
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
                + name + "security: " + e.getMessage());
        }
    }
}
