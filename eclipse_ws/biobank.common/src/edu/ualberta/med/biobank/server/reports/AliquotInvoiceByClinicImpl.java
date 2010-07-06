package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;

public class AliquotInvoiceByClinicImpl extends AbstractReport {

    private static String QUERY_STRING = "Select Alias.inventoryId, Alias.patientVisit.shipment.clinic.name, "
        + "Alias.patientVisit.patient.pnumber, "
        + "Alias.linkDate, Alias.sampleType.name  from "
        + Aliquot.class.getName()
        + " as Alias where Alias.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like ?) and Alias.linkDate > ? and Alias.linkDate < ? and "
        + "Alias.patientVisit.patient.study.site.id {1} {0,number,#} ORDER BY "
        + "Alias.patientVisit.shipment.clinic.id, Alias.patientVisit.patient.pnumber";

    public AliquotInvoiceByClinicImpl(List<Object> parameters,
        List<ReportOption> options) {
        super(QUERY_STRING, parameters, options);
    }

}
