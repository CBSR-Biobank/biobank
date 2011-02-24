package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankWizardDialog;
import edu.ualberta.med.biobank.treeview.AliquotAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import edu.ualberta.med.biobank.wizards.SelectPatientVisitWizard;

public class AliquotEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.AliquotEntryForm";

    public static final String OK_MESSAGE = "Edit aliquot";

    private AliquotWrapper aliquot;

    private ComboViewer activityStatusComboViewer;

    private ComboViewer sampleTypeComboViewer;

    private BiobankText volumeField;

    private BiobankText siteLabel;

    @Override
    protected void init() throws Exception {
        AliquotAdapter aliquotAdapter = (AliquotAdapter) adapter;
        aliquot = aliquotAdapter.getAliquot();
        aliquot.logEdit(aliquot.getCenterString());
        setPartName("Aliquot Entry");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Aliquot " + aliquot.getInventoryId() + " Information");
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true,
            false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        StudyWrapper study = aliquot.getProcessingEvent().getPatient()
            .getStudy();
        study.reload();

        List<SampleStorageWrapper> allowedSampleStorage = study
            .getSampleStorageCollection();

        List<SampleTypeWrapper> containerSampleTypeList = null;
        if (aliquot.hasParent()) {
            ContainerTypeWrapper ct = aliquot.getParent().getContainerType();
            ct.reload();
            containerSampleTypeList = ct.getSampleTypeCollection();
        }

        List<SampleTypeWrapper> sampleTypes = new ArrayList<SampleTypeWrapper>();
        for (SampleStorageWrapper ss : allowedSampleStorage) {
            SampleTypeWrapper sst = ss.getSampleType();
            if (containerSampleTypeList == null) {
                sampleTypes.add(sst);
            } else {
                for (SampleTypeWrapper st : containerSampleTypeList) {
                    if (sst.equals(st))
                        sampleTypes.add(st);
                }
            }
        }
        if (aliquot.getSampleType() != null
            && !sampleTypes.contains(aliquot.getSampleType())) {
            sampleTypes.add(aliquot.getSampleType());
        }

        siteLabel = createReadOnlyLabelledField(client, SWT.NONE, "Site");
        setTextValue(siteLabel, aliquot.getCenterString());

        sampleTypeComboViewer = createComboViewer(client, "Type", sampleTypes,
            aliquot.getSampleType(), "Aliquot must have a sample type",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    aliquot.setSampleType((SampleTypeWrapper) selectedObject);
                    aliquot.setQuantityFromType();
                    Double volume = aliquot.getQuantity();
                    if (volumeField != null) {
                        if (volume == null) {
                            volumeField.setText("");
                        } else {
                            volumeField.setText(volume.toString());
                        }
                    }
                }
            });

        createReadOnlyLabelledField(client, SWT.NONE, "Link Date",
            aliquot.getFormattedLinkDate());

        volumeField = createReadOnlyLabelledField(client, SWT.NONE,
            "Volume (ml)", aliquot.getQuantity() == null ? null : aliquot
                .getQuantity().toString());

        createReadOnlyLabelledField(client, SWT.NONE, "Study", aliquot
            .getProcessingEvent().getPatient().getStudy().getNameShort());

        Label label = widgetCreator.createLabel(client, "Patient Number");

        Composite c = new Composite(client, SWT.NONE);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        c.setLayoutData(gd);
        GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        c.setLayout(gl);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        Control w = widgetCreator.createBoundWidget(c, BiobankText.class,
            SWT.READ_ONLY, null, BeansObservables.observeValue(aliquot,
                "patientVisit.patient.pnumber"), null);
        w.setBackground(WidgetCreator.READ_ONLY_TEXT_BGR);

        Button editPatientButton = new Button(c, SWT.NONE);
        editPatientButton.setText("Change Patient");

        toolkit.adapt(c);

        final BiobankText dateProcessed = createReadOnlyLabelledField(client,
            SWT.NONE, "Date Processed", aliquot.getProcessingEvent()
                .getFormattedDateProcessed());

        final BiobankText dateDrawn = createReadOnlyLabelledField(client,
            SWT.NONE, "Date Drawn", aliquot.getProcessingEvent()
                .getFormattedDateDrawn());

        editPatientButton.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                SelectPatientVisitWizard wizard = new SelectPatientVisitWizard(
                    appService);
                WizardDialog dialog = new BiobankWizardDialog(page.getShell(),
                    wizard);
                int res = dialog.open();
                if (res == Status.OK) {
                    aliquot.setProcessingEvent(wizard.getProcessingEvent());

                    dateProcessed.setText(aliquot.getProcessingEvent()
                        .getFormattedDateProcessed());
                    dateDrawn.setText(aliquot.getProcessingEvent()
                        .getFormattedDateDrawn());

                    setDirty(true); // so changes can be saved
                }
            }
        });

        createReadOnlyLabelledField(client, SWT.NONE, "Position",
            aliquot.getPositionString(true, false));

        activityStatusComboViewer = createComboViewer(client,
            "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            aliquot.getActivityStatus(),
            "Aliquot must have an activity status", new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    aliquot
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.WRAP
            | SWT.MULTI, "Comments", null, aliquot, "comment", null);

        setFirstControl(sampleTypeComboViewer.getControl());
    }

    @Override
    protected void saveForm() throws Exception {
        aliquot.persist();
    }

    @Override
    protected String getOkMessage() {
        return OK_MESSAGE;
    }

    @Override
    public String getNextOpenedFormID() {
        return AliquotViewForm.ID;
    }

    @Override
    public void setFocus() {
        // aliquots are not present in treeviews, unnecessary reloads can be
        // prevented with this method
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        ActivityStatusWrapper currentActivityStatus = aliquot
            .getActivityStatus();
        if (currentActivityStatus != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                currentActivityStatus));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }

        SampleTypeWrapper currentSampleType = aliquot.getSampleType();
        if (currentSampleType != null)
            sampleTypeComboViewer.setSelection(new StructuredSelection(
                currentSampleType));

    }

}
