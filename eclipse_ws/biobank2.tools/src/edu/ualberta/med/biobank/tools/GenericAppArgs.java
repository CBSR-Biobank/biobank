package edu.ualberta.med.biobank.tools;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

@SuppressWarnings("nls")
public class GenericAppArgs {
    public boolean help = false;
    public boolean verbose = false;
    public String hostname = "localhost";
    public String username = "testuser";
    public String password = "test";
    public int port = 443;
    public String[] remainingArgs;
    public boolean error = false;
    public String errorMsg;

    /*
     * Parses the command line arguments and returns them in an AppArgs object.
     */
    public GenericAppArgs(String argv[]) {

        CmdLineParser parser = new CmdLineParser();
        Option helpOpt = parser.addBooleanOption('h', "help");
        Option hostnameOpt = parser.addStringOption('H', "hostname");
        Option portOpt = parser.addIntegerOption('p', "port");
        Option usernameOpt = parser.addStringOption('u', "user");
        Option verboseOpt = parser.addBooleanOption('v', "verbose");
        Option passwordOpt = parser.addStringOption('w', "password");

        try {
            parser.parse(argv);
        } catch (OptionException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        Boolean help = (Boolean) parser.getOptionValue(helpOpt);
        if (help != null) {
            this.help = help.booleanValue();
        }

        Boolean verbose = (Boolean) parser.getOptionValue(verboseOpt);
        if (verbose != null) {
            this.verbose = verbose.booleanValue();
        }

        String hostname = (String) parser.getOptionValue(hostnameOpt);
        if (hostname != null) {
            this.hostname = hostname;
        }

        Integer port = (Integer) parser.getOptionValue(portOpt);
        if (port != null) {
            this.port = port.intValue();
        }

        String password = (String) parser.getOptionValue(passwordOpt);
        if (password != null) {
            this.password = password;
        }

        String username = (String) parser.getOptionValue(usernameOpt);
        if (username != null) {
            this.username = username;
        }

        remainingArgs = parser.getRemainingArgs();
    }

    @Override
    public String toString() {
        return "h: " + hostname + ", p: " + port + ", u: " + username + ", w: "
            + password + ", rArgs: " + remainingArgs.toString() + ", errmsg"
            + errorMsg;
    }

}
