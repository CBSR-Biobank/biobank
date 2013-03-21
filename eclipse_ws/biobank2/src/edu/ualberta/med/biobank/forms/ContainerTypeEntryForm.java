package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.common.action.info.SiteContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetContainerTypeInfoAction;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.model.type.LabelingLayout;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeEntryForm extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory.getI18n(ContainerTypeEntryForm.class);

    @SuppressWarnings("unused")
    private static BgcLogger logger = BgcLogger.getLogger(ContainerTypeEntryForm.class.getName());

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ContainerTypeEntryForm";

    @SuppressWarnings("nls")
    // title area message
    private static final String MSG_NEW_STORAGE_TYPE_OK =
        i18n.tr("Creating a new storage type.");

    @SuppressWarnings("nls")
    // title area message
    private static final String MSG_STORAGE_TYPE_OK =
        i18n.tr("Editing an existing storage type.");

    private ContainerTypeAdapter containerTypeAdapter;

    private final ContainerTypeWrapper containerType =
        new ContainerTypeWrapper(
            SessionManager.getAppService());

    private MultiSelectWidget<SpecimenTypeWrapper> specimensMultiSelect;

    private MultiSelectWidget<ContainerTypeWrapper> childContainerTypesMultiSelect;

    private List<SpecimenTypeWrapper> allSpecimenTypes;

    private List<ContainerTypeWrapper> availSubContainerTypes;

    private final BgcEntryFormWidgetListener multiSelectListener;

    private ComboViewer labelingSchemeComboViewer;

    private Map<Integer, String> labelingSchemeMap;

    private ComboViewer activityStatusComboViewer;

    protected boolean hasSpecimens = false;

    private Button hasContainersRadio;

    private Button hasSpecimensRadio;

    private CommentsInfoTable commentEntryTable;

    private ContainerTypeInfo containerTypeInfo;

    private final CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private LabelingLayoutWidget labelingLayoutWidget;

    public ContainerTypeEntryForm() {
        super();
        multiSelectListener = new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };
    }

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ContainerTypeAdapter),
            "Invalid editor input: object of type " + adapter.getClass().getName());

        containerTypeAdapter = (ContainerTypeAdapter) adapter;
        updateContainerTypeInfo(adapter.getId());

        String tabName;
        if (containerType.isNew()) {
            // tab name
            tabName = i18n.tr("New Container Type");
            containerType.setActivityStatus(ActivityStatus.ACTIVE);
        } else {
            // tab name
            tabName = i18n.tr("Container Type {0} ", containerType.getName());
        }
        setPartName(tabName);
    }

    private void updateContainerTypeInfo(Integer id)
        throws ApplicationException {
        if (id != null) {
            containerTypeInfo = SessionManager.getAppService().doAction(
                new ContainerTypeGetInfoAction(id));
            containerType.setWrappedObject(containerTypeInfo.getContainerType());
        } else {
            containerTypeInfo = new ContainerTypeInfo();
            containerType.setWrappedObject((ContainerType)
                containerTypeAdapter.getModelObject().getWrappedObject());
        }

        comment.setWrappedObject(new Comment());
        ((AdapterBase) adapter).setModelObject(containerType);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(
            // form title
            i18n.tr("Container Type Information"));
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        createContainerTypeSection();
        createCommentSection();
        createContainsSection();
    }

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client = createSectionWithClient(Comment.NAME.format(2).toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentsInfoTable(client, containerType.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            // label.
            i18n.tr("Add a comment"), null, comment, "message", null);

    }

    @SuppressWarnings("nls")
    protected void createContainerTypeSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        adapter.setParent(((SiteAdapter) SessionManager.searchFirstNode(
            SiteWrapper.class, containerType.getSite().getId()))
            .getContainerTypesGroupNode());

        availSubContainerTypes = new ArrayList<ContainerTypeWrapper>();
        List<SiteContainerTypeInfo> containerTypeInfos =
            SessionManager
                .getAppService()
                .doAction(
                    new SiteGetContainerTypeInfoAction(containerType.getSite()
                        .getId())).getList();

        for (SiteContainerTypeInfo info : containerTypeInfos) {
            if (!info.getContainerType().getTopLevel()
                && !info.getContainerType().equals(
                    containerType.getWrappedObject())) {
                availSubContainerTypes.add(new ContainerTypeWrapper(
                    SessionManager
                        .getAppService(), info.getContainerType()));
            }
        }

        BgcBaseText name = (BgcBaseText) createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE,
            HasName.PropertyName.NAME.toString(),
            null, containerType,
            ContainerTypePeer.NAME.getName(), new NonEmptyStringValidator(
                // validation error message
                i18n.tr("Container type must have a name")));

        setFirstControl(name);

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            HasNameShort.PropertyName.NAME_SHORT.toString(),
            null, containerType,
            ContainerTypePeer.NAME_SHORT.getName(),
            new NonEmptyStringValidator(
                // validation error message
                i18n.tr("Container type must have a short name")));

        if (containerType.getTopLevel() == null) {
            containerType.setTopLevel(false);
        }
        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            // label
            i18n.tr("Top Level Container"),
            null, containerType,
            ContainerTypePeer.TOP_LEVEL.getName(), null);
        toolkit.paintBordersFor(client);

        BgcBaseText rowText = (BgcBaseText) createBoundWidgetWithLabel(
            client, BgcBaseText.class, SWT.NONE,
            // label
            i18n.tr("Rows"), null, containerType,
            CapacityPeer.ROW_CAPACITY.getName(), new IntegerNumberValidator(
                // validation error message
                i18n.tr("Row capacity is not a valid number"), false));
        rowText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // containerType.setRowCapacity(capacity);
                updateLabelingLayoutWidget();
            }
        });

        BgcBaseText colText = (BgcBaseText) createBoundWidgetWithLabel(
            client, BgcBaseText.class, SWT.NONE,
            // label
            i18n.tr("Columns"),
            null, containerType,
            CapacityPeer.COL_CAPACITY.getName(), new IntegerNumberValidator(
                // validation error message
                i18n.tr("Column capacity is not a valid number"), false));
        colText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateLabelingLayoutWidget();
            }
        });

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            // label
            i18n.tr("Default Temperature\n(Celcius)"),
            null,
            containerType, ContainerTypePeer.DEFAULT_TEMPERATURE.getName(),
            new DoubleNumberValidator(
                // validation error message
                i18n.tr("Default temperature is not a valid number")));

        String currentScheme = containerType.getChildLabelingSchemeName();
        labelingSchemeMap = new HashMap<Integer, String>();
        for (ContainerLabelingSchemeWrapper scheme : ContainerLabelingSchemeWrapper
            .getAllLabelingSchemesMap(SessionManager.getAppService()).values()) {
            labelingSchemeMap.put(scheme.getId(), scheme.getName());
        }

        labelingSchemeComboViewer = createComboViewer(client,
            ContainerType.Property.CHILD_LABELING_SCHEME.toString(),
            labelingSchemeMap.values(), currentScheme,
            // validation error message
            i18n.tr("Select a child labeling scheme"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    try {
                        containerType.setChildLabelingSchemeName((String) selectedObject);
                        updateLabelingLayoutWidget();
                    } catch (Exception e) {
                        BgcPlugin.openAsyncError(
                            // dialog title
                            i18n.tr("Error setting the labeling scheme"),
                            e);
                    }
                }
            });

        labelingLayoutWidget = new LabelingLayoutWidget(client, widgetCreator, containerType);
        labelingLayoutWidget.setVisible(true);

        activityStatusComboViewer = createComboViewer(client, ActivityStatus.NAME.format(1).toString(),
            ActivityStatus.valuesList(), containerType.getActivityStatus(),
            // validation error message
            i18n.tr("Container type must have an activity status"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    containerType
                        .setActivityStatus((ActivityStatus) selectedObject);
                }
            });

    }

    private void updateLabelingLayoutWidget() {
        labelingLayoutWidget.setVisible(
            containerType.getWrappedObject().hasMultipleLabelingLayout());
        page.layout(true, true);
        book.reflow(true);
    }

    @SuppressWarnings("nls")
    private void createContainsSection() throws Exception {
        Composite client = createSectionWithClient(i18n.tr("Contents"));
        hasContainersRadio = toolkit.createButton(client,
            // radio button label
            i18n.tr("Contains Containers"),
            SWT.RADIO);
        hasSpecimensRadio = toolkit.createButton(client,
            // radio button label
            i18n.tr("Contains Specimens"),
            SWT.RADIO);
        hasContainersRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hasSpecimens = !hasContainersRadio.getSelection();
                if (hasContainersRadio.getSelection()) {
                    showSpecimens(false);
                    setDirty(true);
                }
            }
        });
        hasSpecimensRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hasSpecimens = hasSpecimensRadio.getSelection();
                if (hasSpecimensRadio.getSelection()) {
                    showSpecimens(true);
                    setDirty(true);
                }
            }
        });

        createChildContainerTypesSection(client);
        createSpecimenTypesSection(client);
        showContainersOrSpecimens();
    }

    protected void showSpecimens(boolean show) {
        specimensMultiSelect.setVisible(show);
        ((GridData) specimensMultiSelect.getLayoutData()).exclude = !show;
        childContainerTypesMultiSelect.setVisible(!show);
        ((GridData) childContainerTypesMultiSelect.getLayoutData()).exclude =
            show;
        form.layout(true, true);
    }

    @SuppressWarnings("nls")
    private void createSpecimenTypesSection(Composite parent) throws Exception {
        allSpecimenTypes = SpecimenTypeWrapper.getAllSpecimenTypes(
            SessionManager.getAppService(), true);

        specimensMultiSelect = new MultiSelectWidget<SpecimenTypeWrapper>(parent, SWT.NONE,
            // label
            i18n.tr("Available specimen types"),
            // label
            i18n.tr("Selected specimen types"),
            100) {
            @Override
            protected String getTextForObject(SpecimenTypeWrapper nodeObject) {
                return nodeObject.getName();
            }
        };
        specimensMultiSelect.adaptToToolkit(toolkit, true);
        specimensMultiSelect.addSelectionChangedListener(multiSelectListener);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        specimensMultiSelect.setLayoutData(gd);
        setSpecimenTypesSelection();
    }

    private void setSpecimenTypesSelection() {
        specimensMultiSelect.setSelections(allSpecimenTypes,
            containerType.getSpecimenTypeCollection());
    }

    @SuppressWarnings("nls")
    private void createChildContainerTypesSection(Composite parent) {
        childContainerTypesMultiSelect = new MultiSelectWidget<ContainerTypeWrapper>(
            parent,
            SWT.NONE,
            // label
            i18n.tr("Available Sub-Container Types"),
            // label
            i18n.tr("Selected Sub-Container types"),
            100) {
            @Override
            protected String getTextForObject(
                ContainerTypeWrapper nodeObject) {
                return nodeObject.getName();
            }
        };
        childContainerTypesMultiSelect.adaptToToolkit(toolkit, true);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        childContainerTypesMultiSelect.setLayoutData(gd);
        childContainerTypesMultiSelect.addSelectionChangedListener(multiSelectListener);

        setChildContainerTypeSelection();
    }

    private void setChildContainerTypeSelection() {
        childContainerTypesMultiSelect.setSelections(availSubContainerTypes,
            containerType.getChildContainerTypeCollection());
    }

    @Override
    protected String getOkMessage() {
        if (containerType.getId() == null) {
            return MSG_NEW_STORAGE_TYPE_OK;
        }
        return MSG_STORAGE_TYPE_OK;
    }

    /**
     * Called by base class when form data is to be saved.
     */
    @Override
    protected void saveForm() throws Exception {

        ContainerTypeSaveAction ctSaveAction = new ContainerTypeSaveAction();
        ctSaveAction.setId(containerType.getId());
        ctSaveAction.setName(containerType.getName());
        ctSaveAction.setNameShort(containerType.getNameShort());
        ctSaveAction.setSiteId(containerType.getSite().getId());
        ctSaveAction.setTopLevel(containerType.getTopLevel());
        ctSaveAction.setRowCapacity(containerType.getRowCapacity());
        ctSaveAction.setColCapacity(containerType.getColCapacity());
        ctSaveAction.setActivityStatus(ActivityStatus.ACTIVE);
        ctSaveAction.setDefaultTemperature(containerType.getDefaultTemperature());
        ctSaveAction.setChildLabelingSchemeId(containerType.getChildLabelingSchemeId());
        ctSaveAction.setLabelingLayout(containerType.getLabelingLayout());
        ctSaveAction.setCommentMessage(comment.getMessage());

        ctSaveAction.setSpecimenTypeIds(getSpecimenTypeIds());
        ctSaveAction.setChildContainerTypeIds(getChildContainerTypeIds());

        Integer id = SessionManager.getAppService().doAction(ctSaveAction).getId();
        updateContainerTypeInfo(id);
    }

    private HashSet<Integer> getSpecimenTypeIds() {
        if (hasSpecimens) {
            List<SpecimenTypeWrapper> addedSpcTypes = specimensMultiSelect.getAddedToSelection();
            List<SpecimenTypeWrapper> removedSpcTypes = specimensMultiSelect.getRemovedFromSelection();
            containerType.addToSpecimenTypeCollection(addedSpcTypes);
            containerType.removeFromSpecimenTypeCollection(removedSpcTypes);
        } else {
            containerType.removeFromSpecimenTypeCollection(containerType.getSpecimenTypeCollection());
        }

        HashSet<Integer> result = new HashSet<Integer>();
        for (SpecimenTypeWrapper spcType : containerType.getSpecimenTypeCollection()) {
            result.add(spcType.getId());
        }
        return result;
    }

    private HashSet<Integer> getChildContainerTypeIds() {
        if (!hasSpecimens) {
            List<ContainerTypeWrapper> addedTypes =
                childContainerTypesMultiSelect.getAddedToSelection();
            List<ContainerTypeWrapper> removedTypes =
                childContainerTypesMultiSelect.getRemovedFromSelection();
            containerType.addToChildContainerTypeCollection(addedTypes);
            containerType.removeFromChildContainerTypeCollection(removedTypes);
        } else {
            containerType.removeFromChildContainerTypeCollection(containerType
                .getChildContainerTypeCollection());
        }

        HashSet<Integer> result = new HashSet<Integer>();
        for (ContainerTypeWrapper childContainerType : containerType.getChildContainerTypeCollection()) {
            result.add(childContainerType.getId());
        }
        return result;
    }

    @Override
    public String getNextOpenedFormId() {
        return ContainerTypeViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        SiteWrapper site = containerType.getSite();
        containerType.reset();
        containerType.setSite(site);

        if (containerType.isNew()) {
            containerType.setActivityStatus(ActivityStatus.ACTIVE);
        }

        setChildContainerTypeSelection();
        setSpecimenTypesSelection();
        showContainersOrSpecimens();

        GuiUtil.reset(labelingSchemeComboViewer,
            containerType.getChildLabelingSchemeName());
        GuiUtil.reset(activityStatusComboViewer,
            containerType.getActivityStatus());

        commentEntryTable.setList(containerType.getCommentCollection(false));
    }

    private void showContainersOrSpecimens() {
        hasSpecimens = (containerType.getSpecimenTypeCollection() != null)
            && !containerType.getSpecimenTypeCollection().isEmpty();
        showSpecimens(hasSpecimens);
        hasSpecimensRadio.setSelection(hasSpecimens);
        hasContainersRadio.setSelection(!hasSpecimens);
    }

    private static class LabelingLayoutWidget {

        private final BgcWidgetCreator widgetCreator;

        private final Label label;

        private final ComboViewer comboViewer;

        @SuppressWarnings("nls")
        private static final String LABELING_LAYOUT_BINDING = "labelingLayout-binding";

        @SuppressWarnings("nls")
        public LabelingLayoutWidget(Composite composite, BgcWidgetCreator widgetCreator,
            final ContainerTypeWrapper containerType) {
            this.widgetCreator = widgetCreator;
            label = widgetCreator.createLabel(composite, ContainerType.Property.LABELING_LAYOUT.getString());

            LabelingLayout labelingLayout = containerType.getLabelingLayout();

            comboViewer = widgetCreator.createComboViewer(composite, label, LabelingLayout.valuesList(),
                labelingLayout,
                // validation error message
                i18n.tr("Select a labeling layout"),
                true,
                LABELING_LAYOUT_BINDING,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        LabelingLayout layout = (LabelingLayout) selectedObject;
                        containerType.setLabelingLayout(layout);
                    }
                },
                new BiobankLabelProvider() {
                    @Override
                    public String getText(Object element) {
                        LabelingLayout layout = (LabelingLayout) element;
                        return layout.getLabel();
                    }
                });
        }

        public void setVisible(boolean visible) {
            widgetCreator.showWidget(label, visible);
            widgetCreator.showWidget(comboViewer.getCombo(), visible);
            if (visible) {
                widgetCreator.addBinding(LABELING_LAYOUT_BINDING);
            } else {
                widgetCreator.removeBinding(LABELING_LAYOUT_BINDING);
            }
        }
    }
}
