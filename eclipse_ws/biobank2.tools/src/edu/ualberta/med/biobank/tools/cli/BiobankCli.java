package edu.ualberta.med.biobank.tools.cli;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.Application;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.SessionProvider;
import edu.ualberta.med.biobank.tools.SessionProvider.Mode;
import edu.ualberta.med.biobank.tools.cli.command.CommandRegistry;
import edu.ualberta.med.biobank.tools.cli.command.CreateContainerCommand;
import edu.ualberta.med.biobank.tools.cli.command.PatientDeleteCommand;
import edu.ualberta.med.biobank.tools.cli.command.SpecimenUpdateActivityStatus;
import edu.ualberta.med.biobank.tools.cli.command.StudyCountsCommand;
import edu.ualberta.med.biobank.tools.cli.command.StudyDeleteCommand;
import edu.ualberta.med.biobank.tools.cli.command.batchoperation.PatientImportCommand;
import edu.ualberta.med.biobank.tools.cli.command.batchoperation.ShipmentImportCommand;
import edu.ualberta.med.biobank.tools.cli.command.batchoperation.SpecimenImportCommand;
import edu.ualberta.med.biobank.tools.utils.HostUrl;

/**
 * Used to delete a study and all it's patients, collection events, and specimens.
 *
 * @author loyola
 *
 */
public class BiobankCli extends Application implements CliProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BiobankCli.class);

    protected static final String APP_NAME = "java -jar BiobankCli.jar [OPTIONS] COMMAND [ARGS]";

    protected static String USAGE =
        "\nA command line interface that interacts with the Biobank server."
            + "Reads options from db.properties file or from system properties.\n\n";

    protected static final String SPLIT_BY_SPACES_REGEX = " (?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)";

    public static class AppArgs extends GenericAppArgs {

        public String database = "";

        public AppArgs() {
            super();
            options.addOption("d", "database", true,
                "Use a database connection rather than connect to the Biobank server.");
        }

        @Override
        public void parse(String[] argv) {
            super.parse(argv);

            try {
                if (!error) {
                    if (line.hasOption("d")) {
                        this.database = (String) line.getParsedOptionValue("d");
                    } else if (line.hasOption("database")) {
                        this.database = (String) line.getParsedOptionValue("database");
                    }
                }
            } catch (ParseException e) {
                error = true;
                errorMsg = e.getMessage();
            }
        }
    }

    private BiobankApplicationService appService;

    private SessionProvider sessionProvider;

    private AppArgs options;

    public static void main(String[] argv) {
        new BiobankCli(argv);
    }

    public BiobankCli(String[] argv) {
        super(APP_NAME, USAGE, argv, new AppArgs());
    }

    @Override
    public BiobankApplicationService getAppService() {
        if (appService == null) {
            throw new IllegalStateException("application not started with application service");
        }
        return appService;
    }

    @Override
    public SessionProvider getSessionProvider() {
        if (appService == null) {
            throw new IllegalStateException("application not started with database support");
        }
        return sessionProvider;
    }

    @Override
    protected void start(GenericAppArgs args) {
        this.options = (AppArgs) args;
        String[] remainingArgs = this.options.getRemainingArgs();

        if (remainingArgs.length < 1) {
            System.out.println("Error: command not specified");
            System.exit(1);
        }

        addCommands();

        String[] quotedArgs = StringUtils.join(remainingArgs, " ").split(SPLIT_BY_SPACES_REGEX);
        String commandName = quotedArgs[0];

        if (commandName.equals("help")) {
            CommandRegistry.getInstance().showCommandsAndHelp();
            System.exit(0);
        }

        if (this.options.database.isEmpty()) {
            String hostUrl = HostUrl.getHostUrl(args.hostname, args.port);

            try {
                checkCertificates(hostUrl);

                appService = ServiceConnection.getAppService(hostUrl, args.username,
                    args.password);
            } catch (Exception e) {
                System.out.println("Could not connect to application service: " + e.getMessage());
                System.exit(1);
            }

            LOG.info("host url is {}", hostUrl);
        } else {
            sessionProvider = new SessionProvider(Mode.RUN);
        }

        CommandRegistry.getInstance().invokeCommand(commandName, quotedArgs);
    }

    private void addCommands() {
        CommandRegistry cr = CommandRegistry.getInstance();
        cr.addCommand(new SpecimenImportCommand(this));
        cr.addCommand(new PatientImportCommand(this));
        cr.addCommand(new ShipmentImportCommand(this));
        cr.addCommand(new StudyCountsCommand(this));
        cr.addCommand(new CreateContainerCommand(this));
        cr.addCommand(new StudyDeleteCommand(this));
        cr.addCommand(new PatientDeleteCommand(this));
        cr.addCommand(new SpecimenUpdateActivityStatus(this));
    }

}
