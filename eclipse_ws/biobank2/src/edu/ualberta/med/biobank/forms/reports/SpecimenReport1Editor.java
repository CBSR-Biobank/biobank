package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenReport1Editor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.FTAReportEditor"; //$NON-NLS-1$

    private ComboViewer studyCombo;
    private DateTimeWidget afterDate;

    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        studyCombo = createStudyComboOption(
            Messages.FTAReportEditor_study_label, parent);
        afterDate = widgetCreator.createDateTimeWidget(parent,
            Messages.FTAReportEditor_drawn_after_label, null, null, null,
            SWT.DATE);
    }

    @Override
    protected void initReport() {
        List<Object> params = new ArrayList<Object>();
        params.add(((StudyWrapper) ((IStructuredSelection) studyCombo
            .getSelection()).getFirstElement()).getNameShort());
        params.add(ReportsEditor.processDate(afterDate.getDate(), true));
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
        return new String[] { Messages.FTAReportEditor_pnumber_label,
            Messages.FTAReportEditor_drawn_label,
            Messages.FTAReportEditor_inventoryid_label,
            Messages.FTAReportEditor_specType_label,
            Messages.FTAReportEditor_site_label,
            Messages.FTAReportEditor_location_label };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> names = new ArrayList<String>();
        names.add(Messages.FTAReportEditor_study_label);
        names.add(Messages.FTAReportEditor_drawn_after_label);
        return names;
    }

    @Override
    protected List<Object> getPrintParams() throws Exception {
        List<Object> params = new ArrayList<Object>();
        params.add(((StudyWrapper) ((IStructuredSelection) studyCombo
            .getSelection()).getFirstElement()).getNameShort());
        params.add(ReportsEditor.processDate(afterDate.getDate(), true));
        return params;
    }

    @Override
    protected void onReset() throws Exception {
        studyCombo.getCombo().select(0);
        afterDate.setDate(null);
        super.onReset();
    }
}
