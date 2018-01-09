package edu.ualberta.med.biobank.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

@SuppressWarnings("nls")
public class GenericAppArgs {

    public static final String OPT_HELP = "h";
    public static final String OPT_HOST = "H";
    public static final String OPT_PORT = "p";
    public static final String OPT_USER = "u";
    public static final String OPT_PWD = "w";
    public static final String OPT_VERBOSE = "v";

    protected final Options options;
    protected final CommandLineParser parser;
    protected CommandLine line;
    public boolean error = false;
    public String errorMsg;

    public boolean help = false;
    public boolean verbose = false;
    public String hostname = "localhost";
    public String username = "testuser";
    public String password = "test";
    public int port = 443;

    /**
     * Parses the command line arguments.
     */
    @SuppressWarnings("static-access")
    public GenericAppArgs() {
        options = new Options();
        parser = new GnuParser();

        options.addOption(OPT_HELP,    "help", false, "Displays this help text.");
        options.addOption(OPT_HOST,    "hostname", true, "The host name for the Biobank server.");
        options.addOption(OPT_USER,    "user", true, "The user name on Biobank server.");
        options.addOption(OPT_VERBOSE, "verbose", false, "Use to enable debug information.");
        options.addOption(OPT_PWD,     "password", true, "The user's password.");

        options.addOption(OptionBuilder.withArgName(OPT_PORT)
            .withLongOpt("port")
            .withType(Number.class)
            .hasArg()
            .withDescription("The port number used by the Biobank server.")
            .create());
    }

    public void parse(String[] argv) {
        try {
            line = parser.parse(options, argv);

            if (line.hasOption(OPT_HELP)) {
                this.help = true;
            }

            if (line.hasOption(OPT_VERBOSE)) {
                this.verbose = true;
            }

            if (line.hasOption(OPT_HOST)) {
                this.hostname = line.getOptionValue(OPT_HOST);
            }

            if (line.hasOption("port")) {
                this.port = ((Number) line.getParsedOptionValue("port")).intValue();
            }

            if (line.hasOption(OPT_USER)) {
                this.username = line.getOptionValue(OPT_USER);
            }

            if (line.hasOption(OPT_PWD)) {
                this.password = line.getOptionValue(OPT_PWD);
            }
        } catch (ParseException e) {
            error = true;
            errorMsg = e.getMessage();
        }
    }

    public String[] getRemainingArgs() {
        if (error) {
            throw new IllegalStateException("parsing was not successful");
        }
        return line.getArgs();
    }

    public void printHelp(String name) {
        HelpFormatter fmt = new HelpFormatter();
        fmt.printHelp(name, options);
    }

    public void printUsage(String name, String header) {
        HelpFormatter fmt = new HelpFormatter();
        fmt.printHelp(80, name, header, options, null);
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
