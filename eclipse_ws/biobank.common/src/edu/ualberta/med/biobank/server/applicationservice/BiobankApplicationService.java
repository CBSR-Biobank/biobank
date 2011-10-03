package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.reports.QueryCommand;
import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    /**
     * csmUserId will help to check this method is called by the user itself.
     */
    public void executeModifyPassword(Long csmUserId, String oldPassword,
        String newPassword) throws ApplicationException;

    public void unlockUser(String userNameToUnlock) throws ApplicationException;

    public List<Object> runReport(Report report, int maxResults, int firstRow,
        int timeout) throws ApplicationException;

    public void checkVersion(String clientVersion) throws ApplicationException;

    public String getServerVersion();

    public QueryHandle createQuery(QueryCommand qc) throws Exception;

    public List<Object> startQuery(QueryHandle qh) throws Exception;

    public void stopQuery(QueryHandle qh) throws Exception;

    public ScanProcessResult processScanResult(Map<RowColPos, Cell> cells,
        ProcessData processData, boolean rescanMode,
        Integer currentWorkingCenterId, Locale locale)
        throws ApplicationException;

    public CellProcessResult processCellStatus(Cell cell,
        ProcessData processData, Integer currentWorkingCenterId, Locale locale)
        throws ApplicationException;

    public List<String> executeGetSourceSpecimenUniqueInventoryIds(int numIds)
        throws ApplicationException;

    public String getUserPassword(String login) throws ApplicationException;

    public boolean isUserLockedOut(Long csmUserId) throws ApplicationException;

    public <T> T doAction(Action<T> action);

    public boolean isAllowed(Permission permission) throws ApplicationException;
}
