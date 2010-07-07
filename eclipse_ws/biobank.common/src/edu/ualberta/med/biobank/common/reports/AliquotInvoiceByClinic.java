package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;

@Deprecated
public class AliquotInvoiceByClinic extends QueryObject {

    protected static final String NAME = "Aliquots per Clinic by Date Old";

    private static String QUERY_STRING = "Select Alias.inventoryId, Alias.patientVisit.shipment.clinic.name, "
        + "Alias.patientVisit.patient.pnumber, "
        + "Alias.linkDate, Alias.sampleType.name  from "
        + Aliquot.class.getName()
        + " as Alias where Alias.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like ?) and Alias.linkDate > ? and Alias.linkDate < ? and "
        + "Alias.patientVisit.patient.study.site.id {1} {0,number,#} ORDER BY "
        + "Alias.patientVisit.shipment.clinic.id, Alias.patientVisit.patient.pnumber";

    public AliquotInvoiceByClinic(String op, Integer siteId) {
        super(
            "Lists all aliquots linked in a particular date range, ordered by clinic.",
            MessageFormat.format(QUERY_STRING, siteId, op), new String[] {
                "Inventory ID", "Clinic", "Patient Number", "Link Date",
                "Sample Type" });
        addOption("Start Date (Linked)", Date.class, new Date(0));
        addOption("End Date (Linked)", Date.class, new Date());
    }

    @Override
    public List<Object> preProcess(List<Object> params) {
        params.add(0, "SS%");
        return params;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
