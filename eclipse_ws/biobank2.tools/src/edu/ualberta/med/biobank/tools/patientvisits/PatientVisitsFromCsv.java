package edu.ualberta.med.biobank.tools.patientvisits;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.tools.sentaliquots.AppArgs;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.io.FileReader;
import java.util.List;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Parses a CSV file with BBPSP patient numbers and returns the number of
 * patient visits for the patients. The patient numbers do not have leading
 * zeroes, so they must be added before the database is queried.
 */
public class PatientVisitsFromCsv {

    private static String USAGE = "Usage: visitsfromcsv [options] CSVFILE\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    private static PatientVisitsFromCsv instance = null;

    private static WritableApplicationService appService;

    private SiteWrapper site;

    @SuppressWarnings("unused")
    private AppArgs appArgs = null;

    private PatientVisitsFromCsv() {

    }

    public static PatientVisitsFromCsv getInstance() {
        if (instance == null) {
            instance = new PatientVisitsFromCsv();
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

        final CellProcessor[] processors = new CellProcessor[] { new ParseInt() };

        try {
            String[] header = new String[] { "patientNo" };
            PatientInfo info;
            System.out.println("#Patient Number,Num Visits");
            while ((info = reader.read(PatientInfo.class, header, processors)) != null) {
                String pnumber = String.format("%04d", info.getPatientNo());
                PatientWrapper patient = PatientWrapper.getPatient(appService,
                    pnumber);
                if (patient != null) {
                    List<ProcessingEventWrapper> visits = patient
                        .getPatientVisitCollection();
                    if (visits != null) {
                        System.out.println(pnumber + "," + visits.size());
                    } else {
                        System.out.println(pnumber + ",0");
                    }
                } else {
                    System.out.println(pnumber + ",not in database");
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
            PatientVisitsFromCsv.getInstance().doWork(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
