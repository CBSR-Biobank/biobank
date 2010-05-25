package edu.ualberta.med.biobank.server.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.ualberta.med.biobank.model.Log;

/**
 * 
 * SQLGenerator is a utility class that generates SQL Insert statements for
 * persisting LogMessage objects.
 * 
 * Copy from CLM
 */
public class SQLGenerator {

    public static final String USERNAME = "USERNAME";
    public static final String DATE = "DATE";
    public static final String ACTION = "ACTION";
    public static final String PATIENT_NUMBER = "PATIENT_NUMBER";
    public static final String INVENTORY_ID = "INVENTORY_ID";
    public static final String LOCATION_LABEL = "LOCATION_LABEL";
    public static final String DETAILS = "DETAILS";

    public static final String COMMA = ",";

    public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm");

    /**
     * Returns an the SQL Insert Statement for a given Log.
     */
    public static String getSQLStatement(Log log) {
        if (log == null) {
            return null;
        }
        return getLogMessageSQLStatement(log);
    }

    /**
     * Returns a SQL Insert statement based on an Log Object instance.
     */
    private static String getLogMessageSQLStatement(Log log) {

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO log (");
        sql.append(USERNAME);
        sql.append(COMMA + DATE);
        sql.append(COMMA + ACTION);
        sql.append(COMMA + PATIENT_NUMBER);
        sql.append(COMMA + INVENTORY_ID);
        sql.append(COMMA + LOCATION_LABEL);
        sql.append(COMMA + DETAILS);
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

    public static String initString(String str) {
        String test = "";
        if (str != null) {
            test = str.trim();
        }
        return test;
    }

    public static String initString(Date date) {
        if (date == null) {
            return "";
        }
        return dateTimeFormatter.format(date);
    }
}
