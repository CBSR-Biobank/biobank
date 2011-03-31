package edu.ualberta.med.biobank.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.ualberta.med.biobank.common.peer.LogPeer;
import edu.ualberta.med.biobank.model.Log;

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
        sql.append(LogPeer.USERNAME.getName());
        sql.append(COMMA + LogPeer.CENTER.getName());
        sql.append(COMMA + LogPeer.CREATED_AT.getName());
        sql.append(COMMA + LogPeer.ACTION.getName());
        sql.append(COMMA + LogPeer.PATIENT_NUMBER.getName());
        sql.append(COMMA + LogPeer.INVENTORY_ID.getName());
        sql.append(COMMA + LogPeer.LOCATION_LABEL.getName());
        sql.append(COMMA + LogPeer.DETAILS.getName());
        sql.append(COMMA + LogPeer.TYPE.getName());
        sql.append(") VALUES ('");
        sql.append(initString(log.getUsername()));
        sql.append("','");
        sql.append(initString(log.getCenter()));
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
