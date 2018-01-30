package edu.ualberta.med.biobank.tools.testconfig;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import edu.ualberta.med.biobank.tools.GenericAppArgs;

public class AppArgs extends GenericAppArgs {

    private static List<Option> EXTRA_OPTIONS =
        Arrays.asList(new Option("a", "aliquots", false, "Add aliquot specimens"),
                      new Option("g", "grandchild", false,
                                 "Add grindchild specimen types and configure study with grandchild aliquot types."));

    public AppArgs(String[] argv) throws ParseException {
        super(argv, EXTRA_OPTIONS);
    }

    public boolean aliquotsOption() {
        return hasOption("a", "aliquots");
    }

    public boolean grandchildOption() {
        return hasOption("g", "grandchild");
    }
}
