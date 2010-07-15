package edu.ualberta.med.biobank.server.reports;

import java.text.MessageFormat;
import java.util.List;

import edu.ualberta.med.biobank.client.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;

public class CabinetDAliquotsImpl extends AbstractReport {

    private static final String TYPE_NAME = "%Cabinet%";

    private static final String QUERY = "select aliquot.patientVisit.patient.study.nameShort,"
        + " aliquot.patientVisit.shipment.clinic.name, year(aliquot.linkDate),"
        + " {0}(aliquot.linkDate), count(aliquot.linkDate) from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot.aliquotPosition.container.id in"
        + " (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and"
        + " path2.container.containerType.name like ''"
        + TYPE_NAME
        + "'') and aliquot.patientVisit.patient.study.site "
        + SITE_OPERATOR
        + SITE_ID
        + " group by aliquot.patientVisit.patient.study.nameShort,"
        + " aliquot.patientVisit.shipment.clinic.name, year(aliquot.linkDate), {0}(aliquot.linkDate)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public CabinetDAliquotsImpl(BiobankReport report) {
        super(QUERY, report);
        List<Object> parameters = report.getParams();
        List<String> strings = report.getStrings();
        String groupBy = strings.get(0);
        queryString = MessageFormat.format(queryString, groupBy);
        dateRangePostProcess = new DateRangeRowPostProcess(
            groupBy.equals("Year"), 2);
    }

    @Override
    protected AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }
}