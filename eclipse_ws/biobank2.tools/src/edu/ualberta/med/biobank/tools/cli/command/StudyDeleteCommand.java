package edu.ualberta.med.biobank.tools.cli.command;

import org.hibernate.Session;

import edu.ualberta.med.biobank.tools.cli.CliProvider;

public class StudyDeleteCommand extends Command {

    // private static final Logger LOG = LoggerFactory.getLogger(StudyDelete.class);

    protected static final String NAME = "study_delete";

    protected static final String USAGE = NAME + " STUDY_NAME_SHORT";

    protected static final String HELP = "deletes a study plus all its patients, collection events, and specimens.";

    private Session session;

    public StudyDeleteCommand(CliProvider cliProvider) {
        super(cliProvider, NAME, HELP, USAGE);
    }

    // public static void deleteStudy(Session session, String studyShortName) {

    @Override
    public boolean runCommand(String[] args) {
        if (args.length != 2) {
            System.out.println("Error: " + USAGE);
            return false;
        }

        String studyShortName = args[1];
        session = cliProvider.getSessionProvider().openSession();

        StudyDeleteUtils.deleteStudy(session, studyShortName);
        return true;
    }

}
