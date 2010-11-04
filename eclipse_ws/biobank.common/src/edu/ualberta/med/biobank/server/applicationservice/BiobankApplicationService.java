package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;

/**
 * Application service interface obtained through
 * "ApplicationServiceProvider.getApplicationServiceFromUrl" method. This
 * replace the default WritableApplicationService interface to add our own
 * methods.
 * 
 * See build.properties of the sdk for the generator configuration +
 * application-config*.xml for the generated files.
 */
public interface BiobankApplicationService extends WritableApplicationService {

    public <E> List<E> query(BiobankSQLCriteria sqlCriteria,
        String targetClassName) throws ApplicationException;

    public void logActivity(String action, String site, String patientNumber,
        String inventoryID, String locationLabel, String details, String type)
        throws Exception;

    public void logActivity(Log log) throws Exception;

    public List<Object> launchReport(BiobankReport report)
        throws ApplicationException;

    public void modifyPassword(String oldPassword, String newPassword)
        throws ApplicationException;

    public List<Group> getSecurityGroups() throws ApplicationException;

    public List<User> getSecurityUsers() throws ApplicationException;

    public void persistUser(edu.ualberta.med.biobank.common.security.User user)
        throws ApplicationException;

    public void deleteUser(String login) throws ApplicationException;

    public User getCurrentUser() throws ApplicationException;

    public void unlockUser(String userName) throws ApplicationException;

    public void checkVersion(String version) throws ApplicationException;
}
