package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
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
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeEntryForm extends BiobankEntryForm {

    @SuppressWarnings("unused")
    private static BgcLogger logger = BgcLogger
        .getLogger(ContainerTypeEntryForm.class.getName());

    public static final String ID =
        "edu.ualberta.med.biobank.forms.ContainerTypeEntryForm"; 

    private static final String MSG_NEW_STORAGE_TYPE_OK =
        "Creating a new storage type.";

    private static final String MSG_STORAGE_TYPE_OK =
        "Editing an existing storage type.";

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType = new ContainerTypeWrapper(
        SessionManager.getAppService());

    private MultiSelectWidget<SpecimenTypeWrapper> specimensMultiSelect;

    private MultiSelectWidget<ContainerTypeWrapper> childContainerTypesMultiSelect;

    private List<SpecimenTypeWrapper> allSpecimenTypes;

    private List<ContainerTypeWrapper> availSubContainerTypes;

    private BgcEntryFormWidgetListener multiSelectListener;

    private ComboViewer labelingSchemeComboViewer;

    private Map<Integer, String> labelingSchemeMap;

    private ComboViewer activityStatusComboViewer;

    protected boolean hasSpecimens = false;

    private Button hasContainersRadio;

    private Button hasSpecimensRadio;

    private CommentsInfoTable commentEntryTable;

    private ContainerTypeInfo containerTypeInfo;

    private CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    public ContainerTypeEntryForm() {
        super();
        multiSelectListener = new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ContainerTypeAdapter),
            "Invalid editor input: object of type " 
                + adapter.getClass().getName());

        containerTypeAdapter = (ContainerTypeAdapter) adapter;
        updateContainerTypeInfo(adapter.getId());

        String tabName;
        if (containerType.isNew()) {
            tabName = "New Container Type";
            containerType.setActivityStatus(ActivityStatus.ACTIVE);
        } else {
            tabName =
                NLS.bind("Container Type {0} ",
                    containerType.getName());
        }
        setPartName(tabName);
    }

    private void updateContainerTypeInfo(Integer id)
        throws ApplicationException {
        if (id != null) {
            containerTypeInfo =
                SessionManager.getAppService().doAction(
                    new ContainerTypeGetInfoAction(id));
            containerType
                .setWrappedObject(containerTypeInfo.getContainerType());
        } else {
            containerTypeInfo = new ContainerTypeInfo();
            containerType.setWrappedObject((ContainerType) containerTypeAdapter
                .getModelObject().getWrappedObject());
        }

        comment.setWrappedObject(new Comment());
        ((AdapterBase) adapter).setModelObject(containerType);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container Type Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        createContainerTypeSection();
        createCommentSection();
        createContainsSection();
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient("Comments");
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client,
                containerType.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            "Add a comment", null, comment, "message", null);

    }

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
        for (ContainerTypeWrapper type : containerType.getSite()
            .getContainerTypeCollection()) {
            if (type.getTopLevel().equals(Boolean.FALSE)
                && !type.equals(containerType)) {
                availSubContainerTypes.add(type);
            }
        }

        BgcBaseText name =
            (BgcBaseText) createBoundWidgetWithLabel(client, BgcBaseText.class,
                SWT.NONE, "Name", null, containerType,
                ContainerTypePeer.NAME.getName(), new NonEmptyStringValidator(
                    "Container type must have a name"));

        setFirstControl(name);

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            "Name Short", null, containerType,
            ContainerTypePeer.NAME_SHORT.getName(),
            new NonEmptyStringValidator(
                "Container type must have a short name"));

        if (containerType.getTopLevel() == null) {
            containerType.setTopLevel(false);
        }
        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            "Top Level Container", null, containerType,
            ContainerTypePeer.TOP_LEVEL.getName(), null);
        toolkit.paintBordersFor(client);

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            "Rows", null, containerType,
            CapacityPeer.ROW_CAPACITY.getName(), new IntegerNumberValidator(
                "Row capacity is not a valid number", false));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            "Columns", null, containerType,
            CapacityPeer.COL_CAPACITY.getName(), new IntegerNumberValidator(
                "Column capacity is not a valid number", false));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            "Default Temperature\n(Celcius)", null,
            containerType, ContainerTypePeer.DEFAULT_TEMPERATURE.getName(),
            new DoubleNumberValidator(
                "Default temperature is not a valid number"));

        String currentScheme = containerType.getChildLabelingSchemeName();
        labelingSchemeMap = new HashMap<Integer, String>();
        for (ContainerLabelingSchemeWrapper scheme : ContainerLabelingSchemeWrapper
            .getAllLabelingSchemesMap(SessionManager.getAppService()).values()) {
            labelingSchemeMap.put(scheme.getId(), scheme.getName());
        }
        labelingSchemeComboViewer =
            createComboViewer(client,
                "Child Labeling Scheme",
                labelingSchemeMap.values(), currentScheme,
                "Select a child labeling scheme",
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        try {
                            containerType
                                .setChildLabelingSchemeName((String) selectedObject);
                        } catch (Exception e) {
                            BgcPlugin
                                .openAsyncError(
                                    "Error setting the labeling scheme",
                                    e);
                        }
                    }
                });

        activityStatusComboViewer =
            createComboViewer(client, "Activity status",
                ActivityStatus.valuesList(), containerType.getActivityStatus(),
                "Container type must have an activity status",
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        containerType
                            .setActivityStatus((ActivityStatus) selectedObject);
                    }
                });

    }

    private void createContainsSection() throws Exception {
        Composite client =
            createSectionWithClient("Contents");
        hasContainersRadio =
            toolkit.createButton(client,
                "Contains Containers",
                SWT.RADIO);
        hasSpecimensRadio =
            toolkit.createButton(client,
                "Contains specimens",
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

    private void createSpecimenTypesSection(Composite parent) throws Exception {
        allSpecimenTypes =
            SpecimenTypeWrapper.getAllSpecimenTypes(
                SessionManager.getAppService(), true);

        specimensMultiSelect =
            new MultiSelectWidget<SpecimenTypeWrapper>(parent, SWT.NONE,
                "Available specimen types",
                "Selected specimen types", 100) {
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

    private void createChildContainerTypesSection(Composite parent) {
        childContainerTypesMultiSelect =
            new MultiSelectWidget<ContainerTypeWrapper>(
                parent,
                SWT.NONE,
                "Available Sub-Container Types",
                "Selected Sub-Container types",
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

        childContainerTypesMultiSelect
            .addSelectionChangedListener(multiSelectListener);

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
        ctSaveAction.setDefaultTemperature(containerType
            .getDefaultTemperature());
        ctSaveAction.setChildLabelingSchemeId(containerType
            .getChildLabelingSchemeId());
        ctSaveAction.setCommentMessage(comment.getMessage());

        ctSaveAction.setSpecimenTypeIds(getSpecimenTypeIds());
        ctSaveAction.setChildContainerTypeIds(getChildContainerTypeIds());

        Integer id =
            SessionManager.getAppService().doAction(ctSaveAction).getId();
        updateContainerTypeInfo(id);
    }

    private HashSet<Integer> getSpecimenTypeIds() {
        if (hasSpecimens) {
            List<SpecimenTypeWrapper> addedSpcTypes =
                specimensMultiSelect.getAddedToSelection();
            List<SpecimenTypeWrapper> removedSpcTypes =
                specimensMultiSelect.getRemovedFromSelection();
            containerType.addToSpecimenTypeCollection(addedSpcTypes);
            containerType.removeFromSpecimenTypeCollection(removedSpcTypes);
        } else {
            containerType.removeFromSpecimenTypeCollection(containerType
                .getSpecimenTypeCollection());
        }

        HashSet<Integer> result = new HashSet<Integer>();
        for (SpecimenTypeWrapper spcType : containerType
            .getSpecimenTypeCollection()) {
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
        for (ContainerTypeWrapper childContainerType : containerType
            .getChildContainerTypeCollection()) {
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
        hasSpecimens =
            containerType.getSpecimenTypeCollection() != null
                && containerType.getSpecimenTypeCollection().size() > 0;
        showSpecimens(hasSpecimens);
        hasSpecimensRadio.setSelection(hasSpecimens);
        hasContainersRadio.setSelection(!hasSpecimens);
    }
}
