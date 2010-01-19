package edu.ualberta.med.biobank.server;

import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.exceptions.CSConfigurationException;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.util.ClassCache;

public class CustomApplicationServiceImpl extends
    WritableApplicationServiceImpl implements CustomApplicationService {

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

    @SuppressWarnings("unchecked")
    @Override
    public void newSite(Integer id, String name) {
        try {
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager("biobank2");
            ProtectionElement pe = new ProtectionElement();
            pe.setApplication(upm.getApplication("biobank2"));
            pe.setProtectionElementName(Site.class.getName() + "/ID=" + id
                + "/Name=" + name);
            pe.setProtectionElementDescription(name);
            pe.setObjectId(Site.class.getName());
            pe.setAttribute("id");
            pe.setValue(id.toString());
            upm.createProtectionElement(pe);
            upm.addProtectionElements("11", new String[] { pe
                .getProtectionElementId().toString() });
        } catch (CSConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String toString() {
        return "I am here: " + getClass().getName();
    }

}
