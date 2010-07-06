package edu.ualberta.med.biobank.client.reports;

import java.util.Date;

public class AliquotInvoiceByClinic extends AbstractReport {

    protected static final String NAME = "Aliquots per Clinic by Date";

    public AliquotInvoiceByClinic() {
        super(
            "Lists all aliquots linked in a particular date range, ordered by clinic.",
            new String[] { "Inventory ID", "Clinic", "Patient Number",
                "Link Date", "Sample Type" });
        addOption("Start Date (Linked)", Date.class, new Date(0));
        addOption("End Date (Linked)", Date.class, new Date());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
