package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankWizardDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import edu.ualberta.med.biobank.wizards.SelectCollectionEventWizard;

public class SpecimenEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenEntryForm";

    public static final String OK_MESSAGE = "Edit specimen";

    private SpecimenWrapper specimen;

    private ComboViewer activityStatusComboViewer;

    private ComboViewer specimenTypeComboViewer;

    private BgcBaseText volumeField;

    private BgcBaseText centerLabel;

    private BgcBaseText originCenterLabel;

    private BgcBaseText patientField;

    private BgcBaseText collectionText;

    private BgcBaseText commentText;

    @Override
    protected void init() throws Exception {
        specimen = (SpecimenWrapper) getModelObject();
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

        List<AliquotedSpecimenWrapper> allowedAliquotedSpecimen = study
            .getAliquotedSpecimenCollection(true);

        List<SpecimenTypeWrapper> containerSpecimenTypeList = null;
        if (specimen.hasParent()) {
            ContainerTypeWrapper ct = specimen.getParentContainer()
                .getContainerType();
            ct.reload();
            containerSpecimenTypeList = ct.getSpecimenTypeCollection();
        }

        List<SpecimenTypeWrapper> specimenTypes = new ArrayList<SpecimenTypeWrapper>();
        for (AliquotedSpecimenWrapper ss : allowedAliquotedSpecimen) {
            SpecimenTypeWrapper sst = ss.getSpecimenType();
            if (containerSpecimenTypeList == null) {
                specimenTypes.add(sst);
            } else {
                for (SpecimenTypeWrapper st : containerSpecimenTypeList) {
                    if (sst.equals(st))
                        specimenTypes.add(st);
                }
            }
        }
        if (specimen.getSpecimenType() != null
            && !specimenTypes.contains(specimen.getSpecimenType())) {
            specimenTypes.add(specimen.getSpecimenType());
        }

        specimenTypeComboViewer = createComboViewer(client, "Type",
            specimenTypes, specimen.getSpecimenType(),
            "Specimen must have a type", new ComboSelectionUpdate() {
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

        createReadOnlyLabelledField(client, SWT.NONE, "Created",
            specimen.getFormattedCreatedAt());

        volumeField = createReadOnlyLabelledField(client, SWT.NONE,
            "Volume (ml)", specimen.getQuantity() == null ? null : specimen
                .getQuantity().toString());

        createReadOnlyLabelledField(client, SWT.NONE, "Study", specimen
            .getCollectionEvent().getPatient().getStudy().getNameShort());

        Label label = widgetCreator.createLabel(client, "Patient");

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

        patientField = (BgcBaseText) widgetCreator.createBoundWidget(c,
            BgcBaseText.class, SWT.READ_ONLY, null, BeansObservables
                .observeValue(specimen, "collectionEvent.patient.pnumber"),
            null);
        patientField.setBackground(BgcWidgetCreator.READ_ONLY_TEXT_BGR);

        Button editPatientButton = new Button(c, SWT.NONE);
        editPatientButton.setText("Change Patient");

        toolkit.adapt(c);

        editPatientButton.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                SelectCollectionEventWizard wizard = new SelectCollectionEventWizard(
                    appService);
                WizardDialog dialog = new BiobankWizardDialog(page.getShell(),
                    wizard);
                int res = dialog.open();
                if (res == Status.OK) {
                    if (specimen.getCollectionEvent()
                        .getOriginalSpecimenCollection(false)
                        .contains(specimen)) {
                        // is original
                        specimen.setOriginalCollectionEvent(wizard
                            .getCollectionEvent());
                    }
                    specimen.setCollectionEvent(wizard.getCollectionEvent());
                    String comment = specimen.getComment();
                    if (comment == null)
                        comment = "";
                    else
                        comment += "\n";
                    comment += "Patient/Collection Event modification: "
                        + wizard.getComment();
                    specimen.setComment(comment);
                    patientField.setText(specimen.getCollectionEvent()
                        .getPatient().getPnumber());
                    collectionText.setText(specimen.getCollectionInfo());
                    commentText.setText(comment);
                    setDirty(true); // so changes can be saved
                }
            }
        });
        editPatientButton.setEnabled(specimen.getTopSpecimen().equals(specimen)
            && specimen.getChildSpecimenCollection(false).size() == 0);

        originCenterLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Origin center");
        setTextValue(originCenterLabel, specimen.getOriginInfo().getCenter()
            .getNameShort());
        centerLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Current center");
        setTextValue(centerLabel, specimen.getCenterString());

        createReadOnlyLabelledField(client, SWT.NONE, "Position",
            specimen.getPositionString(true, false));

        collectionText = createReadOnlyLabelledField(client, SWT.NONE,
            "Collection", specimen.getCollectionInfo());
        createReadOnlyLabelledField(client, SWT.NONE, "Parent Processed",
            (specimen.getTopSpecimen().equals(specimen) ? "" : specimen
                .getParentSpecimen().getProcessingEvent()
                .getFormattedCreatedAt()));
        createReadOnlyLabelledField(client, SWT.NONE, "Processed",
            specimen.getProcessingEvent() == null ? "" : specimen
                .getProcessingEvent().getFormattedCreatedAt());
        createReadOnlyLabelledField(client, SWT.NONE, "Children #",
            String.valueOf(specimen.getChildSpecimenCollection(false).size()));

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

        commentText = (BgcBaseText) createBoundWidgetWithLabel(client,
            BgcBaseText.class, SWT.WRAP | SWT.MULTI, "Comments", null,
            specimen, "comment", null);

        setFirstControl(specimenTypeComboViewer.getControl());
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
    protected void onReset() throws Exception {
        specimen.reset();

        GuiUtil.reset(activityStatusComboViewer, specimen.getActivityStatus());
        GuiUtil.reset(specimenTypeComboViewer, specimen.getSpecimenType());
    }

}
