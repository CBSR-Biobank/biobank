package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenTypePvCountEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotCountEditor";

    private ComboViewer studyCombo;

    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        studyCombo = createStudyComboOption("Study", parent);
    }

    @Override
    protected void initReport() {
        List<Object> params = new ArrayList<Object>();
        params.add(((StudyWrapper) ((IStructuredSelection) studyCombo
            .getSelection()).getFirstElement()).getNameShort());
        report.setParams(params);
    }

    protected ComboViewer createStudyComboOption(String labelText,
        Composite parent) throws ApplicationException {
        Collection<StudyWrapper> studyWrappers;
        studyWrappers = StudyWrapper.getAllStudies(SessionManager
            .getAppService());
        ComboViewer combo = widgetCreator.createComboViewer(parent, labelText,
            studyWrappers, null, new BiobankLabelProvider());
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
        return new String[] { "Patient Number", "First Time Processed",
            "First Time Drawn", "Sample Type", "Total" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> name = new ArrayList<String>();
        name.add("Study");
        return name;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> printParams = new ArrayList<Object>();
        printParams.add(((StudyWrapper) ((IStructuredSelection) studyCombo
            .getSelection()).getFirstElement()).getNameShort());
        return printParams;
    }
}
