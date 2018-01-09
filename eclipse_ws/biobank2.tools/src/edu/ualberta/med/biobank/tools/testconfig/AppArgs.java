package edu.ualberta.med.biobank.tools.testconfig;

import edu.ualberta.med.biobank.tools.GenericAppArgs;

public class AppArgs extends GenericAppArgs {

    public boolean grandchidSpecimenTypes;

    public AppArgs() {
        super();
        options.addOption("g", "grandchild", false,
            "Add grindchild specimen types and configure study with grandchild aliquot types.");
    }

    @Override
    public void parse(String[] argv) {
        super.parse(argv);
        if (!error && (line.hasOption("g") || line.hasOption("grandchild"))) {
            this.grandchidSpecimenTypes = true;
        }
    }
}
