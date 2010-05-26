package edu.ualberta.med.biobank.server.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import edu.ualberta.med.biobank.model.Log;

/**
 * 
 * Runnable class responsible for inserting the log messages in batch
 * 
 * Copy from CLM
 */
public class JDBCLogExecutor implements Runnable {

    private Log log;
    private Properties props;
    private String dbUrl = null;
    private String dbDriverClass = null;
    private String dbUser = null;
    private String dbPwd = null;

    public static final String COMMA = ",";

    public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm");

    /**
     * Constructor for JDBCExcecutor.
     * 
     * @param rows -
     */
    public JDBCLogExecutor(Log log, Properties props) {
        this.log = log;
        setProps(props);
        setDBProperties();
    }

    private void setDBProperties() {
        if (this.props != null) {

            this.setDbDriverClass(StringUtils.initString(this.props
                .getProperty("hibernate.connection.driver_class")));
            this.setDbPwd(StringUtils.initString(this.props
                .getProperty("hibernate.connection.password")));
            this.setDbUser(StringUtils.initString(this.props
                .getProperty("hibernate.connection.username")));
            this.setDbUrl(StringUtils.initString(this.props
                .getProperty("hibernate.connection.url")));
        }

    }

    public void run() {
        try {
            Connection conn = null;
            Statement stmt = null;
            try {
                conn = createConn();
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                String statement = getLogMessageSQLStatement(log);
                stmt.execute(statement);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                conn.close();
                throw e;
            } finally {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            writeMsgToTmpFile(ex);
        }
    }

    protected Connection createConn() {
        Connection con = null;
        try {
            if (getDbDriverClass() != null) {
                Class.forName(getDbDriverClass());
            }
            con = DriverManager.getConnection(getDbUrl(), getDbUser(),
                getDbPwd());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return con;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public String getDbDriverClass() {

        return dbDriverClass;
    }

    public void setDbDriverClass(String dbDriverClass) {
        this.dbDriverClass = dbDriverClass;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }

    public String getDbUrl() {

        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    /**
     * Returns a SQL Insert statement based on an Log Object instance.
     */
    private String getLogMessageSQLStatement(Log log) {

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO log (");
        sql.append(LogProperty.USERNAME);
        sql.append(COMMA + LogProperty.DATE);
        sql.append(COMMA + LogProperty.ACTION);
        sql.append(COMMA + LogProperty.PATIENT_NUMBER);
        sql.append(COMMA + LogProperty.INVENTORY_ID);
        sql.append(COMMA + LogProperty.LOCATION_LABEL);
        sql.append(COMMA + LogProperty.DETAILS);
        sql.append(") VALUES ('");
        sql.append(initString(log.getUsername()));
        sql.append("','");
        sql.append(initString(log.getDate()));
        sql.append("','");
        sql.append(initString(log.getAction()));
        sql.append("','");
        sql.append(initString(log.getPatientNumber()));
        sql.append("','");
        sql.append(initString(log.getInventoryId()));
        sql.append("','");
        sql.append(initString(log.getLocationLabel()));
        sql.append("','");
        sql.append(initString(log.getDetails()));
        sql.append("');");
        return sql.toString();
    }

    public String initString(String str) {
        String test = "";
        if (str != null) {
            test = str.trim();
        }
        return test;
    }

    public String initString(Date date) {
        if (date == null) {
            return "";
        }
        return dateTimeFormatter.format(date);
    }

    /**
     * Writes fatal errors to a log file on the system's current directory.
     * 
     * @param t
     */
    private static void writeMsgToTmpFile(Throwable t) {
        FileWriter writer = null;
        try {
            File f = new File("biobanklogappender" + System.currentTimeMillis()
                + ".log");
            writer = new FileWriter(f);
            writer.write(getErrorAndStack(t));
            writer.flush();

        } catch (Exception e) {
        } finally {
            try {
                writer.close();
            } catch (Exception e1) {
            }
        }
    }

    public static String getErrorAndStack(Throwable t) {
        if (t == null) {
            return null;
        }
        return t.getMessage() + System.getProperty("line.separator")
            + getStackTrace(t).toString();
    }

    public static StringBuffer getStackTrace(Throwable t) {
        StringWriter stringWriter = new java.io.StringWriter();
        t.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.getBuffer();
    }

}
