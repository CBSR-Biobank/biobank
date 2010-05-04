package edu.ualberta.med.biobank.forms;

import java.util.Stack;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.dialogs.AliquotStatusDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.AliquotAdapter;
import edu.ualberta.med.biobank.widgets.grids.AbstractContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayFatory;

public class AliquotViewForm extends BiobankViewForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AliquotViewForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleViewForm";

    private AliquotAdapter aliquotAdapter;

    private AliquotWrapper aliquot;

    private Text sampleTypeLabel;

    private Text linkDateLabel;

    private Text volumeLabel;

    private Text shipmentWaybillLabel;

    private Text patientLabel;

    private Text dateProcessedLabel;

    private Text dateDrawnLabel;

    private Text commentLabel;

    private Text positionLabel;

    private Button activityStatusButton;

    private Text activityStatusText;

    private Text activityStatusLabel;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof AliquotAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        aliquotAdapter = (AliquotAdapter) adapter;
        aliquot = aliquotAdapter.getAliquot();
        retrieveAliquot();
        setPartName("Aliquot: " + aliquot.getInventoryId());
    }

    private void retrieveAliquot() {
        try {
            aliquot.reload();
        } catch (Exception e) {
            logger.error("Can't reload aliquot with id " + aliquot.getId());
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Aliquot " + aliquot.getInventoryId());
        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createInformationSection();
        createContainersSection();
        setValues();
    }

    private void createInformationSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        sampleTypeLabel = createReadOnlyLabelledField(client, SWT.NONE, "Type");
        linkDateLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Link Date");
        volumeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Volume (ml)");
        shipmentWaybillLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Shipment Waybill");
        patientLabel = createReadOnlyLabelledField(client, SWT.NONE, "Patient");
        dateProcessedLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Date Processed");
        dateDrawnLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Date Drawn");
        createActivityStatusSection(client);
        commentLabel = createReadOnlyLabelledField(client, SWT.WRAP, "Comment");
        positionLabel = createReadOnlyLabelledField(client, SWT.WRAP,
            "Position");
    }

    private void createContainersSection() {
        if (aliquot.getParent() != null) {
            Section section = createSection("Container Visualization");
            Composite containersComposite = toolkit.createComposite(section);
            section.setClient(containersComposite);
            section.setExpanded(false);
            containersComposite.setLayout(new GridLayout(1, false));
            toolkit.paintBordersFor(containersComposite);

            Stack<ContainerWrapper> parents = new Stack<ContainerWrapper>();
            ContainerWrapper container = aliquot.getParent();
            while (container != null) {
                parents.push(container);
                container = container.getParent();
            }
            while (!parents.isEmpty()) {
                container = parents.pop();
                RowColPos position;
                if (parents.isEmpty()) {
                    position = aliquot.getPosition();
                } else {
                    position = parents.peek().getPosition();
                }
                Composite containerComposite = toolkit
                    .createComposite(containersComposite);
                GridLayout layout = new GridLayout(1, false);
                layout.horizontalSpacing = 0;
                layout.marginWidth = 0;
                layout.verticalSpacing = 0;
                containerComposite.setLayout(layout);
                toolkit
                    .createLabel(containerComposite, container.getLabel()
                        + " (" + container.getContainerType().getNameShort()
                        + ") ");
                AbstractContainerDisplayWidget containerWidget = ContainerDisplayFatory
                    .createWidget(containerComposite, container);
                containerWidget.setSelection(position);
                toolkit.adapt(containerWidget);
            }
        }
    }

    private void createActivityStatusSection(Composite client) {
        if (aliquot.canEdit()) {
            toolkit.createLabel(client, "Activity Status:", SWT.SINGLE);
            Composite activityArea = new Composite(client, SWT.BORDER);
            toolkit.adapt(activityArea);

            GridLayout layout = new GridLayout(2, false);
            layout.marginTop = 0;
            layout.marginBottom = 0;
            activityArea.setLayout(layout);

            GridData gd = new GridData();
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalAlignment = SWT.FILL;
            activityArea.setLayoutData(gd);

            activityStatusText = createReadOnlyWidget(activityArea,
                SWT.READ_ONLY | SWT.BEGINNING, aliquot.getActivityStatus()
                    .getName());
            activityStatusText.setLayoutData(new GridData(SWT.BEGINNING,
                SWT.CENTER, false, false));

            activityStatusButton = new Button(activityArea, SWT.NONE
                | SWT.BORDER);
            activityStatusButton.setImage(BioBankPlugin.getDefault()
                .getImageRegistry().get(BioBankPlugin.IMG_EDIT_FORM));
            activityStatusButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    AliquotStatusDialog dlg = new AliquotStatusDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), aliquot);
                    if (dlg.open() == Dialog.OK) {
                        aliquot.setActivityStatus(dlg.getActivityStatus());
                        BusyIndicator.showWhile(Display.getDefault(),
                            new Runnable() {
                                public void run() {
                                    try {
                                        aliquot.persist();
                                    } catch (Exception e) {
                                        BioBankPlugin.openAsyncError(
                                            "Error saving aliquot", e);
                                    }
                                    setTextValue(activityStatusText, aliquot
                                        .getActivityStatus());
                                    activityStatusText.getParent().layout();
                                }
                            });
                    }
                }
            });
        } else {
            activityStatusLabel = createReadOnlyLabelledField(client, SWT.WRAP,
                "Activity Status");
        }
    }

    private void setValues() {
        setTextValue(sampleTypeLabel, aliquot.getSampleType().getName());
        setTextValue(linkDateLabel, aliquot.getFormattedLinkDate());
        setTextValue(volumeLabel, aliquot.getQuantity() == null ? null
            : aliquot.getQuantity().toString());
        setTextValue(shipmentWaybillLabel, aliquot.getPatientVisit()
            .getShipment().getWaybill());
        setTextValue(patientLabel, aliquot.getPatientVisit().getPatient()
            .getPnumber());
        setTextValue(dateProcessedLabel, aliquot.getPatientVisit()
            .getFormattedDateProcessed());
        setTextValue(dateDrawnLabel, aliquot.getPatientVisit()
            .getFormattedDateDrawn());
        if (activityStatusText != null) {
            setTextValue(activityStatusText, aliquot.getActivityStatus());
        } else if (activityStatusLabel != null) {
            setTextValue(activityStatusLabel, aliquot.getActivityStatus());
        }
        setTextValue(commentLabel, aliquot.getComment());
        setTextValue(positionLabel, aliquot.getPositionString(true, false));
    }

    @Override
    protected void reload() {
        retrieveAliquot();
        setValues();
        setPartName("Aliquot: " + aliquot.getInventoryId());
        form.setText("Aliquot: " + aliquot.getInventoryId());
    }

}
