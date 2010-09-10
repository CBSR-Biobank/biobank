package edu.ualberta.med.biobank.tools.sentaliquots;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.io.FileReader;
import java.util.List;

import org.supercsv.cellprocessor.constraint.Unique;
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

        appService = ServiceConnection.getAppService(serverUrl,
            appArgs.username, appArgs.password);

        site = getCbsrSite();
        if (site == null) {
            throw new Exception("CBSR site not found on server "
                + appArgs.hostname);
        }

        ICsvBeanReader reader = new CsvBeanReader(new FileReader(
            appArgs.csvFileName), CsvPreference.EXCEL_PREFERENCE);

        final CellProcessor[] processors = new CellProcessor[] { new Unique(),
            new Unique(), null };

        ActivityStatusWrapper closedStatus = ActivityStatusWrapper
            .getActivityStatus(appService,
                ActivityStatusWrapper.CLOSED_STATUS_STRING);

        try {
            String[] header = reader.getCSVHeader(true);
            PatientInfo info;
            while ((info = reader.read(PatientInfo.class, header, processors)) != null) {
                List<AliquotWrapper> aliquots = AliquotWrapper
                    .getAliquotsInSite(appService, info.getInventoryId(), site);

                System.out.print("patient " + info.getPatientNo()
                    + " inventory ID " + info.getInventoryId());

                if (aliquots.size() == 0) {
                    System.out.println(" not found");
                    continue;
                } else if (aliquots.size() > 1) {
                    throw new Exception("multiple aliquots with inventory id"
                        + info.getInventoryId());
                }

                AliquotWrapper aliquot = aliquots.get(0);

                String aliquotPnumber = aliquot.getPatientVisit().getPatient()
                    .getPnumber();
                if (!aliquotPnumber.equals(info.getPatientNo())) {
                    System.out
                        .println(" ERROR: does not match patient number for aliquot "
                            + aliquotPnumber);
                    continue;
                }

                System.out.println(" old position "
                    + aliquot.getPositionString());
                aliquot.setComment(info.getCloseComment());
                aliquot.setPosition(null);
                aliquot.setActivityStatus(closedStatus);
                aliquot.persist();
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