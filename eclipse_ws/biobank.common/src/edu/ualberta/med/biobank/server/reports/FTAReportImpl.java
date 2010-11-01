package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class FTAReportImpl extends AbstractReport {

    // note that this will consider patient visits without any aliquots or
    // patient visits without any aliquots of the specific sample type wanted
    private static final String QUERY =
        "select min(a2.id) from "
            + Aliquot.class.getName()
            + " a2 where a2.patientVisit.dateProcessed = "
            + " (select min(pv.dateProcessed) from "
            + PatientVisit.class.getName()
            + " pv where pv.shipmentPatient.patient.id = a2.patientVisit.shipmentPatient.patient "
            + " and pv.shipmentPatient.patient.study.nameShort = ?)"
            + " and a2.sampleType.nameShort = '"
            + FTA_CARD_SAMPLE_TYPE_NAME
            + "' and a2.patientVisit.dateProcessed > ? and a2.aliquotPosition.container.label not like '"
            + SENT_SAMPLES_FREEZER_NAME
            + "' group by a2.patientVisit.shipmentPatient.patient.pnumber"
            + " order by a2.patientVisit.shipmentPatient.patient.pnumber";

    public FTAReportImpl(BiobankReport report) {
        super(QUERY, report);
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        ArrayList<Object> modifiedResults = new ArrayList<Object>();
        // get the info
        for (Object ob : results) {
            Aliquot a = new Aliquot();
            a.setId((Integer) ob);
            try {
                a = (Aliquot) appService.search(Aliquot.class, a).get(0);
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
            String pnumber =
                a.getPatientVisit().getShipmentPatient().getPatient()
                    .getPnumber();
            String inventoryId = a.getInventoryId();
            String dateProcessed =
                DateFormatter.formatAsDate(a.getPatientVisit()
                    .getDateProcessed());
            String stName = a.getSampleType().getNameShort();
            AliquotWrapper aliquotWrapper = new AliquotWrapper(appService, a);
            String aliquotLabel = aliquotWrapper.getPositionString(true, true);
            modifiedResults.add(new Object[] { pnumber, dateProcessed,
                inventoryId, stName,
                aliquotWrapper.getParent().getSite().getNameShort(),
                aliquotLabel });
        }
        return modifiedResults;
    }
}