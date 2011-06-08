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
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;

public class ContainerTypeViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeViewForm"; //$NON-NLS-1$

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType;

    private BgcBaseText siteLabel;

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private Button isTopLevelButton;

    private BgcBaseText rowCapacityLabel;

    private BgcBaseText colCapacityLabel;

    private BgcBaseText defaultTempLabel;

    private BgcBaseText numSchemeLabel;

    private BgcBaseText activityStatusLabel;

    private BgcBaseText commentLabel;

    private ListViewer sampleTypesViewer;

    private ListViewer childContainerTypesViewer;

    public ContainerTypeViewForm() {
        super();
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof ContainerTypeAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        containerTypeAdapter = (ContainerTypeAdapter) adapter;
        containerType = containerTypeAdapter.getContainerType();
        retrieveContainerType();
        setPartName(Messages.getString("ContainerTypeViewForm.title", //$NON-NLS-1$
            containerType.getName()));
    }

    private void retrieveContainerType() throws Exception {
        containerType.reload();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ContainerTypeViewForm.title", //$NON-NLS-1$
            containerType.getName()));
        page.setLayout(new GridLayout(1, false));

        createContainerTypeSection();
        boolean containsSamples = false;
        if (containerType.getSpecimenTypeCollection() != null
            && containerType.getSpecimenTypeCollection().size() > 0) {
            createSpecimenTypesSection();
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
            Messages.getString("containerType.field.label.site")); //$NON-NLS-1$
        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.name")); //$NON-NLS-1$
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.nameShort")); //$NON-NLS-1$
        isTopLevelButton = (Button) createLabelledWidget(client, Button.class,
            SWT.NONE, Messages.getString("containerType.field.label.topLevel")); //$NON-NLS-1$
        rowCapacityLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("containerType.field.label.rows")); //$NON-NLS-1$
        colCapacityLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("containerType.field.label.cols")); //$NON-NLS-1$
        defaultTempLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("containerType.field.label.temperature")); //$NON-NLS-1$
        numSchemeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("containerType.field.label.scheme")); //$NON-NLS-1$
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.activity")); //$NON-NLS-1$
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments")); //$NON-NLS-1$

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
            containerType.getChildLabelingScheme() == null ? "" : containerType //$NON-NLS-1$
                .getChildLabelingSchemeName());
        setTextValue(activityStatusLabel, containerType.getActivityStatus());
        setTextValue(commentLabel, containerType.getComment());
    }

    private void createSpecimenTypesSection() {
        Composite client = createSectionWithClient(Messages
            .getString("ContainerTypeViewForm.specimens.title")); //$NON-NLS-1$
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label = toolkit.createLabel(client,
            Messages.getString("ContainerTypeViewForm.specimens.label")); //$NON-NLS-1$
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
                return type.getName() + " (" + type.getNameShort() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        });
        setSpecimenTypesValues();
    }

    private void setSpecimenTypesValues() {
        if (sampleTypesViewer != null) {
            sampleTypesViewer.setInput(containerType
                .getSpecimenTypeCollection());
        }
    }

    private void createChildContainerTypesSection() {
        Composite client = createSectionWithClient(Messages
            .getString("ContainerTypeViewForm.types.title")); //$NON-NLS-1$
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label = toolkit.createLabel(client,
            Messages.getString("ContainerTypeViewForm.types.label")); //$NON-NLS-1$
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
            .getString("ContainerTypeViewForm.visual")); //$NON-NLS-1$
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
        setPartName(Messages.getString("ContainerTypeViewForm.title", //$NON-NLS-1$
            containerType.getName()));
        form.setText(Messages.getString("ContainerTypeViewForm.title", //$NON-NLS-1$
            containerType.getName()));
        setContainerTypeValues();
        setSpecimenTypesValues();
        setChildContainerTypesValues();
    }

}
