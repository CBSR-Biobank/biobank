package edu.ualberta.med.biobank.common.reports;

import java.util.List;

import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.model.PatientVisit;

@Deprecated
public class PatientWBC extends QueryObject {

    protected static final String NAME = "Patient WBC Aliquots Old";

    public PatientWBC(String op, Integer siteId) {
        super(
            "Displays a list of the WBC aliquots located in Cabinets taken from a patient. Note: the full name of the sample type must contain \"DNA\", and the top container's name must contain \"Cabinet\"",
            "Select Alias.patient.study.nameShort, Alias.shipment.clinic.name, "
                + "Alias.patient.pnumber, Alias.dateProcessed, aliquot.sampleType.name, aliquot.inventoryId, aliquot.aliquotPosition.container.label  from "
                + PatientVisit.class.getName()
                + " as Alias left join Alias.aliquotCollection as aliquot where aliquot.aliquotPosition not in (from "
                + AliquotPosition.class.getName()
                + " a where a.container.label like 'SS%') and Alias.patient.study.site "
                + op
                + siteId
                + " and aliquot.sampleType.name LIKE '%DNA%' and aliquot.aliquotPosition.container.id in (select path1.container.id from "
                + ContainerPath.class.getName()
                + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.name like ?)",
            new String[] { "Study", "Clinic", "Patient", "Date", "Sample Type",
                "Inventory ID", "Location" });
    }

    @Override
    public List<Object> preProcess(List<Object> params) {
        params.add("%Cabinet%");
        return params;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
