package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;

public class ContainerTypeViewForm extends BiobankViewForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ContainerTypeViewForm"; //$NON-NLS-1$

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

    private ListViewer sampleTypesViewer;

    private ListViewer childContainerTypesViewer;

    private CommentCollectionInfoTable commentTable;

    public ContainerTypeViewForm() {
        super();
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof ContainerTypeAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        containerType = (ContainerTypeWrapper) getModelObject();
        setPartName(NLS.bind(Messages.ContainerTypeViewForm_title,
            containerType.getName()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.ContainerTypeViewForm_title,
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

        siteLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.containerType_field_label_site);
        nameLabel =
            createReadOnlyLabelledField(client, SWT.NONE, Messages.label_name);
        nameShortLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_nameShort);
        isTopLevelButton =
            (Button) createLabelledWidget(client, Button.class, SWT.NONE,
                Messages.containerType_field_label_topLevel);
        rowCapacityLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.containerType_field_label_rows);
        colCapacityLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.containerType_field_label_cols);
        defaultTempLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.containerType_field_label_temperature);
        numSchemeLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.containerType_field_label_scheme);
        activityStatusLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_activity);

        createCommentsSection();

        setContainerTypeValues();
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable =
            new CommentCollectionInfoTable(client,
                containerType.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
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
            containerType.getChildLabelingScheme() == null ? "" : containerType //$NON-NLS-1$
                .getChildLabelingSchemeName());
        setTextValue(activityStatusLabel, containerType.getActivityStatus());
    }

    private void createSpecimenTypesSection() {
        Composite client =
            createSectionWithClient(Messages.ContainerTypeViewForm_specimens_title);
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label =
            toolkit.createLabel(client,
                Messages.ContainerTypeViewForm_specimens_label);
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
        Composite client =
            createSectionWithClient(Messages.ContainerTypeViewForm_types_title);
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label =
            toolkit.createLabel(client,
                Messages.ContainerTypeViewForm_types_label);
        label
            .setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

        childContainerTypesViewer =
            new ListViewer(client, SWT.BORDER | SWT.V_SCROLL);
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
        Composite containerSection =
            createSectionWithClient(Messages.ContainerTypeViewForm_visual);
        containerSection.setLayout(new FillLayout());
        ScrolledComposite sc =
            new ScrolledComposite(containerSection, SWT.H_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        Composite client = new Composite(sc, SWT.NONE);
        client.setLayout(new GridLayout(1, false));
        ContainerDisplayWidget containerDisplay =
            new ContainerDisplayWidget(client);
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
        containerType.reload();
        setPartName(NLS.bind(Messages.ContainerTypeViewForm_title,
            containerType.getName()));
        form.setText(NLS.bind(Messages.ContainerTypeViewForm_title,
            containerType.getName()));
        setContainerTypeValues();
        setSpecimenTypesValues();
        setChildContainerTypesValues();
        commentTable.setList(containerType.getCommentCollection(false));
    }

}
