package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class FTAReportEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.FTAReportEditor";

    private ComboViewer studyCombo;
    private DateTimeWidget afterDate;

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100, 100, 100, 100 };
    }

    @Override
    protected void createOptionSection(Composite parent) {
        studyCombo = createStudyComboOption("Study", parent);
        afterDate = widgetCreator.createDateTimeWidget(parent,
            "After Date (Processed)", null, null, null);
    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(((StudyWrapper) ((IStructuredSelection) studyCombo
            .getSelection()).getFirstElement()).getNameShort());
        if (afterDate.getDate() == null)
            params.add(new Date(0));
        else
            params.add(afterDate.getDate());
        return params;
    }

    protected ComboViewer createStudyComboOption(String labelText,
        Composite parent) {
        Collection<StudyWrapper> studyWrappers;
        SiteWrapper site = SessionManager.getInstance().getCurrentSite();
        if (site.getName().compareTo("All Sites") != 0)
            studyWrappers = site.getStudyCollection(true);
        else
            studyWrappers = StudyWrapper.getAllStudies(SessionManager
                .getAppService());
        ComboViewer combo = widgetCreator.createComboViewer(parent, labelText,
            studyWrappers, null);
        combo.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((StudyWrapper) element).getNameShort();
            }
        });
        combo.getCombo().select(0);
        return combo;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Patient Number", "Date Processed",
            "Inventory Id", "Sample Type", "Location" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> names = new ArrayList<String>();
        names.add("Study");
        names.add("After Date (Processed)");
        return names;
    }
}
