package edu.ualberta.med.biobank.server.orm;

import edu.ualberta.med.biobank.common.reports.QueryHandle;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest;
import edu.ualberta.med.biobank.common.reports.QueryHandleRequest.CommandType;
import edu.ualberta.med.biobank.common.reports.QueryProcess;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationServiceImpl.ExampleRequestData;
import edu.ualberta.med.biobank.server.applicationservice.ReportData;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import edu.ualberta.med.biobank.server.reports.ReportRunner;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.dao.orm.WritableORMDAOImpl;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
                log.error("JDBC Exception in ORMDAOImpl ", ex);
                throw new DAOException("JDBC Exception in ORMDAOImpl ", ex);
            } catch (Exception e) {
                log.error("Exception ", e);
                throw new DAOException("Exception in ORMDAOImpl ", e);
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
                        "DataAccessResourceFailureException in ORMDAOImpl ", e);
                    throw new DAOException(
                        "DataAccessResourceFailureException in ORMDAOImpl ", e);
                } catch (IllegalStateException e) {
                    log.error("IllegalStateException in ORMDAOImpl ", e);
                    throw new DAOException(
                        "IllegalStateException in ORMDAOImpl ", e);
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
        } else if (obj instanceof ExampleRequestData) { // PATCH
            return query((ExampleRequestData) obj);
        }
        return super.query(request);
    }

    // PATCH
    private Response query(ExampleRequestData object) {
        Response response = new Response();

        Session session = getSession();

        Address address = new Address();
        address.setCity("argmonton");

        ActivityStatus active = (ActivityStatus) session
            .createQuery(
                "SELECT activityStatus FROM " + ActivityStatus.class.getName()
                    + " activityStatus WHERE activityStatus.name = ?")
            .setParameter(0, "Active").list().get(0);

        Site site = new Site();
        site.setAddress(address); // address is automatically saved via cascade
        site.setActivityStatus(active);
        site.setName("example_"
            + new BigInteger(130, new Random()).toString(32));
        site.setNameShort(site.getName() + "_short");

        session.save(site);

        return response;
    }

    // PATCH

    protected Response query(Request request, BiobankSQLCriteria sqlCriteria)
        throws Exception {
        log.info("SQL Query :" + sqlCriteria.getSqlString());
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
