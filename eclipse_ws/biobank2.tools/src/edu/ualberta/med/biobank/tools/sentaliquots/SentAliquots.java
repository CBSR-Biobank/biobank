package edu.ualberta.med.biobank.tools.sentaliquots;

import edu.ualberta.med.biobank.common.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class SentAliquots {

    private static String USAGE = "Usage: sentaliquots [options] CSVFILE\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    private static SentAliquots instance = null;

    private static WritableApplicationService appService;

    private SiteWrapper site;

    @SuppressWarnings("unused")
    private AppArgs appArgs = null;

    private SentAliquots() {

    }

    public static SentAliquots getInstance() {
        if (instance == null) {
            instance = new SentAliquots();
        }
        return instance;
    }

    public void doWork(AppArgs appArgs) throws Exception {
        this.appArgs = appArgs;

        String prefix = "https://";
        if (appArgs.port == 8080)
            prefix = "http://";

        String serverUrl = prefix + appArgs.host + ":" + appArgs.port
            + "/biobank2";

        appService = ServiceConnection.getAppService(serverUrl,
            appArgs.username, appArgs.password);

        site = getCbsrSite();
        if (site == null) {
            throw new Exception("CBSR site not found on server " + appArgs.host);
        }

        CSVReader reader = new CSVReader(new FileReader(appArgs.csvFileName));

        int count = 0;
        List<String[]> content = reader.readAll();

        // first, parse entire file looking for errors
        for (String[] cols : content) {
            count++;

            if (cols.length < 2) {
                throw new Exception("missing columns in row " + count);
            } else if (cols.length > 3) {
                throw new Exception("too many columns in row " + count);
            }
        }

        ActivityStatusWrapper closedStatus = ActivityStatusWrapper
            .getActivityStatus(appService, "Closed");

        // now process the file contents
        for (String[] cols : content) {
            String patientNo = cols[0];
            String inventoryId = cols[1];
            String closeComment = cols[2];

            List<AliquotWrapper> aliquots = AliquotWrapper.getAliquotsInSite(
                appService, inventoryId, site);

            System.out.print("patient " + patientNo + " inventory ID "
                + inventoryId);

            if (aliquots.size() == 0) {
                System.out.println(" not found");
                continue;
            } else if (aliquots.size() > 1) {
                throw new Exception("multiple aliquots with inventory id"
                    + inventoryId);
            }

            AliquotWrapper aliquot = aliquots.get(0);

            String aliquotPnumber = aliquot.getPatientVisit().getPatient()
                .getPnumber();
            if (!aliquotPnumber.equals(patientNo)) {
                System.out
                    .println(" ERROR: does not match patient number for aliquot "
                        + aliquotPnumber);
                continue;
            }

            System.out.println(" old position " + aliquot.getPositionString());
            aliquot.setComment(closeComment);
            aliquot.setPosition(null);
            aliquot.setActivityStatus(closedStatus);
            aliquot.persist();
        }
    }

    private SiteWrapper getCbsrSite() throws Exception {
        for (SiteWrapper site : SiteWrapper.getSites(appService)) {
            if (site.getName().equals("Canadian BioSample Repository")) {
                return site;
            }
        }
        return null;
    }

    public static void main(String argv[]) {
        try {
            SentAliquots.getInstance().doWork(parseCommandLine(argv));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Parses the command line arguments and returns them in an AppArgs object.
     */
    private static AppArgs parseCommandLine(String argv[])
        throws URISyntaxException {
        AppArgs appArgs = new AppArgs();

        CmdLineParser parser = new CmdLineParser();
        Option hostUrlOpt = parser.addStringOption('h', "hosturl");
        Option portOpt = parser.addIntegerOption('p', "port");
        Option usernameOpt = parser.addStringOption('u', "user");
        Option verboseOpt = parser.addBooleanOption('v', "verbose");
        Option passwordOpt = parser.addStringOption('w', "password");

        try {
            parser.parse(argv);
        } catch (OptionException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        Boolean verbose = (Boolean) parser.getOptionValue(verboseOpt);
        if (verbose != null) {
            appArgs.verbose = verbose.booleanValue();
        }

        String hostUrl = (String) parser.getOptionValue(hostUrlOpt);
        if (hostUrl != null) {
            appArgs.host = hostUrl;
        }

        Integer port = (Integer) parser.getOptionValue(portOpt);
        if (port != null) {
            appArgs.port = port.intValue();
        }

        String password = (String) parser.getOptionValue(passwordOpt);
        if (hostUrl != null) {
            appArgs.password = password;
        }

        String username = (String) parser.getOptionValue(usernameOpt);
        if (username != null) {
            appArgs.username = username;
        }

        String[] args = parser.getRemainingArgs();
        if (args.length != 1) {
            System.out.println("Error: invalid arguments\n" + USAGE);
            System.exit(-1);
        }

        appArgs.csvFileName = args[0];

        return appArgs;
    }

}

class AppArgs {
    boolean verbose = false;
    String host = "localhost";
    String username = "testuser";
    String password = "test";
    int port = 8443;
    String csvFileName = null;
}