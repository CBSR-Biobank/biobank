package edu.ualberta.med.biobank.server.orm;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest.CommandType;
import edu.ualberta.med.biobank.common.reports.QueryProcess;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankSessionAction;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.ReportData;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import edu.ualberta.med.biobank.server.reports.ReportRunner;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.dao.orm.WritableORMDAOImpl;
import gov.nih.nci.system.query.SDKQueryResult;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * Hibernate calls are made in this server side class. It extends the default
 * WritableORMDAOImpl class.
 * 
 * 
 * See build.properties of the sdk for the generator configuration +
 * application-config*.xml for the generated files.
 */
public class BiobankORMDAOImpl extends WritableORMDAOImpl {
    private static AtomicInteger nextHandleId = new AtomicInteger(0);
    private static final HashMap<QueryHandle, QueryProcess> queryMap = new HashMap<QueryHandle, QueryProcess>();

    @Override
    public Response query(Request request) throws DAOException {
        Object obj = request.getRequest();
        if (obj instanceof BiobankSessionAction) {
            return query(request, (BiobankSessionAction) obj);
        } else if (obj instanceof ReportData) {
            return query(request, (ReportData) obj);
        } else if (obj instanceof BiobankSQLCriteria) {
            return query(request, (BiobankSQLCriteria) obj);
        } else if (obj instanceof QueryHandleRequest) {
            return query(request, (QueryHandleRequest) obj);
        } else if (obj instanceof Action) {
            return query((Action<?>) obj);
        }
        return super.query(request);
    }

    private <T> Response query(Action<T> action) {
        Session session = getSession();

        // TODO: pass the logged in user
        User user = null;
        action.isAllowed(user, session);

        T actionResult = action.doAction(session);

        session.flush();
        session.clear();

        Response response = new Response();
        response.setResponse(actionResult);

        return response;
    }

    protected Response query(@SuppressWarnings("unused") Request request,
        BiobankSessionAction sessionAction) throws BiobankSessionException {

        Session session = getSession();

        Object actionResult = sessionAction.doAction(session);

        session.flush();
        session.clear();

        SDKQueryResult queryResult = new SDKQueryResult(actionResult);

        Response response = new Response();
        response.setResponse(queryResult);

        return response;
    }

    protected Response query(@SuppressWarnings("unused") Request request,
        ReportData reportData) {

        Response rsp = new Response();

        ReportRunner reportRunner = new ReportRunner(getSession(), reportData);
        List<?> results = reportRunner.run();

        rsp.setResponse(results);

        return rsp;
    }

    protected Response query(@SuppressWarnings("unused") Request request,
        QueryHandleRequest qhr) throws DAOException {

        CommandType command = qhr.getCommandType();

        if (command.equals(CommandType.CREATE)) {
            QueryHandle handle = new QueryHandle(nextHandleId.incrementAndGet());
            try {
                queryMap.put(handle, new QueryProcess(qhr.getQueryCommand(),
                    qhr.getAppService()));
            } catch (DataAccessResourceFailureException e) {
                log.error(
                    "DataAccessResourceFailureException in ORMDAOImpl ", e); //$NON-NLS-1$
                throw new DAOException(
                    "DataAccessResourceFailureException in ORMDAOImpl ", e); //$NON-NLS-1$
            } catch (IllegalStateException e) {
                log.error("IllegalStateException in ORMDAOImpl ", e); //$NON-NLS-1$
                throw new DAOException(
                    "IllegalStateException in ORMDAOImpl ", e); //$NON-NLS-1$
            }
            return new Response(handle);
        } else if (command.equals(CommandType.STOP)) {
            queryMap.get(qhr.getQueryHandle()).stop();
            return new Response();
        } else if (command.equals(CommandType.START)) {
            try {
                return queryMap.get(qhr.getQueryHandle()).start(getSession());
            } catch (ApplicationException e) {
                throw new DAOException(e);
            } finally {
                queryMap.remove(qhr.getQueryHandle());
            }
        }

        return null;
    }

    protected Response query(Request request, BiobankSQLCriteria sqlCriteria) {
        log.info("SQL Query :" + sqlCriteria.getSqlString()); //$NON-NLS-1$
        Response rsp = new Response();
        HibernateCallback callBack = getExecuteSQLQueryHibernateCallback(
            sqlCriteria.getSqlString(), request.getFirstRow() == null ? -1
                : request.getFirstRow(), getResultCountPerQuery());
        List<?> rs = (List<?>) getHibernateTemplate().execute(callBack);
        rsp.setRowCount(rs.size());
        rsp.setResponse(rs);
        return rsp;
    }

    protected HibernateCallback getExecuteSQLQueryHibernateCallback(
        final String sql, final int firstResult, final int maxResult) {
        HibernateCallback callBack = new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session)
                throws HibernateException, SQLException {
                Query query = session.createSQLQuery(sql);
                query.setFirstResult(firstResult);
                query.setMaxResults(maxResult);
                return query.list();
            }
        };
        return callBack;
    }
}