package edu.ualberta.med.biobank.server.orm;

import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.dao.orm.WritableORMDAOImpl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Session;
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

    protected static Logger log = Logger.getLogger(BiobankORMDAOImpl.class
        .getName());

    @Override
    public Response query(Request request) throws DAOException {
        Object obj = request.getRequest();
        if (obj instanceof BiobankSQLCriteria) {
            try {
                return query(request, (BiobankSQLCriteria) obj);
            } catch (JDBCException ex) {
                log.error("JDBC Exception in ORMDAOImpl ", ex);
                throw new DAOException("JDBC Exception in ORMDAOImpl ", ex);
            } catch (Exception e) {
                log.error("Exception ", e);
                throw new DAOException("Exception in ORMDAOImpl ", e);
            }
        }
        return super.query(request);
    }

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
