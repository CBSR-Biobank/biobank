package edu.ualberta.med.biobank.tools.sentaliquots;

import java.net.URISyntaxException;

import edu.ualberta.med.biobank.tools.GenericAppArgs;

public class AppArgs extends GenericAppArgs {

    public String csvFileName;

    public AppArgs(String argv[]) throws URISyntaxException {
        super(argv);

        if (remainingArgs.length == 1) {
            csvFileName = remainingArgs[0];
        } else {
            error = true;
            errorMsg = "Error: invalid arguments";
        }
    }

}
