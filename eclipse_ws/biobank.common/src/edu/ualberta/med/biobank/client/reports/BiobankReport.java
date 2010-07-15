package edu.ualberta.med.biobank.client.reports;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class BiobankReport {

    private static Map<String, ReportData> REPORTS = new TreeMap<String, ReportData>();

    static {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File("reports.properties")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // load map
        for (Object prop : props.keySet()) {
            String key = (String) prop;
            String pieces[] = key.split(".");
            ReportData r;
            if (REPORTS.get(prop) == null)
                r = new ReportData();
            else
                r = REPORTS.get(prop);
            if (pieces[1].equals("NAME"))
                r.name = (String) props.get(prop);
            else
                r.description = (String) props.get(prop);
            REPORTS.put(key, r);
        }
    };

    protected int[] columnWidths;
    protected String[] columnNames;

    protected List<Object> params;
    protected List<String> strings;

    private String name;
    private String description;
    private String op;
    private Integer siteId;

    public BiobankReport(ReportData data) {
        this.name = data.name;
        this.description = data.description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public int[] getColumnWidths() {
        return columnWidths;
    }

    public List<Object> getParams() {
        return params;
    }

    public List<String> getStrings() {
        return strings;
    }

    public String getOp() {
        return op;
    }

    public Integer getSiteId() {
        return siteId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public static String[] getReportNames() {
        return REPORTS.keySet().toArray(new String[] {});
    }

    public static BiobankReport getReportByName(String name) {
        ReportData data = REPORTS.get(name);
        return new BiobankReport(data);
    }

    public List<Object> generate(WritableApplicationService appService)
        throws ApplicationException {
        if (appService instanceof BiobankApplicationService) {
            return ((BiobankApplicationService) appService).launchReport(this);
        }
        return null;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public void setSiteInfo(String op, Integer id) {
        this.op = op;
        this.siteId = id;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

}
