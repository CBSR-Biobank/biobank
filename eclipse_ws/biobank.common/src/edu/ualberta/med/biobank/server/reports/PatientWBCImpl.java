package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;

public class PatientWBCImpl extends AbstractReport {

    // private static final String TYPE_NAME = "%Cabinet%";

    // private static final String QUERY =
    // "Select Alias.patient.study.nameShort, Alias.shipment.clinic.nameShort, "
    // +
    // "Alias.patient.pnumber, Alias.dateProcessed, aliquot.sampleType.name, aliquot.inventoryId, aliquot.aliquotPosition.container.label  from "
    // + ProcessingEvent.class.getName()
    // +
    // " as Alias join Alias.aliquotCollection as aliquot where aliquot.aliquotPosition.id not in (from "
    // + AliquotPosition.class.getName()
    // + " a where a.container.label like '"
    // + SENT_SAMPLES_FREEZER_NAME
    // + "') and Alias.shipment.site "
    // + SITE_OPERATOR
    // + SITE_ID
    // + " and aliquot.sampleType.nameShort ='"
    // + FTA_CARD_SAMPLE_TYPE_NAME
    // +
    // "' and aliquot.aliquotPosition.container.id in (select path1.container.id from "
    // + ContainerPath.class.getName()
    // + " as path1, "
    // + ContainerPath.class.getName()
    // +
    // " as path2 where path1.path like path2.path || '/%' and path2.container.containerType.name like '"
    // + TYPE_NAME + "')";

    public PatientWBCImpl(BiobankReport report) {
        // super(QUERY, report);
        super("", report); //$NON-NLS-1$
    }

}
