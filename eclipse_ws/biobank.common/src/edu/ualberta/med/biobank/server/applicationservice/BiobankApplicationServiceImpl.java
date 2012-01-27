package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.reports.QueryCommand;
import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest.CommandType;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankSessionAction;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.PrintedSsInvItem;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankServerException;
import edu.ualberta.med.biobank.server.logging.MessageGenerator;
import edu.ualberta.med.biobank.server.orm.BiobankORMDAOImpl;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;
import gov.nih.nci.system.util.ClassCache;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    public BiobankApplicationServiceImpl(ClassCache classCache) {
        super(classCache);
    }

    /**
     * How can we manage security using sql ??
     */
    @Override
    public <E> List<E> query(BiobankSQLCriteria sqlCriteria,
        String targetClassName) throws ApplicationException {
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
    public void logActivity(String action, String site, String patientNumber,
        String inventoryID, String locationLabel, String details, String type)
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
    @Override
    public void logActivity(Log log) throws Exception {
        Logger logger = Logger.getLogger("Biobank.Activity"); //$NON-NLS-1$
        logger.log(Level.toLevel("INFO"), //$NON-NLS-1$
            MessageGenerator.generateStringMessage(log));
    }

    @Override
    public List<Object> runReport(Report report, int maxResults, int firstRow,
        int timeout) throws ApplicationException {

        ReportData reportData = new ReportData(report);
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
        QueryHandleRequest qhr = new QueryHandleRequest(qc, CommandType.CREATE,
            null, this);
        return (QueryHandle) getWritableDAO(Site.class.getName()).query(
            new Request(qhr)).getResponse();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> startQuery(QueryHandle qh) throws Exception {
        QueryHandleRequest qhr = new QueryHandleRequest(null,
            CommandType.START, qh, this);
        return (List<Object>) getWritableDAO(Site.class.getName()).query(
            new Request(qhr)).getResponse();
    }

    @Override
    public void stopQuery(QueryHandle qh) throws Exception {
        QueryHandleRequest qhr = new QueryHandleRequest(null, CommandType.STOP,
            qh, this);
        getWritableDAO(Site.class.getName()).query(new Request(qhr))
            .getResponse();
    }

    @SuppressWarnings("nls")
    private static final String GET_USER_QRY = "from " + User.class.getName()
        + " where " + UserPeer.CSM_USER_ID.getName() + " = ?";

    @Override
    public void executeModifyPassword(Long csmUserId, String oldPassword,
        String newPassword, Boolean recvBulkEmails) throws ApplicationException {
        BiobankCSMSecurityUtil.modifyPassword(csmUserId, oldPassword,
            newPassword);
        List<User> users = query(new HQLCriteria(GET_USER_QRY,
            Arrays.asList(csmUserId)));
        if (users.size() != 1) {
            throw new ApplicationException("Problem with HQL result size"); //$NON-NLS-1$
        }
        User user = users.get(0);
        user.setNeedPwdChange(false);
        if (recvBulkEmails != null)
            user.setRecvBulkEmails(recvBulkEmails);
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

    // @Override
    // @Deprecated
    // public ScanProcessResult processScanResult(Map<RowColPos, Cell> cells,
    // ProcessData processData, boolean isRescanMode,
    // Integer currentWorkingCenterId, Locale locale)
    // throws ApplicationException {
    // try {
    // ServerProcess process = processData.getProcessInstance(this,
    // currentWorkingCenterId, locale);
    // return process.processScanResult(cells, isRescanMode);
    // } catch (ApplicationException ae) {
    // throw ae;
    // } catch (Exception e) {
    // throw new ApplicationException(e);
    // }
    // }
    //
    // @Override
    // @Deprecated
    // public CellProcessResult processCellStatus(Cell cell,
    // ProcessData processData, Integer currentWorkingCenterId, Locale locale)
    // throws ApplicationException {
    // try {
    // ServerProcess process = processData.getProcessInstance(this,
    // currentWorkingCenterId, locale);
    // return process.processCellStatus(cell);
    // } catch (ApplicationException ae) {
    // throw ae;
    // } catch (Exception e) {
    // throw new ApplicationException(e);
    // }
    // }

    private static final int SS_INV_ID_LENGTH = 12;

    @SuppressWarnings("nls")
    private static final String SS_INV_ID_ALPHABET =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int SS_INV_ID_ALPHABET_LENGTH = SS_INV_ID_ALPHABET
        .length();

    private static final int SS_INV_ID_GENERATE_RETRIES = (int) Math.pow(
        SS_INV_ID_ALPHABET_LENGTH, SS_INV_ID_ALPHABET_LENGTH);

    @SuppressWarnings("nls")
    private static final String SS_INV_ID_UNIQ_BASE_QRY = "SELECT count(*) "
        + "FROM printed_ss_inv_item where txt=\"{id}\"";

    @Override
    public List<String> executeGetSourceSpecimenUniqueInventoryIds(int numIds)
        throws ApplicationException {
        boolean isUnique;
        int genRetries;
        Random r = new Random();
        StringBuilder newInvId;
        List<String> result = new ArrayList<String>();

        while (result.size() < numIds) {
            isUnique = false;
            genRetries = 0;
            newInvId = new StringBuilder();

            while (!isUnique && (genRetries < SS_INV_ID_GENERATE_RETRIES)) {
                for (int j = 0; j < SS_INV_ID_LENGTH; ++j) {
                    newInvId.append(SS_INV_ID_ALPHABET.charAt(r
                        .nextInt(SS_INV_ID_ALPHABET_LENGTH)));
                    genRetries++;
                }

                // check database if string is unique
                String potentialInvId = newInvId.toString();
                String qry = SS_INV_ID_UNIQ_BASE_QRY.replace("{id}", //$NON-NLS-1$
                    potentialInvId);

                List<BigInteger> count = privateQuery(new BiobankSQLCriteria(
                    qry), PrintedSsInvItem.class.getName());

                if (count.get(0).equals(BigInteger.ZERO)) {
                    // add new inventory id to the database
                    isUnique = true;
                    result.add(potentialInvId);
                    PrintedSsInvItem newInvIdItem = new PrintedSsInvItem();
                    newInvIdItem.setTxt(potentialInvId);
                    SDKQuery query = new InsertExampleQuery(newInvIdItem);
                    executeQuery(query);
                }
            }

            if (genRetries >= SS_INV_ID_GENERATE_RETRIES) {
                // cannot generate any more unique strings
                throw new BiobankServerException(
                    "cannot generate any more source specimen inventory IDs"); //$NON-NLS-1$
            }

        }
        return result;
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
    public <T extends ActionResult> T doAction(Action<T> action)
        throws ApplicationException {
        Request request =
            new Request(new AppServiceAction<T>(action, this));
        request.setDomainObjectName(Site.class.getName());

        Response response = query(request);

        @SuppressWarnings("unchecked")
        T tmp = (T) response.getResponse();

        return tmp;
    }

    @Override
    public boolean isAllowed(Permission permission) throws ApplicationException {
        return ((BiobankORMDAOImpl) this.getWritableDAO(Site.class.getName()))
            .isAllowed(permission);
    }

    public class AppServiceAction<T extends ActionResult> {

        public Action<T> action;
        public BiobankApplicationService appService;

        public AppServiceAction(Action<T> action,
            BiobankApplicationService appService) {
            this.action = action;
            this.appService = appService;
        }
    }
}
