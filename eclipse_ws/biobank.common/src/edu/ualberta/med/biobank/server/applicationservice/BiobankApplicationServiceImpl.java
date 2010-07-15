package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.client.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.logging.MessageGenerator;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import edu.ualberta.med.biobank.server.reports.ReportFactory;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.ExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.util.ClassCache;

import java.util.List;
import java.util.Set;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Implementation of the BiobankApplicationService interface. This class will be
 * only on the server side.
 * 
 * See build.properties of the sdk for the generator configuration +
 * application-config*.xml for the generated files.
 */
public class BiobankApplicationServiceImpl extends
    WritableApplicationServiceImpl implements BiobankApplicationService {

    private static Logger log = Logger
        .getLogger(BiobankApplicationServiceImpl.class.getName());

    public static final String SITE_CLASS_NAME = "edu.ualberta.med.biobank.model.Site";

    private static final String APPLICATION_CONTEXT_NAME = "biobank2";

    private static final String SITE_ADMIN_PG_ID = "11";

    private static final String CONTAINER_ADMINISTRATION_STRING = "biobank.cbsr.container.administration";

    private static final String CREATE_PRIVILEGE = "CREATE";

    private static final String DELETE_PRIVILEGE = "DELETE";

    private static final String UPDATE_PRIVILEGE = "UPDATE";

    private static final String READ_PRIVILEGE = "READ";

    public BiobankApplicationServiceImpl(ClassCache classCache) {
        super(classCache);
    }

    @Override
    public boolean canReadObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, READ_PRIVILEGE);
    }

    @Override
    public boolean canReadObject(Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(clazz, id, READ_PRIVILEGE);
    }

    @Override
    public boolean canCreateObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, CREATE_PRIVILEGE);
    }

    @Override
    public boolean canDeleteObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, DELETE_PRIVILEGE);
    }

    @Override
    public boolean canDeleteObject(Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(clazz, id, DELETE_PRIVILEGE);
    }

    @Override
    public boolean canUpdateObjects(Class<?> clazz) throws ApplicationException {
        return hasPrivilege(clazz, null, UPDATE_PRIVILEGE);
    }

    @Override
    public boolean canUpdateObject(Class<?> clazz, Integer id)
        throws ApplicationException {
        return hasPrivilege(clazz, id, UPDATE_PRIVILEGE);
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
            return am.checkPermission(userLogin, clazz.getName(), "id",
                id.toString(), privilegeName);
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public boolean isContainerAdministrator() throws ApplicationException {
        try {
            String userLogin = SecurityContextHolder.getContext()
                .getAuthentication().getName();
            AuthorizationManager am = SecurityServiceProvider
                .getAuthorizationManager(APPLICATION_CONTEXT_NAME);
            return am.checkPermission(userLogin,
                CONTAINER_ADMINISTRATION_STRING, "CREATE");
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * How can we manage security using sql ??
     */
    @Override
    public <E> List<E> query(BiobankSQLCriteria sqlCriteria,
        String targetClassName) throws ApplicationException {
        throw new ApplicationException(
            "This functionnality is not available until further notice");
        // return privateQuery(sqlCriteria, targetClassName);
    }

    @Override
    public SDKQueryResult executeQuery(SDKQuery query)
        throws ApplicationException {
        SDKQueryResult res = super.executeQuery(query);
        if (query instanceof ExampleQuery) {
            Object queryObject = ((ExampleQuery) query).getExample();
            if (queryObject != null && queryObject instanceof Site) {
                if (query instanceof InsertExampleQuery) {
                    newSiteSecurity((Site) res.getObjectResult());
                } else if (query instanceof DeleteExampleQuery) {
                    deleteSiteSecurity((Site) queryObject);
                }
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private void deleteSiteSecurity(Site site) throws ApplicationException {
        Object id = null;
        String nameShort = null;
        try {
            id = site.getId();
            nameShort = site.getNameShort();
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
            Set<ProtectionElement> siteAdminPEs = upm
                .getProtectionElements(SITE_ADMIN_PG_ID);
            for (ProtectionElement pe : siteAdminPEs) {
                if (pe.getValue().equals(id.toString())) {
                    upm.removeProtectionElement(pe.getProtectionElementId()
                        .toString());
                    return;
                }
            }
        } catch (Exception e) {
            throw new ApplicationException("Error deleting site " + id + ":"
                + nameShort + "security: " + e.getMessage());
        }

    }

    private void newSiteSecurity(Site site) throws ApplicationException {
        Object id = null;
        String nameShort = null;
        try {
            id = site.getId();
            nameShort = site.getNameShort();
            UserProvisioningManager upm = SecurityServiceProvider
                .getUserProvisioningManager(APPLICATION_CONTEXT_NAME);
            // Create protection element for the site
            ProtectionElement pe = new ProtectionElement();
            pe.setApplication(upm.getApplication(APPLICATION_CONTEXT_NAME));
            pe.setProtectionElementName(SITE_CLASS_NAME + "/" + nameShort);
            pe.setProtectionElementDescription(nameShort);
            pe.setObjectId(SITE_CLASS_NAME);
            pe.setAttribute("id");
            pe.setValue(id.toString());
            upm.createProtectionElement(pe);
            // Add the new protection element to the protection group
            // "Site Admin PG"
            upm.addProtectionElements(SITE_ADMIN_PG_ID, new String[] { pe
                .getProtectionElementId().toString() });
        } catch (Exception e) {
            log.error("error adding new site security", e);
            throw new ApplicationException("Error adding new site " + id + ":"
                + nameShort + "security:" + e.getMessage());
        }
    }

    @Override
    public void logActivity(String action, String patientNumber,
        String inventoryID, String locationLabel, String details, String type) {
        Logger logger = Logger.getLogger("Biobank.Activity");
        logger.log(Level.toLevel("INFO"), MessageGenerator
            .generateStringMessage(action, patientNumber, inventoryID,
                locationLabel, details, type));
    }

    @Override
    public List<Object> launchReport(BiobankReport report)
        throws ApplicationException {
        return ReportFactory.createReport(report).generate(this);
    }

}
