package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenReport1Editor extends ReportsEditor {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenReport1Editor.class);

    @SuppressWarnings("nls")
    public static String ID =
        "edu.ualberta.med.biobank.editors.FTAReportEditor";

    private static final String STUDY_PARAM = Study.NAME.format(1).toString();
    @SuppressWarnings("nls")
    // label
    private static final String DATE_PARAM = i18n.tr("After Date (Drawn)");

    private ComboViewer studyCombo;
    private DateTimeWidget afterDate;

    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        studyCombo = createStudyComboOption(
            STUDY_PARAM,
            parent);
        afterDate = widgetCreator.createDateTimeWidget(parent,
            DATE_PARAM, null, null, null,
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

    @SuppressWarnings("nls")
    @Override
    protected String[] getColumnNames() {
        return new String[] {
            Patient.PropertyName.PNUMBER.toString(),
            // table column name
            i18n.tr("Date Drawn"),
            Specimen.PropertyName.INVENTORY_ID.toString(),
            SpecimenType.NAME.format(1).toString(),
            Site.NAME.format(1).toString(),
            // table column name
            i18n.tr("Location") };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> names = new ArrayList<String>();
        names.add(STUDY_PARAM);
        names.add(DATE_PARAM);
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
    public void setValues() throws Exception {
        studyCombo.getCombo().select(0);
        afterDate.setDate(null);
        super.setValues();
    }
}
