package edu.ualberta.med.biobank.tools.cli.command;

import edu.ualberta.med.biobank.tools.cli.CliProvider;

public class HelpCommand extends Command {

    // private static final Logger LOG = LoggerFactory.getLogger(HelpCommand.class.getName());

    protected static final String NAME = "help";

    protected static final String USAGE = NAME + " [COMMAND]";

    protected static final String HELP = "Displays help information for a command.";

    public HelpCommand(CliProvider cliProvider) {
        super(cliProvider, NAME, HELP, USAGE);
    }

    @Override
    public boolean runCommand(String[] args) {
        if (args.length > 2) {
            System.out.println(USAGE);
            return false;
        }

        if (args.length == 1) {
            CommandRegistry.getInstance().showCommandsAndHelp();
            return true;
        }

        final String command = args[1];
        CommandRegistry.getInstance().showCommandUsage(command);
        return true;
    }

}
