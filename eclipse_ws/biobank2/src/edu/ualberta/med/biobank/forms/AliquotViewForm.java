package edu.ualberta.med.biobank.forms;

import java.util.Stack;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
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

    private Text quantityLabel;

    private Text shipmentWaybillLabel;

    private Text patientLabel;

    private Text visitLabel;

    private Text commentLabel;

    private Text positionLabel;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof AliquotAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        aliquotAdapter = (AliquotAdapter) adapter;
        aliquot = aliquotAdapter.getSample();
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

        sampleTypeLabel = createReadOnlyField(client, SWT.NONE, "Type");
        linkDateLabel = createReadOnlyField(client, SWT.NONE, "Link Date");
        quantityLabel = createReadOnlyField(client, SWT.NONE, "Quantity");
        shipmentWaybillLabel = createReadOnlyField(client, SWT.NONE,
            "Shipment Waybill");
        patientLabel = createReadOnlyField(client, SWT.NONE, "Patient");
        visitLabel = createReadOnlyField(client, SWT.NONE, "Patient Visit");
        commentLabel = createReadOnlyField(client, SWT.WRAP, "Comment");
        positionLabel = createReadOnlyField(client, SWT.WRAP, "Position");
    }

    private void createContainersSection() {
        Section section = createSection("Containers Visualization");
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
            toolkit.createLabel(containerComposite, container.getLabel() + " ("
                + container.getContainerType().getNameShort() + ") ");
            AbstractContainerDisplayWidget containerWidget = ContainerDisplayFatory
                .createWidget(containerComposite, container);
            containerWidget.setSelection(position);
            toolkit.adapt(containerWidget);
        }

    }

    private void setValues() {
        setTextValue(sampleTypeLabel, aliquot.getSampleType().getName());
        setTextValue(linkDateLabel, aliquot.getFormattedLinkDate());
        setTextValue(quantityLabel, aliquot.getQuantity() == null ? null
            : aliquot.getQuantity().toString());
        setTextValue(shipmentWaybillLabel, aliquot.getPatientVisit()
            .getShipment().getWaybill());
        setTextValue(patientLabel, aliquot.getPatientVisit().getPatient()
            .getPnumber());
        setTextValue(visitLabel, DateFormatter.formatAsDateTime(aliquot
            .getPatientVisit().getDateProcessed()));
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

    @Override
    protected String getEntryFormId() {
        return null;
    }

}
