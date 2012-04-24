package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;

public class PatientReport1Editor extends ReportsEditor {
    private static final I18n i18n = I18nFactory
        .getI18n(PatientReport1Editor.class);

    protected DateTimeWidget start;
    protected DateTimeWidget end;

    @SuppressWarnings("nls")
    public static String ID =
        "edu.ualberta.med.biobank.editors.PatientVisitSummaryEditor";

    @SuppressWarnings("nls")
    @Override
    protected void createOptionSection(Composite parent) {
        start = widgetCreator.createDateTimeWidget(parent,
            // label
            i18n.tr("Start Date (Time Drawn)"),
            null, null, null,
            SWT.DATE);
        end = widgetCreator.createDateTimeWidget(parent,
            // label
            i18n.tr("End Date (Time Drawn)"),
            null, null, null,
            SWT.DATE);
    }

    @Override
    protected void initReport() {
        List<Object> params = new ArrayList<Object>();
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        report.setParams(params);
    }

    @SuppressWarnings("nls")
    @Override
    protected String[] getColumnNames() {
        return new String[] {
            Study.NAME.format(1).toString(),
            Clinic.NAME.format(1).toString(),
            // table column name
            i18n.tr("1 Visit"),
            // table column name
            i18n.tr("2 Visits"),
            // table column name
            i18n.tr("3 Visits"),
            // table column name
            i18n.tr("4 Visits"),
            // table column name
            i18n.tr("5+ Visits"),
            // table column name
            i18n.tr("Total Visits"),
            // table column name
            i18n.tr("Total Patients") };
    }

    @SuppressWarnings("nls")
    @Override
    protected List<String> getParamNames() {
        List<String> param = new ArrayList<String>();
        // parameter name
        param.add(i18n.tr("Start Date (Time Drawn)"));
        // parameter name
        param.add(i18n.tr("End Date (Time Drawn)"));
        return param;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(ReportsEditor.processDate(start.getDate(), true));
        params.add(ReportsEditor.processDate(end.getDate(), false));
        return params;
    }

    @Override
    public void setValues() throws Exception {
        start.setDate(null);
        end.setDate(null);
        super.setValues();
    }

}
