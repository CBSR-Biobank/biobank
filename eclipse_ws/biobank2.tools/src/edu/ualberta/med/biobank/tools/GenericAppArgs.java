package edu.ualberta.med.biobank.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.ualberta.med.biobank.common.util.StringUtil;

@SuppressWarnings("nls")
public class GenericAppArgs {

    public static final String OPT_HELP = "h";
    public static final String OPT_HOST = "H";
    public static final String OPT_PORT = "p";
    public static final String OPT_USER = "u";
    public static final String OPT_PWD = "w";
    public static final String OPT_VERBOSE = "v";

    @SuppressWarnings("static-access")
    private static List<Option> OPTIONS =
        Arrays.asList(new Option(OPT_HELP, "help", false, "Displays this help text."),
                      new Option(OPT_HOST, "hostname", true,
                                 "The host name for the Biobank server."),
                      new Option(OPT_USER, "user", true, "The user name on Biobank server."),
                      new Option(OPT_VERBOSE, "verbose", false, "Use to enable debug information."),
                      new Option(OPT_PWD, "password", true, "The user's password."),
                      OptionBuilder.withArgName(OPT_PORT)
                          .withLongOpt("port")
                          .withType(Number.class)
                          .hasArg()
                          .withDescription("The port number used by the Biobank server.")
                          .create());

    protected final Options options;
    protected final CommandLineParser parser;
    protected CommandLine line;
    public boolean error = false;
    public String errorMsg;

    /**
     * Parses the command line arguments.
     * @throws ParseException
     */
    public GenericAppArgs(String[] argv, List<Option> extraOptions) throws ParseException {
        options = new Options();
        parser = new GnuParser();

        List<Option> optionList = new ArrayList<Option>(OPTIONS);
        optionList.addAll(extraOptions);
        for (Option option : optionList) {
            options.addOption(option);
        }

        line = parser.parse(options, argv);
    }

    /**
     * Parses the command line arguments.
     * @throws ParseException
     */
    public GenericAppArgs(String[] argv) throws ParseException {
        this(argv, new ArrayList<Option>(0));
    }

    public boolean helpOption() {
        return hasOption(OPT_HELP);
    }

    public boolean verboseOption() {
        return hasOption(OPT_VERBOSE);
    }

    public String hostOption() {
        if (hasOption(OPT_HOST)) {
            return line.getOptionValue(OPT_HOST);
        }
        return StringUtil.EMPTY_STRING;
    }

    public int portOption() {
        try {
            if (hasOption("port")) {
                return ((Number) line.getParsedOptionValue("port")).intValue();
            }
        } catch (ParseException e) {
            error = true;
            errorMsg = e.getMessage();
        }
        return -1;
    }

    public String userOption() {
        if (hasOption(OPT_USER)) {
            return line.getOptionValue(OPT_USER);
        }
        return StringUtil.EMPTY_STRING;
    }

    public String passwordOption() {
        if (hasOption(OPT_PWD)) {
            return line.getOptionValue(OPT_PWD);
        }
        return StringUtil.EMPTY_STRING;
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

        buf.append("h: ").append(hostOption());
        buf.append(", p: ").append(portOption());
        buf.append(", u: ").append(userOption());
        buf.append(", w: ").append(passwordOption());
        buf.append(", errmsg").append(errorMsg);

        return buf.toString();
    }

    protected boolean hasOption(String opt) {
        if (error) {
            throw new IllegalArgumentException("command line parsing failed!");
        }
        return line.hasOption(opt);

    }

    protected boolean hasOption(String opt, String longOpt) {
        if (error) {
            throw new IllegalArgumentException("command line parsing failed!");
        }
        return (line.hasOption(opt) || line.hasOption(longOpt));

    }

}
