package edu.ualberta.med.biobank.reports;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.hibernate.Session;
import org.omg.CORBA.portable.ApplicationException;

public class BiobankReport implements QueryCommand {

    private static final long serialVersionUID = 1L;
    private static Map<String, ReportData> REPORTS =
        new TreeMap<String, ReportData>();
    public static final String EDITOR_PATH =
        "edu.ualberta.med.biobank.editors."; //$NON-NLS-1$
    private static final String REPORTS_FILE_NAME = "reports"; //$NON-NLS-1$

    static {
        ResourceBundle rb = ResourceBundle.getBundle(BiobankReport.class
            .getPackage().getName() + "." + REPORTS_FILE_NAME, //$NON-NLS-1$
            Locale.getDefault());
        // load map
        Enumeration<String> keysEnum = rb.getKeys();
        while (keysEnum.hasMoreElements()) {
            String key = keysEnum.nextElement();
            String pieces[] = key.split("[.]"); //$NON-NLS-1$
            ReportData r;
            if (REPORTS.get(pieces[0]) == null)
                r = new ReportData();
            else
                r = REPORTS.get(pieces[0]);
            if ("NAME".equals(pieces[1])) //$NON-NLS-1$
                r.name = rb.getString(key);
            else if ("DESCRIPTION".equals(pieces[1])) //$NON-NLS-1$
                r.description = rb.getString(key);
            else if ("EDITOR".equals(pieces[1])) //$NON-NLS-1$
                r.editorId = EDITOR_PATH + rb.getString(key);
            else if ("TYPE".equals(pieces[1])) //$NON-NLS-1$
                r.type = ReportType.valueOf(rb.getString(key));
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
    private Locale locale;

    public BiobankReport(ReportData data, Locale locale) {
        this.name = data.name;
        this.description = data.description;
        this.editorId = data.editorId;
        this.className = data.className;
        this.type = data.type;
        this.locale = locale;
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
        // suppose to be called on the client, so the locale is the correct one
        return new BiobankReport(data, Locale.getDefault());
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

    public Locale getLocale() {
        return locale;
    }
}
