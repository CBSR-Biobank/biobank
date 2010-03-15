package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.server.applicationservice.helper.BiobankApiApplicationServiceMethodHelper;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.util.ClassCache;

import java.util.List;

import org.acegisecurity.context.SecurityContextHolder;

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
    public boolean canReadObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, "READ");
    }

    @Override
    public boolean canReadObject(Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(clazz, id, "READ");
    }

    @Override
    public boolean canCreateObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, "CREATE");
    }

    @Override
    public boolean canDeleteObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, "DELETE");
    }

    @Override
    public boolean canDeleteObject(Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(clazz, id, "CREATE");
    }

    @Override
    public boolean canUpdateObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, "UPDATE");
    }

    @Override
    public boolean canUpdateObject(Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(clazz, id, "UPDATE");
    }

    @Override
    public boolean hasPrivilege(Class<?> clazz, Integer id, String privilegeName)
        throws ApplicationException {
        try {
            String userLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            AuthorizationManager am = SecurityServiceProvider
                .getAuthorizationManager(APPLICATION_CONTEXT_NAME);
            if (id == null) {
                return am.checkPermission(userLogin, clazz.getName(),
                    privilegeName);
            }
            return am.checkPermission(userLogin, clazz.getName(), "id", id
                .toString(), privilegeName);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * How can we manage security using sql ??
     */
    public <E> List<E> query(BiobankSQLCriteria sqlCriteria,
        String targetClassName) throws ApplicationException {
        throw new ApplicationException(
            "This functionnality is not available until further notice");
        // return privateQuery(sqlCriteria, targetClassName);
    }

    /**
     * @see BiobankApiApplicationServiceMethodHelper#getDomainObjectName(org.aopalliance.intercept.MethodInvocation)
     *      for limitation access to this method
     */
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
