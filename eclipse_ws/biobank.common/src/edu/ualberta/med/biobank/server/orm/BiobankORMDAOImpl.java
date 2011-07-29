package edu.ualberta.med.biobank.server.orm;

import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest.CommandType;
import edu.ualberta.med.biobank.common.reports.QueryProcess;
import edu.ualberta.med.biobank.server.applicationservice.ReportData;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import edu.ualberta.med.biobank.server.reports.ReportRunner;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.dao.orm.WritableORMDAOImpl;

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
        if (obj instanceof ReportData) {
            Response rsp = new Response();
            ReportData data = (ReportData) obj;

            ReportRunner reportRunner = new ReportRunner(getSession(), data);
            List<?> results = reportRunner.run();

            rsp.setResponse(results);

            return rsp;
        } else if (obj instanceof BiobankSQLCriteria) {
            try {
                return query(request, (BiobankSQLCriteria) obj);
            } catch (JDBCException ex) {
                log.error("JDBC Exception in ORMDAOImpl ", ex); //$NON-NLS-1$
                throw new DAOException("JDBC Exception in ORMDAOImpl ", ex); //$NON-NLS-1$
            } catch (Exception e) {
                log.error("Exception ", e); //$NON-NLS-1$
                throw new DAOException("Exception in ORMDAOImpl ", e); //$NON-NLS-1$
            }
        } else if (request.getRequest() instanceof QueryHandleRequest) {
            QueryHandleRequest qhr = (QueryHandleRequest) request.getRequest();
            if (qhr.getCommandType().equals(CommandType.CREATE)) {
                QueryHandle handle = new QueryHandle(
                    nextHandleId.incrementAndGet());
                try {
                    queryMap.put(handle, new QueryProcess(
                        qhr.getQueryCommand(), qhr.getAppService()));
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
            } else if (qhr.getCommandType().equals(CommandType.STOP)) {
                queryMap.get(qhr.getQueryHandle()).stop();
                return new Response();
            } else {
                try {
                    return queryMap.get(qhr.getQueryHandle()).start(
                        getSession());
                } catch (ApplicationException e) {
                    throw new DAOException(e);
                } finally {
                    queryMap.remove(qhr.getQueryHandle());
                }
            }
        }
        return super.query(request);
    }

    protected Response query(Request request, BiobankSQLCriteria sqlCriteria)
        throws Exception {
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
