package edu.ualberta.med.biobank.server.reports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.server.query.BiobankSQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PatientVisitSummaryImpl extends AbstractReport {

    public static String QUERY_STRING =
        "select study_name, clinic_name, sum(pvCount=1), sum(pvCount=2), "
            + "sum(pvCount=3), sum(pvCount=4), sum(pvCount >=5), sum(pvCount), count(patient_number) "
            + "from (select s.name_short "
            + "as study_name, c.name_short as clinic_name, p.pnumber as patient_number, count(pv.id) as pvCount "
            + "from patient_visit pv "
            + "join clinic_shipment_patient csp on pv.clinic_shipment_patient_id=csp.id "
            + "join patient p on csp.patient_id=p.id join study s on s.id = p.study_id "
            + "join abstract_shipment sh on sh.id=csp.clinic_shipment_id join clinic c on c.id=sh.clinic_id where pv.date_processed "
            + "between ? and ? and sh.site_id "
            + SITE_OPERATOR_SEARCH_STRING
            + SITE_ID_SEARCH_STRING
            + " group by s.name_short, c.name_short, p.pnumber) as filteredPvs group by study_name, "
            + "clinic_name order by study_name, clinic_name";

    public PatientVisitSummaryImpl(BiobankReport report) {
        super(QUERY_STRING, report);
        List<Object> parameters = report.getParams();
        this.queryString =
            queryString.replaceFirst("\\?",
                "'" + DateFormatter.formatAsDateTime((Date) parameters.get(0))
                    + "'");
        this.queryString =
            queryString.replaceFirst("\\?",
                "'" + DateFormatter.formatAsDateTime((Date) parameters.get(1))
                    + "'");
        report.setParams(parameters);
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService)
        throws ApplicationException {
        queryString =
            queryString.replace(SITE_OPERATOR_SEARCH_STRING, report.getOp());
        queryString =
            queryString.replace(SITE_ID_SEARCH_STRING, report.getSiteId()
                .toString());
        return ((BiobankApplicationService) appService).query(
            new BiobankSQLCriteria(queryString), Site.class.getName());
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        if (results.size() == 0)
            return results;
        List<Object> totalledResults = new ArrayList<Object>();
        String lastStudy = (String) ((Object[]) results.get(0))[0];
        int numSums = 7;
        Long[] sums = new Long[numSums];
        for (int i = 0; i < numSums; i++)
            sums[i] = new Long(0);
        for (Object obj : results) {
            Object[] castObj = (Object[]) obj;
            if (lastStudy.compareTo((String) castObj[0]) != 0) {
                totalledResults.add(new Object[] { lastStudy, "All Clinics",
                    sums[0], sums[1], sums[2], sums[3], sums[4], sums[5],
                    sums[6] });
                totalledResults.add(new Object[] { "", "", "", "", "", "", "",
                    "", "" });
                for (int i = 0; i < numSums; i++)
                    sums[i] = new Long(0);
            }
            for (int i = 0; i < numSums; i++)
                if (castObj[i + 2] instanceof BigInteger)
                    sums[i] += ((BigInteger) castObj[i + 2]).longValue();
                else if (castObj[i + 2] instanceof BigDecimal)
                    sums[i] += ((BigDecimal) castObj[i + 2]).longValue();
            totalledResults.add(new Object[] { castObj[0], castObj[1],
                ((BigDecimal) castObj[2]).longValue(),
                ((BigDecimal) castObj[3]).longValue(),
                ((BigDecimal) castObj[4]).longValue(),
                ((BigDecimal) castObj[5]).longValue(),
                ((BigDecimal) castObj[6]).longValue(),
                ((BigDecimal) castObj[7]).longValue(),
                ((BigInteger) castObj[8]).longValue() });
            lastStudy = (String) castObj[0];
        }
        totalledResults.add(new Object[] { lastStudy, "All Clinics", sums[0],
            sums[1], sums[2], sums[3], sums[4], sums[5], sums[6] });
        return totalledResults;
    }
}
