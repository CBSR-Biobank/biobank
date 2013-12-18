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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeViewForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory.getI18n(ContainerTypeViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeViewForm";

    private final ContainerTypeWrapper containerType = new ContainerTypeWrapper(
        SessionManager.getAppService());

    private BgcBaseText siteLabel;

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private Button isTopLevelButton;

    private Button isMicroplateButton;

    private BgcBaseText rowCapacityLabel;

    private BgcBaseText colCapacityLabel;

    private BgcBaseText defaultTempLabel;

    private BgcBaseText labelingScheme;

    private BgcBaseText labelingLayout = null;

    private BgcBaseText activityStatusLabel;

    private ListViewer sampleTypesViewer;

    private ListViewer childContainerTypesViewer;

    private CommentsInfoTable commentTable;

    private ContainerTypeInfo containerTypeInfo;

    public ContainerTypeViewForm() {
        super();
    }

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof ContainerTypeAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        updateContainerTypeInfo();
        setPartName(
        // tab name
        i18n.tr("Container Type {0}", containerType.getName()));
    }

    private void updateContainerTypeInfo() throws ApplicationException {
        containerTypeInfo = SessionManager.getAppService().doAction(
            new ContainerTypeGetInfoAction(adapter.getId()));
        Assert.isNotNull(containerTypeInfo);
        containerType.setWrappedObject(containerTypeInfo.getContainerType());
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(
            // form title
            i18n.tr("Container Type {0}", containerType.getName()));
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

    @SuppressWarnings("nls")
    private void createContainerTypeSection() {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Site.NAME.singular().toString());
        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            HasName.PropertyName.NAME.toString());
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            HasNameShort.PropertyName.NAME_SHORT.toString());
        isTopLevelButton = (Button) createLabelledWidget(client, Button.class, SWT.NONE,
            ContainerType.Property.TOP_LEVEL.toString());
        isMicroplateButton = (Button) createLabelledWidget(client, Button.class, SWT.NONE,
            ContainerType.Property.IS_MICROPLATE.toString());
        rowCapacityLabel = createReadOnlyLabelledField(client, SWT.NONE,
            // label
            i18n.tr("Rows"));
        colCapacityLabel = createReadOnlyLabelledField(client, SWT.NONE,
            // label
            i18n.tr("Columns"));
        defaultTempLabel = createReadOnlyLabelledField(client, SWT.NONE,
            // label
            i18n.tr("Default temperature\n(Celcius)"));
        labelingScheme = createReadOnlyLabelledField(client, SWT.NONE,
            // label
            i18n.tr("Child labeling scheme"));

        if (containerType.getWrappedObject().hasMultipleLabelingLayout()) {
            labelingLayout = createReadOnlyLabelledField(client, SWT.NONE,
                // label
                ContainerType.Property.LABELING_LAYOUT.getString());
        }
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            ActivityStatus.NAME.format(1).toString());

        createCommentsSection();
        setContainerTypeValues();
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Comment.NAME.format(2).toString());
        commentTable = new CommentsInfoTable(client, containerType.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void setContainerTypeValues() {
        setTextValue(siteLabel, containerType.getSite().getName());
        setTextValue(nameLabel, containerType.getName());
        setTextValue(nameShortLabel, containerType.getNameShort());
        setCheckBoxValue(isTopLevelButton, containerType.getTopLevel());
        setCheckBoxValue(isMicroplateButton, containerType.getIsMicroplate());
        setTextValue(rowCapacityLabel, containerType.getRowCapacity());
        setTextValue(colCapacityLabel, containerType.getColCapacity());
        setTextValue(defaultTempLabel, containerType.getDefaultTemperature());
        setTextValue(labelingScheme,
            containerType.getChildLabelingScheme() == null
                ? StringUtil.EMPTY_STRING : containerType.getChildLabelingSchemeName());
        if (labelingLayout != null) {
            setTextValue(labelingLayout, containerType.getLabelingLayout().getLabel());
        }
        setTextValue(activityStatusLabel, containerType.getActivityStatus());
    }

    @SuppressWarnings("nls")
    private void createSpecimenTypesSection() {
        Composite client =
            createSectionWithClient( // label
            i18n.tr("Contains specimens"));
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label =
            toolkit.createLabel(client,
                // label
                i18n.tr("Specimens types:"));
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
            sampleTypesViewer.setInput(containerType
                .getSpecimenTypeCollection());
        }
    }

    @SuppressWarnings("nls")
    private void createChildContainerTypesSection() {
        Composite client =
            createSectionWithClient(
            // label
            i18n.tr("Contains Container Types"));
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client);

        Label label = toolkit.createLabel(client,
            // label
            i18n.tr("Container types:"));
        label
            .setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

        childContainerTypesViewer =
            new ListViewer(client, SWT.BORDER | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        childContainerTypesViewer.getList().setLayoutData(gd);
        childContainerTypesViewer
            .setContentProvider(new ArrayContentProvider());
        setChildContainerTypesValues();
    }

    @SuppressWarnings("nls")
    protected void createVisualizeContainer() {
        Composite containerSection = createSectionWithClient(
            // label
            i18n.tr("Container Visual"));
        containerSection.setLayout(new FillLayout());
        ScrolledComposite sc = new ScrolledComposite(containerSection, SWT.H_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        Composite client = new Composite(sc, SWT.NONE);
        client.setLayout(new GridLayout(1, false));
        ContainerDisplayWidget containerDisplay =
            new ContainerDisplayWidget(client, ContainerTypeViewForm.class.getSimpleName());
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

    @SuppressWarnings("nls")
    @Override
    public void setValues() throws Exception {
        // part name.
        setPartName(i18n.tr("Container Type {0}",
            containerType.getName()));
        // form title.
        form.setText(i18n.tr("Container Type {0}",
            containerType.getName()));
        setContainerTypeValues();
        setSpecimenTypesValues();
        setChildContainerTypesValues();
        commentTable.setList(containerType.getCommentCollection(false));
    }

}
