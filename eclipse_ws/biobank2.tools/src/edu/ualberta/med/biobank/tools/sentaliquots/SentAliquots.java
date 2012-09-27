package edu.ualberta.med.biobank.tools.sentaliquots;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.io.FileReader;
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

        String serverUrl = prefix + appArgs.hostname + ":" + appArgs.port
            + "/biobank2";

        if (appArgs.verbose) {
            System.out.println("connection URL: " + serverUrl + " w="
                + appArgs.username + " p=" + appArgs.password);
        }

        appService = edu.ualberta.med.biobank.common.ServiceConnection
            .getAppService(serverUrl, appArgs.username, appArgs.password);

        site = getCbsrSite();
        if (site == null) {
            throw new Exception("CBSR site not found on server "
                + appArgs.hostname);
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

            if (aliquots.size() == 0) {
                System.out.println(" ERROR: aliquot not found: inventoryId/"
                    + inventoryId + " patientNo/" + patientNo);
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
                    .println(" ERROR: patient number mismatch: inventoryId/"
                        + inventoryId + " csvPatientNo/" + patientNo
                        + " dbPatientNo/" + aliquotPnumber);
                continue;
            }

            if (aliquot.getActivityStatus().equals(closedStatus)) {
                System.out
                    .println(" ERROR: aliquot already closed: inventoryId/"
                        + inventoryId + " patientNo/" + patientNo
                        + " comment/\"" + aliquot.getComment() + "\"");
                continue;
            }

            String oldPosition = new String(aliquot.getPositionString());

            aliquot.setComment(closeComment);
            aliquot.setPosition(null);
            aliquot.setActivityStatus(closedStatus);
            aliquot.persist();

            if (appArgs.verbose) {
                System.out.println("patient/" + patientNo + " inventoryId/"
                    + inventoryId + " oldPosition/" + oldPosition
                    + " comment/\"" + closeComment + "\"");
            }
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
            AppArgs args = new AppArgs(argv);
            if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }
            SentAliquots.getInstance().doWork(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}