package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SpecimenTypeReport1Editor extends ReportsEditor {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenTypeReport1Editor.class);

    @SuppressWarnings("nls")
    public static String ID =
        "edu.ualberta.med.biobank.editors.AliquotCountEditor";

    private ComboViewer studyCombo;

    @Override
    protected void createOptionSection(Composite parent) throws Exception {
        List<StudyWrapper> studies = StudyWrapper.getAllStudies(SessionManager
            .getAppService());
        studyCombo = widgetCreator.createComboViewer(parent,
            Study.NAME.format(1).toString(), studies, null,
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

    @SuppressWarnings("nls")
    @Override
    protected String[] getColumnNames() {
        return new String[] {
            // table column name
            i18n.tr("Patient Number"),
            // table column name
            i18n.tr("First Time Processed"),
            // table column name
            i18n.tr("First Time Drawn"),
            SpecimenType.NAME.format(1).toString(),
            // table column name
            i18n.tr("Total") };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> name = new ArrayList<String>();
        name.add(Study.NAME.format(1).toString());
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
    public void setValues() throws Exception {
        studyCombo.getCombo().select(0);
        super.setValues();

    }
}
