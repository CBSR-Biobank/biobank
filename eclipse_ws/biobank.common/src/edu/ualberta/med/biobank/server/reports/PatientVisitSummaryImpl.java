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

    public static String QUERY_STRING = "SELECT study_name, clinic_name, sum(pvCount=1), sum(pvCount=2)," //$NON-NLS-1$
        + " sum(pvCount=3), sum(pvCount=4), sum(pvCount >=5), sum(pvCount), count(patient_number)" //$NON-NLS-1$
        + " FROM (SELECT t.name_short as study_name," //$NON-NLS-1$
        + "         c.name_short as clinic_name," //$NON-NLS-1$
        + "         p.pnumber as patient_number," //$NON-NLS-1$
        + "         count(distinct ce.id) as pvCount" //$NON-NLS-1$
        + "     FROM collection_event ce" //$NON-NLS-1$
        + "         join specimen s on s.original_collection_event_id=ce.id" // "source vessels" //$NON-NLS-1$
        + "         join origin_info oi on s.origin_info_id=oi.id" //$NON-NLS-1$
        + "         join center c on oi.center_id=c.id" //$NON-NLS-1$
        + "         join patient p on ce.patient_id=p.id" //$NON-NLS-1$
        + "         join study t on t.id=p.study_id" //$NON-NLS-1$
        + "     WHERE s.created_at between ? and ?" //$NON-NLS-1$
        + "     GROUP BY t.name_short, c.name_short, p.pnumber) as filteredPvs" //$NON-NLS-1$
        + " GROUP BY study_name, clinic_name" //$NON-NLS-1$
        + " ORDER BY study_name, clinic_name"; //$NON-NLS-1$

    public PatientVisitSummaryImpl(BiobankReport report) {
        super(QUERY_STRING, report);
        List<Object> parameters = report.getParams();
        this.queryString = queryString.replaceFirst("\\?", //$NON-NLS-1$
            "'" + DateFormatter.formatAsDateTime((Date) parameters.get(0)) //$NON-NLS-1$
                + "'"); //$NON-NLS-1$
        this.queryString = queryString.replaceFirst("\\?", //$NON-NLS-1$
            "'" + DateFormatter.formatAsDateTime((Date) parameters.get(1)) //$NON-NLS-1$
                + "'"); //$NON-NLS-1$
        report.setParams(parameters);
    }

    @Override
    public List<Object> executeQuery(WritableApplicationService appService)
        throws ApplicationException {
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
            sums[i] = 0L;
        for (Object obj : results) {
            Object[] castObj = (Object[]) obj;
            if (lastStudy.compareTo((String) castObj[0]) != 0) {
                totalledResults.add(new Object[] { lastStudy,
                    Messages.getString("PatientVisitSummaryImpl.clinics_label", //$NON-NLS-1$
                        report.getLocale()), sums[0], sums[1], sums[2],
                    sums[3], sums[4], sums[5], sums[6] });
                totalledResults.add(new Object[] { "", "", "", "", "", "", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                    "", "" }); //$NON-NLS-1$ //$NON-NLS-2$
                for (int i = 0; i < numSums; i++)
                    sums[i] = 0L;
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
        totalledResults.add(new Object[] { lastStudy,
            Messages.getString("PatientVisitSummaryImpl.clinics_label", //$NON-NLS-1$
                report.getLocale()), sums[0], sums[1], sums[2], sums[3],
            sums[4], sums[5], sums[6] });
        return totalledResults;
    }
}
