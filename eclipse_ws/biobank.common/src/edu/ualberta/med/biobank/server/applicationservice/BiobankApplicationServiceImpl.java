package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.reports.ReportInput;
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.common.permission.GlobalAdminPermission;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.reports.QueryCommand;
import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest.CommandType;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankSessionAction;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.logging.MessageGenerator;
import edu.ualberta.med.biobank.server.orm.BiobankORMDAOImpl;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;
import gov.nih.nci.system.util.ClassCache;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Implementation of the BiobankApplicationService interface. This class will be only on the server
 * side.
 * 
 * See build.properties of the sdk for the generator configuration + application-config*.xml for the
 * generated files.
 */
public class BiobankApplicationServiceImpl
    extends WritableApplicationServiceImpl
    implements BiobankApplicationService {

    private static final Bundle bundle = new CommonBundle();

    private MaintenanceMode maintenanceMode;

    public BiobankApplicationServiceImpl(ClassCache classCache) {
        super(classCache);
        this.maintenanceMode = MaintenanceMode.NONE;
    }

    /**
     * How can we manage security using sql ??
     */
    @Override
    public <E> List<E> query(
        BiobankSQLCriteria sqlCriteria,
        String targetClassName)
        throws ApplicationException {

        return privateQuery(sqlCriteria, targetClassName);
    }

    @Override
    protected Request prepareRequest(SDKQuery query, String classname) {
        Request request = super.prepareRequest(query, classname);

        // super.prepareRequest replaces the request of SearchHQLQuery, switch
        // it back to the query if it's a BiobankSessionAction
        if (query instanceof BiobankSessionAction) {
            request.setRequest(query);
        }

        return request;
    }

    @Override
    public void logActivity(
        String action,
        String site,
        String patientNumber,
        String inventoryID,
        String locationLabel,
        String details,
        String type)
        throws Exception {

        Log log = new Log();
        log.setAction(action);
        log.setCenter(site);
        log.setPatientNumber(patientNumber);
        log.setInventoryId(inventoryID);
        log.setLocationLabel(locationLabel);
        log.setDetails(details);
        log.setType(type);
        logActivity(log);
    }

    /**
     * See log4j.xml: it should contain the Biobank.Activity appender
     */
    @SuppressWarnings("nls")
    @Override
    public void logActivity(Log log) throws Exception {
        Logger logger = Logger.getLogger("Biobank.Activity");
        logger.log(Level.toLevel("INFO"), MessageGenerator.generateStringMessage(log));
    }

    @Override
    public List<Object> runReport(
        ReportInput reportInput,
        int maxResults,
        int firstRow,
        int timeout)
        throws ApplicationException {

        ReportData reportData = new ReportData(reportInput);
        reportData.setMaxResults(maxResults);
        reportData.setFirstRow(firstRow);
        reportData.setTimeout(timeout);

        Request request = new Request(reportData);
        request.setIsCount(Boolean.FALSE);
        request.setFirstRow(0);
        request.setDomainObjectName(Report.class.getName());

        Response response = query(request);

        @SuppressWarnings("unchecked")
        List<Object> results = (List<Object>) response.getResponse();

        return results;
    }

    @Override
    public QueryHandle createQuery(QueryCommand qc) throws Exception {
        QueryHandleRequest qhr = new QueryHandleRequest(qc, CommandType.CREATE, null, this);
        return (QueryHandle) getWritableDAO(Site.class.getName()).query(
            new Request(qhr)).getResponse();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> startQuery(QueryHandle qh) throws Exception {
        QueryHandleRequest qhr = new QueryHandleRequest(null, CommandType.START, qh, this);
        return (List<Object>) getWritableDAO(Site.class.getName()).query(
            new Request(qhr)).getResponse();
    }

    @Override
    public void stopQuery(QueryHandle qh) throws Exception {
        QueryHandleRequest qhr = new QueryHandleRequest(null, CommandType.STOP, qh, this);
        getWritableDAO(Site.class.getName()).query(new Request(qhr)).getResponse();
    }

    @SuppressWarnings("nls")
    private static final String GET_USER_QRY =
        "FROM " + User.class.getName() + " WHERE " + UserPeer.CSM_USER_ID.getName() + " = ?";

    @SuppressWarnings("nls")
    @Override
    public void executeModifyPassword(
        Long csmUserId,
        String oldPassword,
        String newPassword,
        Boolean recvBulkEmails)
        throws ApplicationException {

        BiobankCSMSecurityUtil.modifyPassword(csmUserId, oldPassword, newPassword);
        List<User> users = query(new HQLCriteria(GET_USER_QRY, Arrays.asList(csmUserId)));
        if (users.size() != 1) {
            throw new LocalizedException(bundle.tr("Problem with HQL result size").format());
        }
        User user = users.get(0);
        user.setNeedPwdChange(false);
        if (recvBulkEmails != null) {
            user.setRecvBulkEmails(recvBulkEmails);
        }
        executeQuery(new UpdateExampleQuery(user));
    }

    @Override
    public void unlockUser(String userNameToUnlock) throws ApplicationException {
        BiobankCSMSecurityUtil.unlockUser(userNameToUnlock);
    }

    @Override
    public void checkVersion(String clientVersion) throws ApplicationException {
        BiobankVersionUtil.checkVersion(clientVersion);
    }

    @Override
    public String getServerVersion() {
        return BiobankVersionUtil.getServerVersion();
    }

    @Override
    public String getUserPassword(String login) throws ApplicationException {
        return BiobankCSMSecurityUtil.getUserPassword(login);
    }

    @Override
    public boolean isUserLockedOut(Long csmUserId) throws ApplicationException {
        return BiobankCSMSecurityUtil.isUserLockedOut(csmUserId);
    }

    @Override
    public <T extends ActionResult> T doAction(Action<T> action) throws ApplicationException {
        try {
            Request request = new Request(new AppServiceAction<T>(action, this));
            request.setDomainObjectName(Site.class.getName());

            Response response = query(request);

            @SuppressWarnings("unchecked")
            T tmp = (T) response.getResponse();

            return tmp;

        } catch (ApplicationException e) {
            if (e.getCause() instanceof ActionException)
                throw (ActionException) e.getCause();
            throw e;
        }
    }

    @Override
    public boolean isAllowed(Permission permission) throws ApplicationException {
        return ((BiobankORMDAOImpl) this.getWritableDAO(Site.class.getName())).isAllowed(permission);
    }

    @SuppressWarnings("nls")
    public static final LString MAINTENANCE_MODE_PERMISSION_ERROR =
        bundle.tr("The user does not have the valid permissions to change the maintenance mode").format();

    @Override
    public void maintenanceMode(MaintenanceMode mode) throws ApplicationException,
        LocalizedException {
        if (isAllowed(new GlobalAdminPermission())) {
            maintenanceMode = mode;
        } else {
            throw new LocalizedException(MAINTENANCE_MODE_PERMISSION_ERROR);
        }
    }

    @Override
    public MaintenanceMode maintenanceMode() {
        return maintenanceMode;
    }

    public class AppServiceAction<T extends ActionResult> {

        public Action<T> action;
        public BiobankApplicationService appService;

        public AppServiceAction(Action<T> action, BiobankApplicationService appService) {
            this.action = action;
            this.appService = appService;
        }
    }
}
