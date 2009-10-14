package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.model.PvCustomInfo;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.infotables.PvSampleSourceInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SamplesListWidget;

public class PatientVisitViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitViewForm";

    private static Logger LOGGER = Logger.getLogger(PatientVisitViewForm.class
        .getName());

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisitWrapper patientVisitWrapper;

    private Label siteLabel;

    private SamplesListWidget samplesWidget;

    private ListOrderedMap pvCustomInfoMap;

    private Label clinicLabel;

    private Label dateProcessedLabel;

    private Label dateReceivedLabel;

    private Label commentsLabel;

    public PatientVisitViewForm() {
        super();
        pvCustomInfoMap = new ListOrderedMap();

    }

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof PatientVisitAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientVisitAdapter = (PatientVisitAdapter) adapter;
        patientVisitWrapper = patientVisitAdapter.getWrapper();
        retrievePatientVisit();

        setPartName("Visit " + patientVisitWrapper.getFormattedDateDrawn());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Visit Drawn Date: "
            + patientVisitWrapper.getFormattedDateDrawn());
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_PATIENT_VISIT));
        createMainSection();
        createSourcesSection();
        createSamplesSection();
    }

    private void createMainSection() throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = (Label) createWidget(client, Label.class, SWT.NONE, "Site");
        clinicLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Clinic");
        dateProcessedLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Date Processed");
        dateReceivedLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Date Received");

        createPvDataSection(client);

        commentsLabel = (Label) createWidget(client, Label.class, SWT.WRAP,
            "Comments");

        setPatientVisitValues();
    }

    private void createPvDataSection(Composite client) throws Exception {
        String[] labels = patientVisitWrapper.getPvInfoLabels();
        if (labels == null)
            return;

        Label widget;
        for (String label : labels) {
            PvCustomInfo combinedPvInfo = new PvCustomInfo();
            combinedPvInfo.label = label;
            combinedPvInfo.type = patientVisitWrapper.getPvInfoType(label);
            combinedPvInfo.value = patientVisitWrapper.getPvInfo(label);

            pvCustomInfoMap.put(label, combinedPvInfo);

            Label labelWidget = toolkit.createLabel(client, label + ":",
                SWT.LEFT);
            labelWidget.setLayoutData(new GridData(
                GridData.VERTICAL_ALIGN_BEGINNING));

            int style = SWT.BORDER | SWT.LEFT;
            if (combinedPvInfo.type.equals(1) || combinedPvInfo.type.equals(5)) {
                style |= SWT.WRAP;
            }

            if ((combinedPvInfo.value != null) && combinedPvInfo.type.equals(5)) {
                combinedPvInfo.value = combinedPvInfo.value.replace(';', '\n');
            }

            widget = toolkit.createLabel(client, combinedPvInfo.value, style);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            widget.setLayoutData(gd);
        }
    }

    private void createSourcesSection() {
        Composite client = createSectionWithClient("Source Vessels");

        Collection<PvSampleSourceWrapper> sources = patientVisitWrapper
            .getPvSampleSourceCollection();
        new PvSampleSourceInfoTable(client, sources);
    }

    private void setPatientVisitValues() {
        FormUtils.setTextValue(siteLabel, patientVisitWrapper.getClinic()
            .getSite().getName());
        FormUtils.setTextValue(clinicLabel,
            patientVisitWrapper.getClinic() == null ? "" : patientVisitWrapper
                .getClinic().getName());
        FormUtils.setTextValue(dateProcessedLabel, patientVisitWrapper
            .getDateProcessed() == null ? "" : patientVisitWrapper
            .getDateProcessed());
        FormUtils.setTextValue(dateReceivedLabel, patientVisitWrapper
            .getDateReceived() == null ? "" : patientVisitWrapper
            .getDateReceived());
        FormUtils.setTextValue(commentsLabel,
            patientVisitWrapper.getComments() == null ? ""
                : patientVisitWrapper.getComments());
        // FIXME update all pvinfos ?
    }

    private void createSamplesSection() {
        Composite parent = createSectionWithClient("Samples");
        samplesWidget = new SamplesListWidget(parent, patientVisitAdapter
            .getParentFromClass(SiteAdapter.class), patientVisitWrapper
            .getSampleWrapperCollection());
        samplesWidget.adaptToToolkit(toolkit, true);
        samplesWidget.setSelection(patientVisitAdapter.getSelectedSample());
    }

    @Override
    protected void reload() {
        retrievePatientVisit();
        setPartName("Visit " + patientVisitWrapper.getFormattedDateDrawn());
        form.setText("Visit Drawn Date: "
            + patientVisitWrapper.getFormattedDateDrawn());
        setPatientVisitValues();
    }

    private void retrievePatientVisit() {
        try {
            patientVisitWrapper.reload();
        } catch (Exception ex) {
            LOGGER.error("Error while retrieving patient visit "
                + patientVisitWrapper.getDateDrawn(), ex);
        }
    }

    @Override
    protected String getEntryFormId() {
        return PatientVisitEntryForm.ID;
    }
}
