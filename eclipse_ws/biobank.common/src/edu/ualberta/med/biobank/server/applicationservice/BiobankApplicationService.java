package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.reports.QueryCommand;
import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;

/**
 * Application service interface obtained through
 * "ApplicationServiceProvider.getApplicationServiceFromUrl" method. This replace the default
 * WritableApplicationService interface to add our own methods.
 * 
 * See build.properties of the sdk for the generator configuration + application-config*.xml for the
 * generated files.
 */
public interface BiobankApplicationService extends WritableApplicationService {

    public <E> List<E> query(BiobankSQLCriteria sqlCriteria,
        String targetClassName) throws ApplicationException;

    public void logActivity(String action, String site, String patientNumber,
        String inventoryID, String locationLabel, String details, String type)
        throws Exception;

    public void logActivity(Log log) throws Exception;

    /**
     * csmUserId will help to check this method is called by the user itself.
     */
    public void executeModifyPassword(Long csmUserId, String oldPassword,
        String newPassword, Boolean bulkEmails) throws ApplicationException;

    public void unlockUser(String userNameToUnlock) throws ApplicationException;

    public List<Object> runReport(Report report, int maxResults, int firstRow,
        int timeout) throws ApplicationException;

    public void checkVersion(String clientVersion) throws ApplicationException;

    public String getServerVersion();

    public QueryHandle createQuery(QueryCommand qc) throws Exception;

    public List<Object> startQuery(QueryHandle qh) throws Exception;

    public void stopQuery(QueryHandle qh) throws Exception;

    public String getUserPassword(String login) throws ApplicationException;

    public boolean isUserLockedOut(Long csmUserId) throws ApplicationException;

    public <T extends ActionResult> T doAction(Action<T> action)
        throws ApplicationException;

    public boolean isAllowed(Permission permission) throws ApplicationException;
}
