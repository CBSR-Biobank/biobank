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

    private static final String QUERY =
        "Select Alias.patientVisit.clinicShipmentPatient.patient.study.nameShort, "
            + "Alias.patientVisit.clinicShipmentPatient.clinicShipment.clinic.nameShort, (select count(*) from "
            + PatientVisit.class.getName()
            + " pv where pv.clinicShipmentPatient.clinicShipment.clinic = Alias.patientVisit.clinicShipmentPatient.clinicShipment.clinic and pv.clinicShipmentPatient.patient.study = Alias.patientVisit.clinicShipmentPatient.patient.study and pv.dateProcessed between ? and ?),"
            + " Alias.sampleType.nameShort, count(*) from "
            + Aliquot.class.getName()
            + " as Alias left join Alias.aliquotPosition p where (p is null or p not in (from "
            + AliquotPosition.class.getName()
            + " a where a.container.label like '"
            + SENT_SAMPLES_FREEZER_NAME
            + "')) and Alias.linkDate between ? and ? and Alias.patientVisit.clinicShipmentPatient.clinicShipment.site "
            + SITE_OPERATOR
            + SITE_ID
            + " GROUP BY Alias.patientVisit.clinicShipmentPatient.patient.study.nameShort, Alias.patientVisit.clinicShipmentPatient.clinicShipment.clinic.nameShort, Alias.sampleType.nameShort"
            + " ORDER BY Alias.patientVisit.clinicShipmentPatient.patient.study.nameShort, Alias.patientVisit.clinicShipmentPatient.clinicShipment.clinic.nameShort, Alias.sampleType.nameShort";

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
