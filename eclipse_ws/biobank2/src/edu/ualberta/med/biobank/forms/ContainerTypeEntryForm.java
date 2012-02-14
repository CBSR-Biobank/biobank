package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashMap;
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
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
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
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeEntryForm extends BiobankEntryForm {

    @SuppressWarnings("unused")
    private static BgcLogger logger = BgcLogger
        .getLogger(ContainerTypeEntryForm.class.getName());

    public static final String ID =
        "edu.ualberta.med.biobank.forms.ContainerTypeEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_STORAGE_TYPE_OK =
        Messages.ContainerTypeEntryForm_creation_msg;

    private static final String MSG_STORAGE_TYPE_OK =
        Messages.ContainerTypeEntryForm_edition_msg;

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType;

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

    private CommentCollectionInfoTable commentEntryTable;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

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
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        containerTypeAdapter = (ContainerTypeAdapter) adapter;
        containerType = (ContainerTypeWrapper) getModelObject();

        String tabName;
        if (containerType.isNew()) {
            tabName = Messages.ContainerTypeEntryForm_new_title;
            containerType.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(SessionManager.getAppService()));
        } else {
            tabName = NLS.bind(Messages.ContainerTypeEntryForm_edit_title,
                containerType.getName());
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.ContainerTypeEntryForm_main_title);
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        createContainerTypeSection();
        createCommentSection();
        createContainsSection();
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentCollectionInfoTable(client,
            containerType.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createLabelledWidget(client, BgcBaseText.class, SWT.MULTI,
            Messages.Comments_add);

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

        BgcBaseText name = (BgcBaseText) createBoundWidgetWithLabel(client,
            BgcBaseText.class, SWT.NONE, Messages.label_name, null,
            containerType, ContainerTypePeer.NAME.getName(),
            new NonEmptyStringValidator(
                Messages.ContainerTypeEntryForm_name_validation_msg));

        setFirstControl(name);

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.label_nameShort, null, containerType,
            ContainerTypePeer.NAME_SHORT.getName(),
            new NonEmptyStringValidator(
                Messages.ContainerTypeEntryForm_nameShort_validation_msg));

        if (containerType.getTopLevel() == null) {
            containerType.setTopLevel(false);
        }
        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            Messages.containerType_field_label_topLevel, null, containerType,
            ContainerTypePeer.TOP_LEVEL.getName(), null);
        toolkit.paintBordersFor(client);

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.containerType_field_label_rows, null, containerType,
            CapacityPeer.ROW_CAPACITY.getName(), new IntegerNumberValidator(
                Messages.ContainerTypeEntryForm_rows_validation_msg, false));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.containerType_field_label_cols, null, containerType,
            CapacityPeer.COL_CAPACITY.getName(), new IntegerNumberValidator(
                Messages.ContainerTypeEntryForm_cols_validation_msg, false));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.containerType_field_label_temperature, null,
            containerType, ContainerTypePeer.DEFAULT_TEMPERATURE.getName(),
            new DoubleNumberValidator(
                Messages.ContainerTypeEntryForm_temperature_validation_msg));

        String currentScheme = containerType.getChildLabelingSchemeName();
        labelingSchemeMap = new HashMap<Integer, String>();
        for (ContainerLabelingSchemeWrapper scheme : ContainerLabelingSchemeWrapper
            .getAllLabelingSchemesMap(SessionManager.getAppService()).values()) {
            labelingSchemeMap.put(scheme.getId(), scheme.getName());
        }
        labelingSchemeComboViewer = createComboViewer(client,
            Messages.containerType_field_label_scheme,
            labelingSchemeMap.values(), currentScheme,
            Messages.ContainerTypeEntryForm_scheme_validation_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    try {
                        containerType
                            .setChildLabelingSchemeName((String) selectedObject);
                    } catch (Exception e) {
                        BgcPlugin
                            .openAsyncError(
                                Messages.ContainerTypeEntryForm_scheme_error_msg,
                                e);
                    }
                }
            });

        activityStatusComboViewer = createComboViewer(client,
            Messages.label_activity,
            ActivityStatusWrapper.getAllActivityStatuses(SessionManager
                .getAppService()), containerType.getActivityStatus(),
            Messages.ContainerTypeEntryForm_activity_validation_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    containerType
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

    }

    private void createContainsSection() throws Exception {
        Composite client =
            createSectionWithClient(Messages.ContainerTypeEntryForm_contents_title);
        hasContainersRadio = toolkit.createButton(client,
            Messages.ContainerTypeEntryForm_contents_button_container,
            SWT.RADIO);
        hasSpecimensRadio = toolkit
            .createButton(client,
                Messages.ContainerTypeEntryForm_contents_button_specimen,
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
        allSpecimenTypes = SpecimenTypeWrapper.getAllSpecimenTypes(
            SessionManager.getAppService(), true);

        specimensMultiSelect = new MultiSelectWidget<SpecimenTypeWrapper>(
            parent, SWT.NONE,
            Messages.ContainerTypeEntryForm_contents_specimen_available,
            Messages.ContainerTypeEntryForm_contents_specimen_selected, 100) {
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
                Messages.ContainerTypeEntryForm_contents_subcontainer_available,
                Messages.ContainerTypeEntryForm_contents_subcontainer_selected,
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
        setSpecimenTypes();
        setChildContainerTypes();
        containerType.persist();
        SessionManager.updateAllSimilarNodes(containerTypeAdapter, true);

    }

    private void setSpecimenTypes() {
        if (hasSpecimens) {
            List<SpecimenTypeWrapper> addedSpcTypes = specimensMultiSelect
                .getAddedToSelection();
            List<SpecimenTypeWrapper> removedSpcTypes = specimensMultiSelect
                .getRemovedFromSelection();
            containerType.addToSpecimenTypeCollection(addedSpcTypes);
            containerType.removeFromSpecimenTypeCollection(removedSpcTypes);
        } else {
            containerType.removeFromSpecimenTypeCollection(containerType
                .getSpecimenTypeCollection());
        }
    }

    private void setChildContainerTypes() {
        if (!hasSpecimens) {
            List<ContainerTypeWrapper> addedTypes =
                childContainerTypesMultiSelect
                    .getAddedToSelection();
            List<ContainerTypeWrapper> removedTypes =
                childContainerTypesMultiSelect
                    .getRemovedFromSelection();
            containerType.addToChildContainerTypeCollection(addedTypes);
            containerType.removeFromChildContainerTypeCollection(removedTypes);
        } else {
            containerType.removeFromChildContainerTypeCollection(containerType
                .getChildContainerTypeCollection());
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return ContainerTypeViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        SiteWrapper site = containerType.getSite();
        containerType.reset();
        containerType.setSite(site);

        if (containerType.isNew()) {
            containerType.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(SessionManager.getAppService()));
        }

        setChildContainerTypeSelection();
        setSpecimenTypesSelection();
        showContainersOrSpecimens();

        GuiUtil.reset(labelingSchemeComboViewer,
            containerType.getChildLabelingSchemeName());
        GuiUtil.reset(activityStatusComboViewer,
            containerType.getActivityStatus());
    }

    private void showContainersOrSpecimens() {
        hasSpecimens = containerType.getSpecimenTypeCollection() != null
            && containerType.getSpecimenTypeCollection().size() > 0;
        showSpecimens(hasSpecimens);
        hasSpecimensRadio.setSelection(hasSpecimens);
        hasContainersRadio.setSelection(!hasSpecimens);
    }
}
