package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SampleTypePvCountEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotCountEditor";

    private ComboViewer studyCombo;

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100, 100, 100, 100 };
    }

    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        studyCombo = createStudyComboOption("Study", parent);
    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(((StudyWrapper) ((IStructuredSelection) studyCombo
            .getSelection()).getFirstElement()).getNameShort());
        return params;
    }

    protected ComboViewer createStudyComboOption(String labelText,
        Composite parent) throws ApplicationException {
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
        return new String[] { "Patient Number", "Date Processed", "Date Drawn",
            "Sample Type", "Total" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> name = new ArrayList<String>();
        name.add("Study");
        return name;
    }

}
