package edu.ualberta.med.biobank.tools.cli.command;

import org.hibernate.Session;

import edu.ualberta.med.biobank.tools.cli.CliProvider;

public class PatientDeleteCommand extends Command {

    // private static final Logger LOG = LoggerFactory.getLogger(PatientDelete.class);

    protected static final String NAME = "patient_delete";

    protected static final String USAGE = NAME + " PNUMBER";

    protected static final String HELP = "deletes a patient, plus all its collection events, and specimens.";

    private Session session;

    public PatientDeleteCommand(CliProvider cliProvider) {
        super(cliProvider, NAME, HELP, USAGE);
    }

    @Override
    public boolean runCommand(String[] args) {
        if (args.length != 2) {
            System.out.println("Error: " + USAGE);
            return false;
        }

        String pnumber = args[1];
        session = cliProvider.getSessionProvider().openSession();

        StudyDeleteUtils.deletePatient(session, pnumber);
        return true;
    }

}
