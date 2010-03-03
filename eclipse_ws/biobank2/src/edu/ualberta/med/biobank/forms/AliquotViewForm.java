package edu.ualberta.med.biobank.forms;

import java.util.Stack;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.SampleAdapter;
import edu.ualberta.med.biobank.widgets.grids.AbstractContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayFatory;

public class AliquotViewForm extends BiobankViewForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AliquotViewForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleViewForm";

    private SampleAdapter sampleAdapter;
    private AliquotWrapper sample;

    private Text sampleTypeLabel;

    private Text linkDateLabel;

    private Text quantityLabel;

    private Text shipmentWaybillLabel;

    private Text patientLabel;

    private Text visitLabel;

    private Text commentLabel;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof SampleAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        sampleAdapter = (SampleAdapter) adapter;
        sample = sampleAdapter.getSample();
        retrieveSample();
        setPartName("Aliquot: " + sample.getInventoryId());
    }

    private void retrieveSample() {
        try {
            sample.reload();
        } catch (Exception e) {
            logger.error("Can't reload sample with id " + sample.getId());
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Aliquot " + sample.getInventoryId());
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
    }

    private void createContainersSection() {
        Composite containersComposite = toolkit.createComposite(form.getBody());
        containersComposite.setLayout(new GridLayout(1, false));
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        containersComposite.setLayoutData(gd);
        toolkit.paintBordersFor(containersComposite);

        Stack<ContainerWrapper> parents = new Stack<ContainerWrapper>();
        ContainerWrapper container = sample.getParent();
        while (container != null) {
            parents.push(container);
            container = container.getParent();
        }
        while (!parents.isEmpty()) {
            container = parents.pop();
            RowColPos position;
            if (parents.isEmpty()) {
                position = sample.getPosition();
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
        setTextValue(sampleTypeLabel, sample.getSampleType().getName());
        setTextValue(linkDateLabel, sample.getFormattedLinkDate());
        setTextValue(quantityLabel, sample.getQuantity() == null ? null
            : sample.getQuantity().toString());
        setTextValue(shipmentWaybillLabel, sample.getPatientVisit()
            .getShipment().getWaybill());
        setTextValue(patientLabel, sample.getPatientVisit().getPatient()
            .getPnumber());
        setTextValue(visitLabel, DateFormatter.formatAsDateTime(sample
            .getPatientVisit().getDateProcessed()));
        setTextValue(commentLabel, sample.getComment());
    }

    @Override
    protected void reload() {
        retrieveSample();
        setValues();
        setPartName("Aliquot: " + sample.getInventoryId());
        form.setText("Aliquot: " + sample.getInventoryId());
    }

    @Override
    protected String getEntryFormId() {
        return null;
    }

}
