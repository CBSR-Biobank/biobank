package edu.ualberta.med.biobank.client.reports;

import java.util.Date;

public class AliquotInvoiceByPatient extends BiobankReport {

    protected static final String NAME = "Aliquots per Patient by Date";

    public AliquotInvoiceByPatient() {
        super(
            "Lists all aliquots linked in a particular date range, ordered by patient.",
            new String[] { "Patient Number", "Clinic", "Link Date",
                "Sample Type" });
        addOption("Start Date (Linked)", Date.class, new Date(0));
        addOption("End Date (Linked)", Date.class, new Date());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
