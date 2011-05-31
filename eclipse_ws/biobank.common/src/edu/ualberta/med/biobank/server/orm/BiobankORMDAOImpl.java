package edu.ualberta.med.biobank.server.orm;

import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest.CommandType;
import edu.ualberta.med.biobank.common.reports.QueryProcess;
import edu.ualberta.med.biobank.common.wrappers.BiobankSessionAction;
import edu.ualberta.med.biobank.server.applicationservice.ReportData;
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
import org.hibernate.JDBCException;
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
        }
        return super.query(request);
    }

    protected Response query(@SuppressWarnings("unused") Request request,
        BiobankSessionAction sessionAction) {

        Response response = new Response();

        Object actionResult = sessionAction.doAction(getSession());
        SDKQueryResult queryResult = new SDKQueryResult(actionResult);

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
        QueryHandleRequest queryHandleRequest) throws DAOException {

        CommandType command = queryHandleRequest.getCommandType();

        if (command.equals(CommandType.CREATE)) {
            QueryHandle handle = new QueryHandle(nextHandleId.incrementAndGet());
            try {
                queryMap.put(handle,
                    new QueryProcess(queryHandleRequest.getQueryCommand(),
                        queryHandleRequest.getAppService()));
            } catch (DataAccessResourceFailureException e) {
                log.error("DataAccessResourceFailureException in ORMDAOImpl ",
                    e);
                throw new DAOException(
                    "DataAccessResourceFailureException in ORMDAOImpl ", e);
            } catch (IllegalStateException e) {
                log.error("IllegalStateException in ORMDAOImpl ", e);
                throw new DAOException("IllegalStateException in ORMDAOImpl ",
                    e);
            }
            return new Response(handle);
        } else if (command.equals(CommandType.STOP)) {
            QueryHandle handle = queryHandleRequest.getQueryHandle();
            queryMap.get(handle).stop();
            return new Response();
        } else if (command.equals(CommandType.START)) {
            try {
                QueryHandle handle = queryHandleRequest.getQueryHandle();
                return queryMap.get(handle).start(getSession());
            } catch (ApplicationException e) {
                throw new DAOException(e);
            } finally {
                queryMap.remove(queryHandleRequest.getQueryHandle());
            }
        }

        return null;
    }

    protected Response query(Request request, BiobankSQLCriteria sqlCriteria)
        throws DAOException {
        try {
            log.info("SQL Query :" + sqlCriteria.getSqlString());
            Response rsp = new Response();
            HibernateCallback callBack = getExecuteSQLQueryHibernateCallback(
                sqlCriteria.getSqlString(), request.getFirstRow() == null ? -1
                    : request.getFirstRow(), getResultCountPerQuery());
            List<?> rs = (List<?>) getHibernateTemplate().execute(callBack);
            rsp.setRowCount(rs.size());
            rsp.setResponse(rs);
            return rsp;
        } catch (JDBCException ex) {
            log.error("JDBC Exception in ORMDAOImpl ", ex);
            throw new DAOException("JDBC Exception in ORMDAOImpl ", ex);
        } catch (Exception e) {
            log.error("Exception ", e);
            throw new DAOException("Exception in ORMDAOImpl ", e);
        }
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