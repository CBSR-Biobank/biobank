package edu.ualberta.med.biobank.client.reports;


public class SampleTypeSUsage extends AbstractReport {

    public static String NAME = "Sample Type Usage by Study";

    public SampleTypeSUsage() {
        super(
            "Lists sample types, and the associated studies permitting them as valid sample storage.",
            new String[] { "Sample Type", "Study" });
    }

    @Override
    public String getName() {
        return NAME;
    }

}
