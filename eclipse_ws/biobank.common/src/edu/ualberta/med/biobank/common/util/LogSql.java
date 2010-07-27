package edu.ualberta.med.biobank.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.server.logging.LogProperty;

public class LogSql {

    public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");

    public static final String COMMA = ",";

    /**
     * Returns a SQL Insert statement based on an Log Object instance.
     */
    public static String getLogMessageSQLStatement(Log log) {

        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO log (");
        sql.append(LogProperty.USERNAME);
        sql.append(COMMA + LogProperty.SITE);
        sql.append(COMMA + LogProperty.CREATED_AT);
        sql.append(COMMA + LogProperty.ACTION);
        sql.append(COMMA + LogProperty.PATIENT_NUMBER);
        sql.append(COMMA + LogProperty.INVENTORY_ID);
        sql.append(COMMA + LogProperty.LOCATION_LABEL);
        sql.append(COMMA + LogProperty.DETAILS);
        sql.append(COMMA + LogProperty.TYPE);
        sql.append(") VALUES ('");
        sql.append(initString(log.getUsername()));
        sql.append("','");
        sql.append(initString(log.getSite()));
        sql.append("','");
        sql.append(initString(log.getCreatedAt()));
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
        sql.append("','");
        sql.append(initString(log.getType()));
        sql.append("');");
        return sql.toString();
    }

    public static String initString(String str) {
        String test = "";
        if (str != null) {
            test = str.trim().replaceAll("'", "''");

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
