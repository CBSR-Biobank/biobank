package edu.ualberta.med.biobank.tools;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

@SuppressWarnings("nls")
public class GenericAppArgs {
    protected final CmdLineParser parser;

    public boolean help = false;
    public boolean verbose = false;
    public String hostname = "localhost";
    public String username = "testuser";
    public String password = "test";
    public int port = 443;
    public boolean error = false;
    public String errorMsg;

    private final Option helpOpt;
    private final Option hostnameOpt;
    private final Option portOpt;
    private final Option usernameOpt;
    private final Option verboseOpt;
    private final Option passwordOpt;

    /**
     * Parses the command line arguments.
     */
    public GenericAppArgs() {
        parser = new CmdLineParser();

        helpOpt = parser.addBooleanOption('h', "help");
        hostnameOpt = parser.addStringOption('H', "hostname");
        portOpt = parser.addIntegerOption('p', "port");
        usernameOpt = parser.addStringOption('u', "user");
        verboseOpt = parser.addBooleanOption('v', "verbose");
        passwordOpt = parser.addStringOption('w', "password");
    }

    public void parse(String[] argv) {
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
    }

    public String[] getRemainingArgs() {
        return parser.getRemainingArgs();
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("h: ").append(hostname);
        buf.append(", p: ").append(port);
        buf.append(", u: ").append(username);
        buf.append(", w: ").append(password);
        buf.append(", errmsg").append(errorMsg);

        return buf.toString();
    }

}
