package edu.ualberta.med.biobank.common.reports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.hibernate.Session;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.server.reports.ReportFactory;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class BiobankReport implements QueryCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Map<String, ReportData> REPORTS = new TreeMap<String, ReportData>();
    public static String editorPath = "edu.ualberta.med.biobank.editors."; //$NON-NLS-1$

    static {
        Properties props = new Properties();
        try {
            props.load(BiobankReport.class
                .getResourceAsStream("reports.properties")); //$NON-NLS-1$
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // load map
        for (Map.Entry<Object, Object> prop : props.entrySet()) {
            String key = (String) prop.getKey();
            String pieces[] = key.split("[.]"); //$NON-NLS-1$
            ReportData r;
            if (REPORTS.get(pieces[0]) == null)
                r = new ReportData();
            else
                r = REPORTS.get(pieces[0]);
            if ("NAME".equals(pieces[1])) //$NON-NLS-1$
                r.name = (String) prop.getValue();
            else if ("DESCRIPTION".equals(pieces[1])) //$NON-NLS-1$
                r.description = (String) prop.getValue();
            else if ("EDITOR".equals(pieces[1])) //$NON-NLS-1$
                r.editorId = editorPath + (String) prop.getValue();
            else if ("TYPE".equals(pieces[1])) //$NON-NLS-1$
                r.type = ReportType.valueOf((String) prop.getValue());
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
    private ReportType type;

    public BiobankReport(ReportData data) {
        this.name = data.name;
        this.description = data.description;
        this.editorId = data.editorId;
        this.className = data.className;
        this.type = data.type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ReportType getType() {
        return type;
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

    @Override
    public List<Object> start(Session s, BiobankApplicationService appService)
        throws ApplicationException {
        return ReportFactory.createReport(this).generate(appService);
    }

}
