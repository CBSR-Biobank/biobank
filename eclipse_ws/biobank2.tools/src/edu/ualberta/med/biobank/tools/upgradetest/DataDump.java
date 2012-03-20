package edu.ualberta.med.biobank.tools.upgradetest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;

public class DataDump {

    protected Properties queryProps;

    protected Connection dbconnection;

    protected BufferedWriter bw;

    public DataDump(Connection connection, Properties queryProps,
        String filename) throws IOException {
        this.dbconnection = connection;
        this.queryProps = queryProps;

        File f = new File(filename);
        f.delete();

        bw = new BufferedWriter(new FileWriter(filename, true));
    }

    public void dispose() throws IOException, SQLException {
        bw.close();
        dbconnection.close();
    }

    public static Date getDateFromStr(String str) {
        if (str == null)
            return null;
        Date date = DateFormatter.parseToDateTime(str);
        if (date == null) {
            date = DateFormatter.parseToDate(str);
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

}
