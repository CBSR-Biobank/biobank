package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.AliquotAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class AliquotEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.AliquotEntryForm";

    public static final String OK_MESSAGE = "Edit aliquot";

    private AliquotWrapper aliquot;

    private ComboViewer activityStatusComboViewer;

    private ComboViewer sampleTypeComboViewer;

    @Override
    protected void init() throws Exception {
        AliquotAdapter aliquotAdapter = (AliquotAdapter) adapter;
        aliquot = aliquotAdapter.getAliquot();
        setPartName("Aliquot Entry");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Aliquot " + aliquot.getInventoryId() + " Information");
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(
            new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        StudyWrapper study = aliquot.getPatientVisit().getPatient().getStudy();
        study.reload();

        List<SampleStorageWrapper> allowedSampleStorage = study
            .getSampleStorageCollection();

        ContainerTypeWrapper ct = aliquot.getParent().getContainerType();
        ct.reload();

        List<SampleTypeWrapper> containerSampleTypeList = ct
            .getSampleTypeCollection();

        List<SampleTypeWrapper> sampleTypes = new ArrayList<SampleTypeWrapper>();

        for (SampleStorageWrapper ss : allowedSampleStorage) {
            SampleTypeWrapper sst = ss.getSampleType();
            for (SampleTypeWrapper st : containerSampleTypeList) {
                if (sst.equals(st))
                    sampleTypes.add(st);
            }
        }

        sampleTypeComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Type", sampleTypes, aliquot.getSampleType(),
            "Aliquot must have a sample type", true);

        createReadOnlyLabelledField(client, SWT.NONE, "Link Date", aliquot
            .getFormattedLinkDate());

        createReadOnlyLabelledField(client, SWT.NONE, "Volume (ml)", aliquot
            .getQuantity() == null ? null : aliquot.getQuantity().toString());

        createReadOnlyLabelledField(client, SWT.NONE, "Shipment Waybill",
            aliquot.getPatientVisit().getShipment().getWaybill());

        createReadOnlyLabelledField(client, SWT.NONE, "Patient Number", aliquot
            .getPatientVisit().getPatient().getPnumber());

        createReadOnlyLabelledField(client, SWT.NONE, "Date Processed", aliquot
            .getPatientVisit().getFormattedDateProcessed());

        createReadOnlyLabelledField(client, SWT.NONE, "Date Drawn", aliquot
            .getPatientVisit().getFormattedDateDrawn());

        createReadOnlyLabelledField(client, SWT.NONE, "Position", aliquot
            .getPositionString(true, false));

        activityStatusComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Activity Status", ActivityStatusWrapper
                .getAllActivityStatuses(appService), aliquot
                .getActivityStatus(), "Aliquot must have an activity status",
            true);

        BiobankText comment = (BiobankText) createBoundWidgetWithLabel(client,
            BiobankText.class, SWT.WRAP | SWT.MULTI, "Comments", null,
            BeansObservables.observeValue(aliquot, "comment"), null);

        setFirstControl(sampleTypeComboViewer.getControl());
    }

    @Override
    protected void saveForm() throws Exception {
        ActivityStatusWrapper activity = (ActivityStatusWrapper) ((StructuredSelection) activityStatusComboViewer
            .getSelection()).getFirstElement();
        aliquot.setActivityStatus(activity);
        SampleTypeWrapper st = (SampleTypeWrapper) ((StructuredSelection) sampleTypeComboViewer
            .getSelection()).getFirstElement();
        aliquot.setSampleType(st);
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
