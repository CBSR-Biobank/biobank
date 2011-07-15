package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SpecimenTypePvCountEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotCountEditor"; //$NON-NLS-1$

    private ComboViewer studyCombo;

    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        List<StudyWrapper> studies = StudyWrapper.getAllStudies(SessionManager
            .getAppService());
        studyCombo = widgetCreator.createComboViewer(parent,
            Messages.SpecimenTypePvCountEditor_study_label, studies, null,
            new BiobankLabelProvider());
        studyCombo.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((StudyWrapper) element).getNameShort();
            }
        });
        studyCombo.getCombo().select(0);
    }

    @Override
    protected void initReport() {
        List<Object> params = new ArrayList<Object>();
        params.add(((StudyWrapper) ((IStructuredSelection) studyCombo
            .getSelection()).getFirstElement()).getNameShort());
        report.setParams(params);
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { Messages.SampleTypePvCountEditor_pnumber_label,
            Messages.SampleTypePvCountEditor_first_processed_label,
            Messages.SampleTypePvCountEditor_first_drawn_label,
            Messages.SampleTypePvCountEditor_specType_label,
            Messages.SampleTypePvCountEditor_total_label };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> name = new ArrayList<String>();
        name.add(Messages.SpecimenTypePvCountEditor_study_label);
        return name;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> printParams = new ArrayList<Object>();
        printParams.add(((StudyWrapper) ((IStructuredSelection) studyCombo
            .getSelection()).getFirstElement()).getNameShort());
        return printParams;
    }

    @Override
    protected void onReset() throws Exception {
        studyCombo.getCombo().select(0);
        super.onReset();

    }
}
