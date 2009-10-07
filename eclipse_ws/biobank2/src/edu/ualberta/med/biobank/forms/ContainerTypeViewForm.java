package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.ContainerDisplayWidget;

public class ContainerTypeViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeViewForm";

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType;

    private Capacity capacity;

    private Label siteLabel;

    private Label nameLabel;

    private Label nameShortLabel;

    private Label defaultTempLabel;

    private Label numSchemeLabel;

    private Label activityStatusLabel;

    private Label commentLabel;

    private Label dimOneCapacityLabel;

    private Label dimTwoCapacityLabel;

    private org.eclipse.swt.widgets.List sampleTypesList;

    private org.eclipse.swt.widgets.List childContainerTypesList;

    public ContainerTypeViewForm() {
        super();
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof ContainerTypeAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        containerTypeAdapter = (ContainerTypeAdapter) adapter;
        containerType = containerTypeAdapter.getContainerType();
        retrieveContainerType();
        setPartName("Container Type " + containerType.getName());
    }

    private void retrieveContainerType() throws Exception {
        containerType.reload();
        capacity = containerType.getCapacity();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container Type: " + containerType.getName());
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getIconForTypeName(
            containerType.getName()));

        createContainerTypeSection();
        if (containerType.getChildContainerTypeCollection().size() > 0) {
            createVisualizeContainer();
        }
        createDimensionsSection();
        if (containerType.getSampleTypeCollection() != null
            && containerType.getSampleTypeCollection().size() > 0) {
            createSampleTypesSection();
        }
        if (containerType.getChildContainerTypeCollection() != null
            && containerType.getChildContainerTypeCollection().size() > 0) {
            createChildContainerTypesSection();
        }
        createButtons();
    }

    private void createContainerTypeSection() {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = (Label) createWidget(client, Label.class, SWT.NONE, "Site");
        nameLabel = (Label) createWidget(client, Label.class, SWT.NONE, "Name");
        nameShortLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Short Name");
        defaultTempLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Default Temperature\n(Celcius)");
        numSchemeLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Child Labeling Scheme");
        activityStatusLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Activity Status");
        commentLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Comments");

        setContainerTypeValues();
    }

    private void setContainerTypeValues() {
        FormUtils.setTextValue(siteLabel, containerType.getSite().getName());
        FormUtils.setTextValue(nameLabel, containerType.getName());
        FormUtils.setTextValue(nameShortLabel, containerType.getNameShort());
        FormUtils.setTextValue(defaultTempLabel, containerType
            .getDefaultTemperature());
        FormUtils.setTextValue(numSchemeLabel, containerType
            .getChildLabelingScheme() == null ? "" : containerType
            .getChildLabelingScheme().getName());
        FormUtils.setTextValue(activityStatusLabel, containerType
            .getActivityStatus());
        FormUtils.setTextValue(commentLabel, containerType.getComment());
    }

    private void createDimensionsSection() {
        Composite client = createSectionWithClient("Default Capacity");
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        dimOneCapacityLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Maximum Rows");
        dimTwoCapacityLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Maximum Columns");

        setDimensionsValues();
    }

    private void setDimensionsValues() {
        FormUtils.setTextValue(dimOneCapacityLabel, capacity.getRowCapacity());
        FormUtils.setTextValue(dimTwoCapacityLabel, capacity.getColCapacity());
    }

    private void createSampleTypesSection() {
        Composite client = createSectionWithClient("Contains Samples");
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label = toolkit.createLabel(client, "Sample types:");
        label
            .setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

        sampleTypesList = new org.eclipse.swt.widgets.List(client, SWT.BORDER
            | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        sampleTypesList.setLayoutData(gd);
        setSampleTypesValues();
    }

    private void setSampleTypesValues() {
        sampleTypesList.removeAll();
        for (SampleTypeWrapper type : containerType.getSampleTypeCollection()) {
            sampleTypesList.add(type.getNameShort());
        }
    }

    private void createChildContainerTypesSection() {
        Composite client = createSectionWithClient("Contains Container Types");
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label = toolkit.createLabel(client, "Container types:");
        label
            .setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

        childContainerTypesList = new org.eclipse.swt.widgets.List(client,
            SWT.BORDER | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        childContainerTypesList.setLayoutData(gd);
        setChildContainerTypesValues();
    }

    protected void createVisualizeContainer() {
        Composite client = createSectionWithClient("Container Visual");
        // get occupied positions
        if (containerType.getName().startsWith("Drawer")) {
            // if Drawer, requires special grid
            CabinetDrawerWidget containerWidget = new CabinetDrawerWidget(
                client);
            GridData gdBin = new GridData();
            gdBin.verticalSpan = 2;
            containerWidget.setLayoutData(gdBin);
        } else {
            // otherwise, normal grid
            ContainerDisplayWidget containerWidget = new ContainerDisplayWidget(
                client);
            containerWidget.setContainerType(containerType);
            int dim2 = containerType.getCapacity().getColCapacity().intValue();
            if (dim2 <= 1) {
                // single dimension size
                containerWidget.setCellWidth(150);
                containerWidget.setCellHeight(20);
                containerWidget.setLegendOnSide(true);
            }
        }
    }

    private void setChildContainerTypesValues() {
        childContainerTypesList.removeAll();
        for (ContainerTypeWrapper type : containerType
            .getChildContainerTypeCollection()) {
            childContainerTypesList.add(type.getName());
        }
    }

    private void createButtons() {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(4, false));
        toolkit.paintBordersFor(client);
    }

    @Override
    protected void reload() throws Exception {
        retrieveContainerType();
        setPartName("Container Type " + containerType.getName());
        form.setText("Container Type: " + containerType.getName());
        setContainerTypeValues();
        setDimensionsValues();
        setSampleTypesValues();
        setChildContainerTypesValues();
    }

    @Override
    protected String getEntryFormId() {
        return ContainerTypeEntryForm.ID;
    }
}
