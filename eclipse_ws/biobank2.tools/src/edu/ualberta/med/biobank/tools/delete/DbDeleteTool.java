package edu.ualberta.med.biobank.tools.delete;

import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;

import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.SessionProvider;
import edu.ualberta.med.biobank.tools.SessionProvider.Mode;

/**
 * Used to delete a study and all it's patients, collection events, and specimens.
 * 
 * @author loyola
 * 
 */
public class DbDeleteTool {

    private static final String APP_NAME = "biobank_delete_tool";

    private static String APP_DESCRIPTION =
        "Reads options from db.properties file or from system properties.";

    // private static final Logger log = LoggerFactory.getLogger(DbDeleteTool.class);

    private static class AppArgs extends GenericAppArgs {

        public boolean queriesOnly = false;

        public AppArgs() {
            super();
            options.addOption("q", "queries", false, "Runs queries on database to get totals.");
        }

        @Override
        public void parse(String[] argv) {
            super.parse(argv);
            if (!error && line.hasOption("q")) {
                this.queriesOnly = true;
            }
        }
    }

    private final SessionProvider sessionProvider;

    private final Session session;

    private final AppArgs options;

    public static void main(String[] argv) {
        try {
            AppArgs args = new AppArgs();
            args.parse(argv);

            if (args.help) {
                System.out.println(APP_NAME);
                System.out.println(APP_DESCRIPTION);
                args.printHelp(APP_NAME);
                System.exit(0);
            } else if (args.error) {
                System.out.println(args.errorMsg + "\n" + APP_DESCRIPTION);
                System.exit(-1);
            }

            new DbDeleteTool(args.getRemainingArgs(), args);
        } catch (SQLGrammarException e) {
            System.out.println(e.getMessage());
        }
    }

    private DbDeleteTool(String[] args, AppArgs options) {
        this.options = options;
        sessionProvider = new SessionProvider(Mode.RUN);
        session = sessionProvider.openSession();

        if (args.length != 2) {
            System.out.println("Error: invalid command:\n\n");
            System.exit(0);
        } else if (args[0].equals("study")) {
            studyCmd(args[1]);
        } else if (args[0].equals("patient")) {
            patientCmd(args[1]);
        } else {
            System.out.println("Error: invalid command:\n\n");
            System.exit(0);
        }
    }

    private void studyCmd(String studyShortName) {
        if (options.queriesOnly) {
            StudyCounts.getPatientCount(session, studyShortName);
            StudyCounts.getCeventCount(session, studyShortName);
            StudyCounts.getParentSpecimenCount(session, studyShortName);
            StudyCounts.getChildSpecimenCount(session, studyShortName);
            StudyCounts.getDispatchCount(session, studyShortName);
            StudyCounts.getDispatchSpecimenCount(session, studyShortName);
        } else {
            StudyDelete.deleteStudy(session, studyShortName);
        }
    }

    private void patientCmd(String pnumber) {
        StudyDelete.deletePatient(session, pnumber);
    }

}
