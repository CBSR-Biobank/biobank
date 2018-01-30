package edu.ualberta.med.biobank.tools.cli;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
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
import edu.ualberta.med.biobank.tools.cli.command.HelpCommand;
import edu.ualberta.med.biobank.tools.cli.command.ProcessingEventUpdate;
import edu.ualberta.med.biobank.tools.cli.command.SpecimenUpdateActivityStatus;
import edu.ualberta.med.biobank.tools.cli.command.batchoperation.PatientImportCommand;
import edu.ualberta.med.biobank.tools.cli.command.batchoperation.SpecimenImportCommand;
import edu.ualberta.med.biobank.tools.utils.HostUrl;

/**
 * Provides a Command Line Interface (CLI) to a Biobank server.
 *
 * The CLI provides a number commands that can be used to add information to a Biobank installation.
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

        private static List<Option> EXTRA_OPTIONS =
            Arrays.asList(new Option("d", "database", false,
                          "Use a database connection rather than connect to the Biobank server via HTTPS."));

        public AppArgs(String[] argv) throws ParseException {
            super(argv, EXTRA_OPTIONS);
        }

        public boolean databaseOption() {
            return hasOption("d", "database");
        }
    }

    private BiobankApplicationService appService;

    private SessionProvider sessionProvider;

    private AppArgs options;

    public static void main(String[] argv) {
        try {
            new BiobankCli(argv);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    public BiobankCli(String[] argv) throws ParseException {
        super(APP_NAME, USAGE, new AppArgs(argv));
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
        if (sessionProvider == null) {
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
            CommandRegistry.getInstance().invokeCommand(commandName, quotedArgs);
            System.exit(0);
        }

        // when run from within eclipse "clientVersion" will be null, but
        // when run from the JAR file, it will return a valid version number
        String clientVersion = BiobankCli.class.getPackage().getImplementationVersion();

        if (commandName.equals("version")) {
            System.out.println("BiobankCli version: " + clientVersion);
            System.exit(0);
        }

        if (!this.options.databaseOption()) {
            String hostUrl = HostUrl.getHostUrl(args.hostOption(), args.portOption());

            try {
                checkCertificates(hostUrl);

                appService = ServiceConnection.getAppService(
                    hostUrl, args.userOption(), args.passwordOption());

                // validate that we are connected with a valid version of the server
                if (clientVersion != null) {
                    appService.checkVersion(clientVersion);
                }
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
        cr.addCommand(new HelpCommand(this));
        cr.addCommand(new SpecimenImportCommand(this));
        cr.addCommand(new PatientImportCommand(this));
        cr.addCommand(new CreateContainerCommand(this));
        cr.addCommand(new SpecimenUpdateActivityStatus(this));
        cr.addCommand(new ProcessingEventUpdate(this));

        // DO NOT ADD these commands since they need to be converted to actions
        // cr.addCommand(new StudyCountsCommand(this));
        // cr.addCommand(new PatientDeleteCommand(this));
        // cr.addCommand(new StudyDeleteCommand(this));

        // Shipment CSV import not fully implemented on server side yet
        // cr.addCommand(new ShipmentImportCommand(this));
    }

}
