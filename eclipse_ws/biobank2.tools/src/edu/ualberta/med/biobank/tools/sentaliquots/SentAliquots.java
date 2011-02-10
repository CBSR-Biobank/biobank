package edu.ualberta.med.biobank.tools.sentaliquots;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

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

        appService = ServiceConnection.getAppService(serverUrl,
            appArgs.username, appArgs.password);

        site = getCbsrSite();
        if (site == null) {
            throw new Exception("CBSR site not found on server "
                + appArgs.hostname);
        }

        ICsvBeanReader reader = new CsvBeanReader(new FileReader(
            appArgs.csvFileName), CsvPreference.EXCEL_PREFERENCE);

        final CellProcessor[] processors = new CellProcessor[] {
            new StrNotNullOrEmpty(), new StrNotNullOrEmpty(),
            new StrNotNullOrEmpty() };

        ActivityStatusWrapper closedStatus = ActivityStatusWrapper
            .getActivityStatus(appService,
                ActivityStatusWrapper.CLOSED_STATUS_STRING);

        Map<String, AliquotWrapper> aliquotsAffected = new HashMap<String, AliquotWrapper>();

        try {
            String[] header = new String[] { "patientNo", "inventoryId",
                "closeComment" };
            PatientInfo info;
            while ((info = reader.read(PatientInfo.class, header, processors)) != null) {
                AliquotWrapper aliquot = AliquotWrapper.getAliquot(appService,
                    info.getInventoryId(), null);

                if (aliquot == null) {
                    System.out
                        .println(" ERROR: aliquot not found: inventoryId/"
                            + info.getInventoryId() + " patientNo/"
                            + info.getPatientNo());
                    continue;
                }

                String aliquotPnumber = aliquot.getProcessingEvent().getPatient()
                    .getPnumber();

                if (!aliquotPnumber.equals(info.getPatientNo())) {
                    System.out
                        .println(" ERROR: patient number mismatch: inventoryId/"
                            + info.getInventoryId()
                            + " csvPatientNo/"
                            + info.getPatientNo()
                            + " dbPatientNo/"
                            + aliquotPnumber);
                    continue;
                }

                if (aliquotsAffected.containsKey(info.getInventoryId())) {
                    System.out
                        .println(" ERROR: duplicate aliquot: inventoryId/"
                            + info.getInventoryId() + " patientNo/"
                            + aliquotPnumber);
                    continue;
                }

                aliquotsAffected.put(info.getInventoryId(), aliquot);

                if (aliquot.getActivityStatus().equals(closedStatus)) {
                    System.out
                        .println(" ERROR: aliquot already closed: inventoryId/"
                            + info.getInventoryId() + " patientNo/"
                            + info.getPatientNo() + " comment/\""
                            + aliquot.getComment() + "\"");
                    continue;
                }

                String oldPosition = new String(aliquot.getPositionString());

                aliquot.setComment(info.getCloseComment());
                aliquot.setPosition(null);
                aliquot.setActivityStatus(closedStatus);
                aliquot.persist();

                if (appArgs.verbose) {
                    System.out.println("patient/" + info.getPatientNo()
                        + " inventoryId/" + info.getInventoryId()
                        + " oldPosition/" + oldPosition + " comment/\""
                        + info.getCloseComment() + "\"");
                }
            }
        } finally {
            reader.close();
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