package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayFatory;

public class ContainerTypeViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeViewForm";

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType;

    private Text siteLabel;

    private Text nameLabel;

    private Text nameShortLabel;

    private Button isTopLevelButton;

    private Text rowCapacityLabel;

    private Text colCapacityLabel;

    private Text defaultTempLabel;

    private Text numSchemeLabel;

    private Text activityStatusLabel;

    private Text commentLabel;

    private ListViewer sampleTypesViewer;

    private ListViewer childContainerTypesViewer;

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
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container Type: " + containerType.getName());
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getIconForTypeName(
            containerType.getName()));

        createContainerTypeSection();
        boolean containsSamples = false;
        if (containerType.getSampleTypeCollection() != null
            && containerType.getSampleTypeCollection().size() > 0) {
            createSampleTypesSection();
            containsSamples = true;
        }
        if (containerType.getChildContainerTypeCollection() != null
            && containerType.getChildContainerTypeCollection().size() > 0) {
            createChildContainerTypesSection();
        }
        if (!containsSamples) {
            createVisualizeContainer();
        }
        createButtons();
    }

    private void createContainerTypeSection() {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = createReadOnlyField(client, SWT.NONE, "Repository Site");
        nameLabel = createReadOnlyField(client, SWT.NONE, "Name");
        nameShortLabel = createReadOnlyField(client, SWT.NONE, "Short Name");
        isTopLevelButton = (Button) createWidget(client, Button.class,
            SWT.NONE, "Top Level Container");
        rowCapacityLabel = createReadOnlyField(client, SWT.NONE, "Maximum Rows");
        colCapacityLabel = createReadOnlyField(client, SWT.NONE,
            "Maximum Columns");
        defaultTempLabel = createReadOnlyField(client, SWT.NONE,
            "Default Temperature\n(Celcius)");
        numSchemeLabel = createReadOnlyField(client, SWT.NONE,
            "Child Labeling Scheme");
        activityStatusLabel = createReadOnlyField(client, SWT.NONE,
            "Activity Status");
        commentLabel = createReadOnlyField(client, SWT.NONE, "Comments");

        setContainerTypeValues();
    }

    private void setContainerTypeValues() {
        setTextValue(siteLabel, containerType.getSite().getName());
        setTextValue(nameLabel, containerType.getName());
        setTextValue(nameShortLabel, containerType.getNameShort());
        setCheckBoxValue(isTopLevelButton, containerType.getTopLevel());
        setTextValue(rowCapacityLabel, containerType.getRowCapacity());
        setTextValue(colCapacityLabel, containerType.getColCapacity());
        setTextValue(defaultTempLabel, containerType.getDefaultTemperature());
        setTextValue(numSchemeLabel,
            containerType.getChildLabelingScheme() == null ? "" : containerType
                .getChildLabelingSchemeName());
        setTextValue(activityStatusLabel, containerType.getActivityStatus());
        setTextValue(commentLabel, containerType.getComment());
    }

    private void createSampleTypesSection() {
        Composite client = createSectionWithClient("Contains Aliquots");
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label = toolkit.createLabel(client, "Sample types:");
        label
            .setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

        sampleTypesViewer = new ListViewer(client, SWT.BORDER | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        sampleTypesViewer.getList().setLayoutData(gd);
        sampleTypesViewer.setContentProvider(new ArrayContentProvider());
        sampleTypesViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                SampleTypeWrapper type = (SampleTypeWrapper) element;
                return type.getName() + " (" + type.getNameShort() + ")";
            }
        });
        setSampleTypesValues();
    }

    private void setSampleTypesValues() {
        if (sampleTypesViewer != null) {
            sampleTypesViewer.setInput(containerType.getSampleTypeCollection());
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

        childContainerTypesViewer = new ListViewer(client, SWT.BORDER
            | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        childContainerTypesViewer.getList().setLayoutData(gd);
        childContainerTypesViewer
            .setContentProvider(new ArrayContentProvider());
        childContainerTypesViewer
            .addDoubleClickListener(collectionDoubleClickListener);
        setChildContainerTypesValues();
    }

    protected void createVisualizeContainer() {
        Composite client = createSectionWithClient("Container Visual");
        ContainerDisplayFatory.createWidget(client, containerType);
    }

    private void setChildContainerTypesValues() {
        if (childContainerTypesViewer != null) {
            childContainerTypesViewer.setInput(containerType
                .getChildContainerTypeCollection());
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
        setSampleTypesValues();
        setChildContainerTypesValues();
    }

    @Override
    protected String getEntryFormId() {
        return ContainerTypeEntryForm.ID;
    }
}
