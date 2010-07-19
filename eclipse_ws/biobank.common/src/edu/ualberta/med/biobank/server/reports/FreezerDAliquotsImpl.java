package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;

public class FreezerDAliquotsImpl extends AbstractReport {

    private static final String TYPE_NAME = "%Freezer%";

    private static final String QUERY = "select aliquot.patientVisit.patient.study.nameShort,"
        + " aliquot.patientVisit.shipment.clinic.name , year(aliquot.linkDate), "
        + GROUPBY_DATE
        + "(aliquot.linkDate), count(aliquot.linkDate) from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like '"
        + SENT_SAMPLES_FREEZER_NAME
        + "') and aliquot.aliquotPosition.container.id"
        + " in (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and"
        + " path2.container.containerType.name like '"
        + TYPE_NAME
        + "') and aliquot.linkDate between ? and ? and aliquot.patientVisit.patient.study.site "
        + SITE_OPERATOR
        + SITE_ID
        + " group by aliquot.patientVisit.patient.study.nameShort,"
        + " aliquot.patientVisit.shipment.clinic.name,  year(aliquot.linkDate), "
        + GROUPBY_DATE + "(aliquot.linkDate)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public FreezerDAliquotsImpl(BiobankReport report) {
        super(QUERY, report);
        List<Object> parameters = report.getParams();
        String groupBy = (String) parameters.remove(0);
        queryString = queryString.replaceAll(GROUPBY_DATE_SEARCH_STRING,
            groupBy);
        dateRangePostProcess = new DateRangeRowPostProcess(
            groupBy.equals("Year"), 2);
    }

    @Override
    protected AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}