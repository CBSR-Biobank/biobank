package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.reports2.ReportOption;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientWBCImpl extends AbstractReport {

    private static final String QUERY = "Select Alias.patient.study.nameShort, Alias.shipment.clinic.name, "
        + "Alias.patient.pnumber, Alias.dateProcessed, aliquot.sampleType.name, aliquot.inventoryId, aliquot.aliquotPosition.container.label  from "
        + PatientVisit.class.getName()
        + " as Alias left join Alias.aliquotCollection as aliquot where aliquot.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like 'SS%') and Alias.patient.study.site "
        + siteOperatorString
        + siteIdString
        + " and aliquot.sampleType.name LIKE '%DNA%' and aliquot.aliquotPosition.container.id in (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like ?)";

    // FIXME need BiobankListProxy
    public PatientWBCImpl(List<Object> parameters, List<ReportOption> options) {
        super(QUERY, parameters, options);
        parameters.add("%Cabinet%");
    }

}
