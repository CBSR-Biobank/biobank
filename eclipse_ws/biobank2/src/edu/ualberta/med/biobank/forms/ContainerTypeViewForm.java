package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;

public class ContainerTypeViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeViewForm";

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType;

    private BiobankText siteLabel;

    private BiobankText nameLabel;

    private BiobankText nameShortLabel;

    private Button isTopLevelButton;

    private BiobankText rowCapacityLabel;

    private BiobankText colCapacityLabel;

    private BiobankText defaultTempLabel;

    private BiobankText numSchemeLabel;

    private BiobankText activityStatusLabel;

    private BiobankText commentLabel;

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
        setPartName(Messages.getString("ContainerTypeViewForm.title",
            containerType.getName()));
    }

    private void retrieveContainerType() throws Exception {
        containerType.reload();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ContainerTypeViewForm.title",
            containerType.getName()));
        page.setLayout(new GridLayout(1, false));

        createContainerTypeSection();
        boolean containsSamples = false;
        if (containerType.getSpecimenTypeCollection() != null
            && containerType.getSpecimenTypeCollection().size() > 0) {
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
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("containerType.field.label.site"));
        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.name"));
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.nameShort"));
        isTopLevelButton = (Button) createLabelledWidget(client, Button.class,
            SWT.NONE, Messages.getString("containerType.field.label.topLevel"));
        rowCapacityLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("containerType.field.label.rows"));
        colCapacityLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("containerType.field.label.cols"));
        defaultTempLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("containerType.field.label.temperature"));
        numSchemeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("containerType.field.label.scheme"));
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.activity"));
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments"));

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
        setTextValue(
            numSchemeLabel,
            containerType.getChildLabelingScheme() == null ? "" : containerType
                .getChildLabelingSchemeName());
        setTextValue(activityStatusLabel, containerType.getActivityStatus());
        setTextValue(commentLabel, containerType.getComment());
    }

    private void createSpecimenTypesSection() {
        Composite client = createSectionWithClient(Messages
            .getString("ContainerTypeViewForm.specimens.title"));
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label = toolkit.createLabel(client,
            Messages.getString("ContainerTypeViewForm.specimens.label"));
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
                SpecimenTypeWrapper type = (SpecimenTypeWrapper) element;
                return type.getName() + " (" + type.getNameShort() + ")";
            }
        });
        setSpecimenTypesValues();
    }

    private void setSpecimenTypesValues() {
        if (sampleTypesViewer != null) {
            sampleTypesViewer.setInput(containerType.getSpecimenTypeCollection());
        }
    }

    private void createChildContainerTypesSection() {
        Composite client = createSectionWithClient(Messages
            .getString("ContainerTypeViewForm.types.title"));
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label = toolkit.createLabel(client,
            Messages.getString("ContainerTypeViewForm.types.label"));
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
        Composite containerSection = createSectionWithClient(Messages
            .getString("ContainerTypeViewForm.visual"));
        containerSection.setLayout(new FillLayout());
        ScrolledComposite sc = new ScrolledComposite(containerSection,
            SWT.H_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        Composite client = new Composite(sc, SWT.NONE);
        client.setLayout(new GridLayout(1, false));
        ContainerDisplayWidget containerDisplay = new ContainerDisplayWidget(
            client);
        containerDisplay.setContainerType(containerType);
        toolkit.adapt(containerSection);
        toolkit.adapt(sc);
        toolkit.adapt(client);
        toolkit.adapt(containerDisplay);
        sc.setContent(client);
        sc.setMinSize(client.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void setChildContainerTypesValues() {
        if (childContainerTypesViewer != null) {
            childContainerTypesViewer.setInput(containerType
                .getChildContainerTypeCollection());
        }
    }

    private void createButtons() {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(4, false));
        toolkit.paintBordersFor(client);
    }

    @Override
    public void reload() throws Exception {
        retrieveContainerType();
        setPartName(Messages.getString("ContainerTypeViewForm.title",
            containerType.getName()));
        form.setText(Messages.getString("ContainerTypeViewForm.title",
            containerType.getName()));
        setContainerTypeValues();
        setSpecimenTypesValues();
        setChildContainerTypesValues();
    }

}
