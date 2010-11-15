package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class BiobankReport implements Serializable {

    private static final long serialVersionUID = 5851068592524377223L;
    private static Map<String, ReportData> REPORTS = new TreeMap<String, ReportData>();
    public static String editorPath = "edu.ualberta.med.biobank.editors.";

    static {
        Properties props = new Properties();
        try {
            props.load(BiobankReport.class
                .getResourceAsStream("reports.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // load map
        for (Object prop : props.keySet()) {
            String key = (String) prop;
            String pieces[] = key.split("[.]");
            ReportData r;
            if (REPORTS.get(pieces[0]) == null)
                r = new ReportData();
            else
                r = REPORTS.get(pieces[0]);
            if (pieces[1].equals("NAME"))
                r.name = (String) props.get(prop);
            else if (pieces[1].equals("DESCRIPTION"))
                r.description = (String) props.get(prop);
            else
                r.editorId = editorPath + (String) props.get(prop);
            r.className = pieces[0];
            REPORTS.put(pieces[0], r);
        }
    };

    protected String[] columnNames;

    protected List<Object> params;
    protected String containerList;

    private String name;
    private String description;
    private String op;
    private Integer siteId;
    private String editorId;
    private String groupBy;
    private String className;

    public BiobankReport(ReportData data) {
        this.name = data.name;
        this.description = data.description;
        this.editorId = data.editorId;
        this.className = data.className;
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

    public List<Object> getParams() {
        return params;
    }

    public String getGroupBy() {
        return groupBy;
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

    public void setGroupBy(String s) {
        this.groupBy = s;
    }

    public String getEditorId() {
        return editorId;
    }

    public String getClassName() {
        return className;
    }

    public void setContainerList(String s) {
        this.containerList = s;
    }

    public String getContainerList() {
        return containerList;
    }

}
