package edu.ualberta.med.biobank.server.applicationservice;

import edu.ualberta.med.biobank.common.exception.ExceptionUtils;
import edu.ualberta.med.biobank.common.reports.QueryCommand;
import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest.CommandType;
import edu.ualberta.med.biobank.common.security.ProtectionGroupPrivilege;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.server.logging.MessageGenerator;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.WritableApplicationServiceImpl;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.util.ClassCache;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.PropertyValueException;

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
    public SDKQueryResult executeQuery(SDKQuery query)
        throws ApplicationException {
        try {
            return super.executeQuery(query);
        } catch (ApplicationException ae) {
            Throwable cause = ExceptionUtils.findCauseInThrowable(ae,
                PropertyValueException.class);
            if (cause != null) {
                PropertyValueException pve = (PropertyValueException) cause;
                if (pve.getMessage().startsWith("not-null"))
                    throw new ValueNotSetException(pve.getPropertyName(), ae);
            }
            throw ae;
        }
    }

    @Override
    public void logActivity(String action, String site, String patientNumber,
        String inventoryID, String locationLabel, String details, String type)
        throws Exception {
        Log log = new Log();
        log.setAction(action);
        log.setSite(site);
        log.setPatientNumber(patientNumber);
        log.setInventoryId(inventoryID);
        log.setLocationLabel(locationLabel);
        log.setDetails(details);
        log.setType(type);
        logActivity(log);
    }

    @Override
    public void logActivity(Log log) throws Exception {
        Logger logger = Logger.getLogger("Biobank.Activity");
        logger.log(Level.toLevel("INFO"),
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

    @Override
    public void modifyPassword(String oldPassword, String newPassword)
        throws ApplicationException {
        BiobankSecurityUtil.modifyPassword(oldPassword, newPassword);
    }

    @Override
    public List<edu.ualberta.med.biobank.common.security.Group> getSecurityGroups()
        throws ApplicationException {
        return BiobankSecurityUtil.getSecurityGroups();
    }

    @Override
    public List<edu.ualberta.med.biobank.common.security.User> getSecurityUsers()
        throws ApplicationException {
        return BiobankSecurityUtil.getSecurityUsers();
    }

    @Override
    public void persistUser(edu.ualberta.med.biobank.common.security.User user)
        throws ApplicationException {
        BiobankSecurityUtil.persistUser(user);
    }

    @Override
    public void deleteUser(String login) throws ApplicationException {
        BiobankSecurityUtil.deleteUser(login);
    }

    @Override
    public edu.ualberta.med.biobank.common.security.User getCurrentUser()
        throws ApplicationException {
        return BiobankSecurityUtil.getCurrentUser();
    }

    @Override
    public edu.ualberta.med.biobank.common.security.Group persistGroup(
        edu.ualberta.med.biobank.common.security.Group group)
        throws ApplicationException {
        return BiobankSecurityUtil.persistGroup(group);
    }

    @Override
    public void deleteGroup(edu.ualberta.med.biobank.common.security.Group group)
        throws ApplicationException {
        BiobankSecurityUtil.deleteGroup(group);
    }

    @Override
    public void unlockUser(String userName) throws ApplicationException {
        BiobankSecurityUtil.unlockUser(userName);
    }

    @Override
    public List<ProtectionGroupPrivilege> getSecurityFeatures()
        throws ApplicationException {
        return BiobankSecurityUtil.getSecurityFeatures();
    }

    @Override
    public void checkVersion(String clientVersion) throws ApplicationException {
        BiobankVersionUtil.checkVersion(clientVersion);
    }

    @Override
    public String getServerVersion() {
        return BiobankVersionUtil.getServerVersion();
    }

}
