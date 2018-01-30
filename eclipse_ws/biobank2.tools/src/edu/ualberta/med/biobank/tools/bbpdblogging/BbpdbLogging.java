package edu.ualberta.med.biobank.tools.bbpdblogging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.LogSql;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.tools.GenericAppArgs;

@SuppressWarnings("nls")
public class BbpdbLogging {

    private static String USAGE = "Usage: bbpdblogging [options]\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    private static final Logger LOGGER = Logger.getLogger(BbpdbLogging.class
        .getName());

    private static String BBPDB_LOG_BASE_QUERY =
        "FROM logging JOIN users on users.user_nr=logging.user_nr "
            + "JOIN forms ON forms.form_nr=logging.form_nr "
            + "JOIN actions ON actions.shortform=logging.action "
            + "JOIN patient ON patient.patient_nr=logging.patient_nr "
            + "LEFT JOIN freezer ON freezer.index_nr=logging.findex_nr "
            + "LEFT JOIN cabinet ON cabinet.index_nr=logging.cindex_nr "
            + "WHERE timestamp < '2010-05-18' ORDER BY timestamp";

    private static String BBPDB_LOG_COUNT_QUERY = "SELECT count(*) "
        + BBPDB_LOG_BASE_QUERY;

    private static String BBPDB_LOG_QUERY =
        "SELECT login_id,timestamp,form_name,"
            + "actions.action,dec_chr_nr,logging.inventory_id,"
            + "details,fnum,rack,box,cell,cnum,drawer,bin,binpos "
            + BBPDB_LOG_BASE_QUERY;

    private static String BBPDB_PV_QUERY = "SELECT dec_chr_nr, visit_nr, "
        + "clinic_site, date_received, date_taken, worksheet "
        + "FROM patient_visit "
        + "join patient on patient.patient_nr=patient_visit.patient_nr "
        + "where visit_nr = ?";

    private static Pattern VISIT_NR_DETAILS_RE = Pattern
        .compile("Visit #(\\d+)");

    @SuppressWarnings("unused")
    private final GenericAppArgs args;

    private final Connection bbpdbCon;

    private final Connection biobank2Con;

    public static void main(String[] argv) {
        try {
            GenericAppArgs args = new GenericAppArgs(argv);
            if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }
            new BbpdbLogging(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BbpdbLogging(GenericAppArgs args) throws Exception {
        this.args = args;
        PropertyConfigurator.configure("conf/log4j.properties");
        bbpdbCon = DriverManager.getConnection("jdbc:mysql://" + args.hostOption()
            + ":3306/bbpdb", "dummy", "ozzy498");

        biobank2Con =
            DriverManager.getConnection("jdbc:mysql://" + args.hostOption()
                + ":3306/biobank2", "dummy", "ozzy498");

        importPass1();
        // importPass2();
    }

    private void importPass1() throws Exception {
        Statement s = bbpdbCon.createStatement();
        s.execute(BBPDB_LOG_COUNT_QUERY);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numLogRecords = rs.getInt(1);

        PreparedStatement ps;
        ps = bbpdbCon.prepareStatement(BBPDB_LOG_QUERY,
            ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ps.setFetchSize(Integer.MIN_VALUE);
        rs = ps.executeQuery();

        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        int count = 0;
        while (rs.next()) {
            String loginId = rs.getString(1);
            Timestamp timestamp = rs.getTimestamp(2);
            String formName = rs.getString(3);
            String action = rs.getString(4);
            String pnumber = rs.getString(5);
            String inventoryId = rs.getString(6);
            String details = rs.getString(7);
            Integer fnum = rs.getInt(8);
            String rack = rs.getString(9);
            Integer box = rs.getInt(10);
            String cell = rs.getString(11);
            Integer cnum = rs.getInt(12);
            String drawer = rs.getString(13);
            Integer bin = rs.getInt(14);
            String binpos = rs.getString(15);

            ++count;

            String location = null;
            if ((fnum != null) && (rack != null) && (box != null)
                && (cell != null)) {
                location = String.format("%02d%s%02d%s", fnum, rack, box, cell);
            } else if ((cnum != null) && (drawer != null) && (bin != null)
                && (binpos != null)) {
                location = String.format("%02d%s%02d%s", cnum, drawer, bin,
                    binpos);
            }

            Log logMsg = new Log();
            logMsg.setUsername(loginId);
            logMsg.setCreatedAt(timestamp);
            logMsg.setType(formName);
            logMsg.setAction(action);
            logMsg.setPatientNumber(pnumber);
            logMsg.setInventoryId(inventoryId);
            logMsg.setDetails(details);
            logMsg.setLocationLabel(location);

            s = biobank2Con.createStatement();
            s.execute(LogSql.getLogMessageSQLStatement(logMsg));

            System.out.println("wrote log record " + count + " of "
                + numLogRecords);
        }

    }

    @SuppressWarnings("unused")
    private void importPass2() throws Exception {
        Statement s = biobank2Con.createStatement();
        s.execute("SELECT * FROM log WHERE created_at < '2010-05-18'");
        ResultSet rs = s.getResultSet();

        while (rs.next()) {
            Integer id = rs.getInt(1);
            String action = rs.getString(5);
            String details = rs.getString(9);
            String newDetails = null;

            if (details.startsWith("Visit")) {
                if (action.equals("Select")) {
                    Matcher visitNrMatcher = VISIT_NR_DETAILS_RE
                        .matcher(details);
                    if (visitNrMatcher.find()) {
                        newDetails = convertPvDetails(new Integer(
                            visitNrMatcher.group(1)));
                    }
                }
            }

            if (newDetails != null) {
                PreparedStatement ps =
                    biobank2Con
                        .prepareStatement("UPDATE log SET details = ? where id = ?");
                ps.setString(1, newDetails);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        }
    }

    private String convertPvDetails(Integer visitNr) throws Exception {
        PreparedStatement ps = bbpdbCon.prepareStatement(BBPDB_PV_QUERY);
        ps.setInt(1, visitNr.intValue());
        ResultSet rs = ps.executeQuery();

        if (rs == null) {
            throw new Exception("Database query returned null");
        }

        Timestamp dateProcessed = null;
        String worksheet = null;
        int count = 0;

        while (rs.next()) {
            dateProcessed = rs.getTimestamp(4);
            worksheet = rs.getString(6);
            ++count;
        }

        if ((count == 0) || (count > 1)) {
            LOGGER.error("could not retrieve visit number " + visitNr);
            return null;
        }

        return "visit LOOKUP (Date Processed:"
            + DateFormatter.formatAsDateTime(dateProcessed) + " - Worksheet:"
            + worksheet + ")";
    }
}
