package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;

public class SpecimenEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenEntryForm";

    public static final String OK_MESSAGE = "Edit specimen";

    private SpecimenWrapper specimen;

    private ComboViewer activityStatusComboViewer;

    private ComboViewer sampleTypeComboViewer;

    private BiobankText volumeField;

    private BiobankText siteLabel;

    @Override
    protected void init() throws Exception {
        SpecimenAdapter specimenAdapter = (SpecimenAdapter) adapter;
        specimen = specimenAdapter.getSpecimen();
        SessionManager.logEdit(specimen);
        setPartName("Specimen Entry");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Specimen " + specimen.getInventoryId() + " Information");
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true,
            false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        StudyWrapper study = specimen.getCollectionEvent().getPatient()
            .getStudy();
        study.reload();

        List<AliquotedSpecimenWrapper> allowedSampleStorage = study
            .getAliquotedSpecimenCollection(true);

        List<SpecimenTypeWrapper> containerSampleTypeList = null;
        if (specimen.hasParent()) {
            ContainerTypeWrapper ct = specimen.getParentContainer()
                .getContainerType();
            ct.reload();
            containerSampleTypeList = ct.getSpecimenTypeCollection();
        }

        List<SpecimenTypeWrapper> sampleTypes = new ArrayList<SpecimenTypeWrapper>();
        for (AliquotedSpecimenWrapper ss : allowedSampleStorage) {
            SpecimenTypeWrapper sst = ss.getSpecimenType();
            if (containerSampleTypeList == null) {
                sampleTypes.add(sst);
            } else {
                for (SpecimenTypeWrapper st : containerSampleTypeList) {
                    if (sst.equals(st))
                        sampleTypes.add(st);
                }
            }
        }
        if (specimen.getSpecimenType() != null
            && !sampleTypes.contains(specimen.getSpecimenType())) {
            sampleTypes.add(specimen.getSpecimenType());
        }

        siteLabel = createReadOnlyLabelledField(client, SWT.NONE, "Site");
        setTextValue(siteLabel, specimen.getCenterString());

        sampleTypeComboViewer = createComboViewer(client, "Type", sampleTypes,
            specimen.getSpecimenType(), "Specimen must have a sample type",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    specimen
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                    specimen.setQuantityFromType();
                    Double volume = specimen.getQuantity();
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
            specimen.getFormattedCreatedAt());

        volumeField = createReadOnlyLabelledField(client, SWT.NONE,
            "Volume (ml)", specimen.getQuantity() == null ? null : specimen
                .getQuantity().toString());

        createReadOnlyLabelledField(client, SWT.NONE, "Study", specimen
            .getCollectionEvent().getPatient().getStudy().getNameShort());

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
            SWT.READ_ONLY, null, BeansObservables.observeValue(specimen,
                "collectionEvent.patient.pnumber"), null);
        w.setBackground(WidgetCreator.READ_ONLY_TEXT_BGR);

        Button editPatientButton = new Button(c, SWT.NONE);
        editPatientButton.setText("Change Patient");

        toolkit.adapt(c);

        // FIXME
        // final BiobankText dateProcessed = createReadOnlyLabelledField(client,
        // SWT.NONE, "Date Processed", specimen.getParentProcessingEvent()
        // .getFormattedDateProcessed());
        //
        // final BiobankText dateDrawn = createReadOnlyLabelledField(client,
        // SWT.NONE, "Date Drawn", specimen.getParentProcessingEvent()
        // .getFormattedCreatedAt());
        //
        // editPatientButton.addListener(SWT.MouseUp, new Listener() {
        // @Override
        // public void handleEvent(Event event) {
        // SelectPatientVisitWizard wizard = new SelectPatientVisitWizard(
        // appService);
        // WizardDialog dialog = new BiobankWizardDialog(page.getShell(),
        // wizard);
        // int res = dialog.open();
        // if (res == Status.OK) {
        // specimen.setCollectionEvent(wizard.getCollectionEvent());
        //
        // dateProcessed.setText(specimen.getParentProcessingEvent()
        // .getFormattedDateProcessed());
        // dateDrawn.setText( specimen.getParentProcessingEvent()
        // .getFormattedCreatedAt());
        //
        // setDirty(true); // so changes can be saved
        // }
        // }
        // });

        createReadOnlyLabelledField(client, SWT.NONE, "Position",
            specimen.getPositionString(true, false));

        activityStatusComboViewer = createComboViewer(client,
            "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            specimen.getActivityStatus(),
            "Specimen must have an activity status",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    specimen
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.WRAP
            | SWT.MULTI, "Comments", null, specimen, "comment", null);

        setFirstControl(sampleTypeComboViewer.getControl());
    }

    @Override
    protected void saveForm() throws Exception {
        specimen.persist();
    }

    @Override
    protected String getOkMessage() {
        return OK_MESSAGE;
    }

    @Override
    public String getNextOpenedFormID() {
        return SpecimenViewForm.ID;
    }

    @Override
    public void setFocus() {
        // specimens are not present in treeviews, unnecessary reloads can be
        // prevented with this method
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        ActivityStatusWrapper currentActivityStatus = specimen
            .getActivityStatus();
        if (currentActivityStatus != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                currentActivityStatus));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }

        SpecimenTypeWrapper currentSampleType = specimen.getSpecimenType();
        if (currentSampleType != null)
            sampleTypeComboViewer.setSelection(new StructuredSelection(
                currentSampleType));

    }

}
