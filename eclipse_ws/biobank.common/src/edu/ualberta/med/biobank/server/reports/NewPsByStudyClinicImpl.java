package edu.ualberta.med.biobank.server.reports;

import java.text.MessageFormat;
import java.util.List;

import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.DateRangeRowPostProcess;
import edu.ualberta.med.biobank.common.util.ReportOption;

public class NewPsByStudyClinicImpl extends AbstractReport {

    private static final String QUERY = "select pv.patient.study.nameShort,"
        + " pv.shipment.clinic.name, year(pv.dateProcessed), {0}(pv.dateProcessed),"
        + " count(*) from edu.ualberta.med.biobank.model.PatientVisit pv"
        + " where pv.dateProcessed=(select min(pvCollection.dateProcessed)"
        + " from edu.ualberta.med.biobank.model.Patient p join p.patientVisitCollection"
        + " as pvCollection where p=pv.patient) and pv.patient.study.site "
        + SITE_OPERATOR + SITE_ID
        + " group by pv.patient.study.nameShort, pv.shipment.clinic.name,"
        + " year(pv.dateProcessed), {0}(pv.dateProcessed)";

    private DateRangeRowPostProcess dateRangePostProcess;

    public NewPsByStudyClinicImpl(List<Object> parameters,
        List<ReportOption> options) {
        super(QUERY, parameters, options);
        for (int i = 0; i < options.size(); i++) {
            ReportOption option = options.get(i);
            if (parameters.get(i) == null)
                parameters.set(i, option.getDefaultValue());
            if (option.getType().equals(String.class))
                parameters.set(i, "%" + parameters.get(i) + "%");
        }
        String groupBy = (String) parameters.remove(0);
        queryString = MessageFormat.format(queryString, groupBy);
        dateRangePostProcess = new DateRangeRowPostProcess(
            groupBy.equals("Year"), 2);
    }

    @Override
    protected AbstractRowPostProcess getRowPostProcess() {
        return dateRangePostProcess;
    }

}