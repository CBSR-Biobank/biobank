package edu.ualberta.med.biobank.forms;

import java.util.Stack;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.treeview.SampleAdapter;
import edu.ualberta.med.biobank.widgets.grids.AbstractContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayFatory;

public class SampleViewForm extends BiobankViewForm {

    private static Logger LOGGER = Logger.getLogger(SampleViewForm.class
        .getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleViewForm";

    private SampleAdapter sampleAdapter;
    private SampleWrapper sample;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof SampleAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        sampleAdapter = (SampleAdapter) adapter;
        sample = sampleAdapter.getSample();
        retrieveSample();
        setPartName("Sample: " + sample.getInventoryId());
    }

    private void retrieveSample() {
        try {
            sample.reload();
        } catch (Exception e) {
            LOGGER.error("Can't reload sample with id " + sample.getId());
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Sample " + sample.getInventoryId() + " from patient "
            + sample.getPatientVisit().getPatient().getNumber() + " / visit "
            + sample.getPatientVisit().getFormattedDateDrawn());
        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createInformationSection();
        createContainersSection();
    }

    private void createInformationSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createWidget(client, Label.class, SWT.NONE, "Type", sample
            .getSampleType().getName());
        createWidget(client, Label.class, SWT.NONE, "Link Date", sample
            .getFormattedLinkDate());
        createWidget(client, Label.class, SWT.NONE, "Quantity", sample
            .getQuantity() == null ? null : sample.getQuantity().toString());
        createWidget(client, Label.class, SWT.NONE, "Quantity Used", sample
            .getQuantityUsed() == null ? null : sample.getQuantityUsed()
            .toString());
        createWidget(client, Label.class, SWT.NONE, "Comment", sample
            .getComment());
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
            toolkit.createLabel(containerComposite, container
                .getContainerType().getName()
                + ": " + container.getLabel());
            AbstractContainerDisplayWidget containerWidget = ContainerDisplayFatory
                .createWidget(containerComposite, container);
            containerWidget.setSelection(position);
            toolkit.adapt(containerWidget);
        }

    }

    @Override
    protected void reload() {
        retrieveSample();
        setPartName("Sample: " + sample.getInventoryId());
        form.setText("Sample: " + sample.getInventoryId());
    }

    @Override
    protected String getEntryFormId() {
        return null;
    }

}
