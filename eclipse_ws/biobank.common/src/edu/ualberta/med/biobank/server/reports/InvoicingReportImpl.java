package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class InvoicingReportImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.patientVisit.patient.study.nameShort, "
        + "Alias.patientVisit.shipment.clinic.nameShort, (select count(*) from "
        + PatientVisit.class.getName()
        + " pv where pv.shipment.clinic = Alias.patientVisit.shipment.clinic and pv.patient.study = Alias.patientVisit.patient.study and pv.dateProcessed between ? and ?),"
        + " Alias.sampleType.nameShort, count(*) from "
        + Aliquot.class.getName()
        + " as Alias where Alias.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "') and Alias.linkDate between ? and ? and Alias.patientVisit.shipment.site "
        + SITE_OPERATOR
        + SITE_ID
        + " GROUP BY Alias.patientVisit.patient.study.nameShort, Alias.patientVisit.shipment.clinic, Alias.sampleType.nameShort";

    public InvoicingReportImpl(BiobankReport report) {
        super(QUERY, report);
        List<Object> params = report.getParams();
        params.addAll(params);
        this.report.setParams(params);
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        List<Object> modifiedResults = new ArrayList<Object>();
        HashSet<String> pvCount = new HashSet<String>();
        for (Object ob : results) {
            Object[] castOb = (Object[]) ob;
            if (pvCount.contains((String) castOb[0] + (String) castOb[1]))
                modifiedResults.add(new Object[] { "", "", "", castOb[3],
                    castOb[4] });
            else {
                pvCount.add((String) castOb[0] + (String) castOb[1]);
                modifiedResults.add(castOb);
            }
        }
        return modifiedResults;
    }
}
