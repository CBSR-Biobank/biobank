package edu.ualberta.med.biobank.client.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ualberta.med.biobank.common.util.ReportOption;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractReport {

    private static Map<String, Class<? extends AbstractReport>> REPORTS = new TreeMap<String, Class<? extends AbstractReport>>();

    static {
        Map<String, Class<? extends AbstractReport>> aMap = new TreeMap<String, Class<? extends AbstractReport>>();
        aMap.put(CabinetCAliquots.NAME, CabinetCAliquots.class);
        aMap.put(CabinetDAliquots.NAME, CabinetDAliquots.class);
        aMap.put(CabinetSAliquots.NAME, CabinetSAliquots.class);
        aMap.put(FreezerCAliquots.NAME, FreezerCAliquots.class);
        aMap.put(FreezerDAliquots.NAME, FreezerDAliquots.class);
        aMap.put(FreezerSAliquots.NAME, FreezerSAliquots.class);
        aMap.put(FvLPatientVisits.NAME, FvLPatientVisits.class);
        aMap.put(NewPVsByStudyClinic.NAME, NewPVsByStudyClinic.class);
        aMap.put(NewPsByStudyClinic.NAME, NewPsByStudyClinic.class);
        aMap.put(PsByStudy.NAME, PsByStudy.class);
        aMap.put(PVsByStudy.NAME, PVsByStudy.class);
        aMap.put(PatientVisitSummary.NAME, PatientVisitSummary.class);
        aMap.put(PatientWBC.NAME, PatientWBC.class);
        aMap.put(AliquotsByPallet.NAME, AliquotsByPallet.class);
        aMap.put(AliquotCount.NAME, AliquotCount.class);
        aMap.put(AliquotInvoiceByClinic.NAME, AliquotInvoiceByClinic.class);
        aMap.put(AliquotInvoiceByPatient.NAME, AliquotInvoiceByPatient.class);
        aMap.put(AliquotRequest.NAME, AliquotRequest.class);
        aMap.put(AliquotSCount.NAME, AliquotSCount.class);
        aMap.put(SampleTypePvCount.NAME, SampleTypePvCount.class);
        aMap.put(SampleTypeSUsage.NAME, SampleTypeSUsage.class);
        aMap.put(QACabinetAliquots.NAME, QACabinetAliquots.class);
        aMap.put(QAFreezerAliquots.NAME, QAFreezerAliquots.class);
        REPORTS = Collections.unmodifiableMap(aMap);
    };

    /**
     * Description of this query object
     */
    private String description;

    /**
     * Column names for the result
     */
    private String[] columnNames;

    private List<ReportOption> queryOptions;

    public AbstractReport(String description, String[] columnNames) {
        this.description = description;
        queryOptions = new ArrayList<ReportOption>();
        this.columnNames = columnNames;
    }

    public abstract String getName();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<ReportOption> getOptions() {
        return queryOptions;
    }

    public void addOption(String name, Class<?> type, Object defaultValue) {
        queryOptions.add(new ReportOption(name, type, defaultValue));
    }

    public void setOptions(List<ReportOption> queryOptions) {
        this.queryOptions = queryOptions;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public static String[] getReportNames() {
        return REPORTS.keySet().toArray(new String[] {});
    }

    public static Class<? extends AbstractReport> getReportByName(String name)
        throws Exception {
        Class<? extends AbstractReport> queryObject = REPORTS.get(name);
        if (queryObject == null) {
            throw new Exception("Report \"" + name + "\" does not exist");
        }
        return queryObject;
    }

    public List<Object> generate(WritableApplicationService appService,
        ArrayList<Object> parameters, String siteOperator, Integer siteId)
        throws ApplicationException {
        if (appService instanceof BiobankApplicationService) {
            return ((BiobankApplicationService) appService).launchReport(
                getClass().getName(), parameters, getOptions(), siteOperator,
                siteId);
        }
        return null;
    }
}
