package edu.ualberta.med.biobank.tools.bbpdblogging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.LogWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class BbpdbLogging {

    private static String USAGE = "Usage: bbpdblogging [options]\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    private static final Logger LOGGER = Logger.getLogger(BbpdbLogging.class
        .getName());

    private static String BBPDB_LOG_BASE_QUERY = "FROM logging JOIN users on users.user_nr=logging.user_nr "
        + "JOIN forms ON forms.form_nr=logging.form_nr "
        + "JOIN actions ON actions.shortform=logging.action "
        + "LEFT JOIN freezer ON freezer.index_nr=logging.findex_nr "
        + "LEFT JOIN cabinet ON cabinet.index_nr=logging.cindex_nr "
        + "WHERE timestamp < '2010-05-18' ORDER BY timestamp";

    private static String BBPDB_LOG_COUNT_QUERY = "SELECT count(*) "
        + BBPDB_LOG_BASE_QUERY;

    private static String BBPDB_LOG_QUERY = "SELECT login_id,timestamp,form_name,"
        + "actions.action,logging.patient_nr,logging.inventory_id,"
        + "details,fnum,rack,box,cell,cnum,drawer,bin,binpos "
        + BBPDB_LOG_BASE_QUERY;

    private GenericAppArgs args;

    private Connection con;

    private WritableApplicationService appService;

    private SiteWrapper cbsrSite = null;

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
        con = DriverManager.getConnection("jdbc:mysql://" + args.host
            + ":3306/bbpdb", "dummy", "ozzy498");

        String prefix = "https://";
        if (args.port == 8080)
            prefix = "http://";

        String serverUrl = prefix + args.host + ":" + args.port + "/biobank2";

        appService = ServiceConnection.getAppService(serverUrl, args.username,
            args.password);

        cbsrSite = getCbsrSite();

        Statement s = con.createStatement();
        s.execute(BBPDB_LOG_COUNT_QUERY);
        ResultSet rs = s.getResultSet();
        rs.next();
        int numLogRecords = rs.getInt(1);

        PreparedStatement ps;
        ps = con.prepareStatement(BBPDB_LOG_QUERY, ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY);
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

            // search for timestamp and if exists skip this record

            // TODO patient number should be the CHR number

            String location = null;
            if ((fnum != null) && (rack != null) && (box != null)
                && (cell != null)) {
                location = String.format("%02d%s%02d%s", fnum, rack, box, cell);
            } else if ((cnum != null) && (drawer != null) && (bin != null)
                && (binpos != null)) {
                location = String.format("%02d%s%02d%s", cnum, drawer, bin,
                    binpos);
            }

            LogWrapper logMsg = new LogWrapper(appService);
            logMsg.setUsername(loginId);
            logMsg.setDate(timestamp);
            logMsg.setType(formName);
            logMsg.setAction(action);
            logMsg.setPatientNumber(pnumber);
            logMsg.setInventoryId(inventoryId);
            logMsg.setDetails(details);
            logMsg.setLocationLabel(location);
            logMsg.persist();
            ++count;
            System.out.println("wrote log record " + count + " of "
                + numLogRecords);
        }

    }

    private SiteWrapper getCbsrSite() throws Exception {
        for (SiteWrapper site : SiteWrapper.getSites(appService)) {
            if (site.getName().equals("Canadian BioSample Repository")) {
                return site;
            }
        }
        throw new Exception("CBSR site not found");
    }

}
