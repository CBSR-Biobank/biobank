package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
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
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeEntryForm extends BiobankEntryForm {

    @SuppressWarnings("unused")
    private static BgcLogger logger = BgcLogger
        .getLogger(ContainerTypeEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_STORAGE_TYPE_OK = Messages.ContainerTypeEntryForm_creation_msg;

    private static final String MSG_STORAGE_TYPE_OK = Messages.ContainerTypeEntryForm_edition_msg;

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType;

    private MultiSelectWidget specimensMultiSelect;

    private MultiSelectWidget childContainerTypesMultiSelect;

    private List<SpecimenTypeWrapper> allSpecimenTypes;

    private List<ContainerTypeWrapper> availSubContainerTypes;

    private BgcEntryFormWidgetListener multiSelectListener;

    private ComboViewer labelingSchemeComboViewer;

    private Map<Integer, String> labelingSchemeMap;

    private ComboViewer activityStatusComboViewer;

    protected boolean hasSpecimens = false;

    private Button hasContainersRadio;

    private Button hasSpecimensRadio;

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
                .getActiveActivityStatus(appService));
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
        createContainsSection();
        setChildContainerTypeSelection();
    }

    protected void createContainerTypeSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        availSubContainerTypes = new ArrayList<ContainerTypeWrapper>();
        adapter.setParent(((SiteAdapter) SessionManager
            .searchFirstNode(containerType.getSite()))
            .getContainerTypesGroupNode());
        for (ContainerTypeWrapper type : containerType.getSite()
            .getContainerTypeCollection()) {
            if (type.getTopLevel().equals(Boolean.FALSE)) {
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
            .getAllLabelingSchemesMap(appService).values()) {
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
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            containerType.getActivityStatus(),
            Messages.ContainerTypeEntryForm_activity_validation_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    containerType
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            Messages.label_comments, null, containerType,
            ContainerTypePeer.COMMENT.getName(), null);

    }

    private void createContainsSection() throws Exception {
        Composite client = createSectionWithClient(Messages.ContainerTypeEntryForm_contents_title);
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
        ((GridData) childContainerTypesMultiSelect.getLayoutData()).exclude = show;
        form.layout(true, true);
    }

    private void createSpecimenTypesSection(Composite parent) throws Exception {
        allSpecimenTypes = SpecimenTypeWrapper.getAllSpecimenTypes(appService,
            true);

        specimensMultiSelect = new MultiSelectWidget(parent, SWT.NONE,
            Messages.ContainerTypeEntryForm_contents_specimen_available,
            Messages.ContainerTypeEntryForm_contents_specimen_selected, 100);
        specimensMultiSelect.adaptToToolkit(toolkit, true);
        specimensMultiSelect.addSelectionChangedListener(multiSelectListener);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        specimensMultiSelect.setLayoutData(gd);

        setSpecimenTypesSelection();
    }

    private void setSpecimenTypesSelection() {
        Collection<SpecimenTypeWrapper> stSamplesTypes = containerType
            .getSpecimenTypeCollection();
        LinkedHashMap<Integer, String> availSpecimenTypes = new LinkedHashMap<Integer, String>();
        List<Integer> selSpecimenTypes = new ArrayList<Integer>();

        if (stSamplesTypes != null) {
            for (SpecimenTypeWrapper sampleType : stSamplesTypes) {
                selSpecimenTypes.add(sampleType.getId());
            }
        }

        for (SpecimenTypeWrapper sampleType : allSpecimenTypes) {
            availSpecimenTypes.put(sampleType.getId(), sampleType.getName());
        }
        specimensMultiSelect
            .setSelections(availSpecimenTypes, selSpecimenTypes);
    }

    private void createChildContainerTypesSection(Composite parent) {
        childContainerTypesMultiSelect = new MultiSelectWidget(parent,
            SWT.NONE,
            Messages.ContainerTypeEntryForm_contents_subcontainer_available,
            Messages.ContainerTypeEntryForm_contents_subcontainer_selected, 100);
        childContainerTypesMultiSelect.adaptToToolkit(toolkit, true);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        childContainerTypesMultiSelect.setLayoutData(gd);

        childContainerTypesMultiSelect
            .addSelectionChangedListener(multiSelectListener);

        setChildContainerTypeSelection();
    }

    private void setChildContainerTypeSelection() {
        List<Integer> selChildContainerTypes = new ArrayList<Integer>();
        Collection<ContainerTypeWrapper> childContainerTypes = containerType
            .getChildContainerTypeCollection();
        if (childContainerTypes != null) {
            for (ContainerTypeWrapper childContainerType : childContainerTypes) {
                selChildContainerTypes.add(childContainerType.getId());
            }
        }
        LinkedHashMap<Integer, String> availContainerTypes = new LinkedHashMap<Integer, String>();
        if (availSubContainerTypes != null) {
            for (ContainerTypeWrapper type : availSubContainerTypes) {
                if (containerType.isNew() || !containerType.equals(type)) {
                    availContainerTypes.put(type.getId(), type.getName());
                }
            }
        }
        childContainerTypesMultiSelect.setSelections(availContainerTypes,
            selChildContainerTypes);
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

    private void setSpecimenTypes() throws BiobankCheckException {
        if (hasSpecimens) {
            List<Integer> addedIds = specimensMultiSelect.getAddedToSelection();
            List<Integer> removedIds = specimensMultiSelect
                .getRemovedToSelection();
            List<SpecimenTypeWrapper> addedSpecimenTypes = new ArrayList<SpecimenTypeWrapper>();
            List<SpecimenTypeWrapper> removedSpecimenTypes = new ArrayList<SpecimenTypeWrapper>();
            for (SpecimenTypeWrapper sampleType : allSpecimenTypes) {
                if (addedIds.indexOf(sampleType.getId()) >= 0) {
                    addedSpecimenTypes.add(sampleType);
                }
                if (removedIds.indexOf(sampleType.getId()) >= 0) {
                    removedSpecimenTypes.add(sampleType);
                }
            }
            if (addedIds.size() != addedSpecimenTypes.size()) {
                throw new BiobankCheckException(
                    Messages.ContainerTypeEntryForm_save_error_msg_specimen_added);
            }
            if (removedIds.size() != removedSpecimenTypes.size()) {
                throw new BiobankCheckException(
                    Messages.ContainerTypeEntryForm_save_error_msg_specimen_removed);
            }
            containerType.addToSpecimenTypeCollection(addedSpecimenTypes);
            containerType
                .removeFromSpecimenTypeCollection(removedSpecimenTypes);
        } else {
            containerType.removeFromSpecimenTypeCollection(containerType
                .getSpecimenTypeCollection());
        }
    }

    private void setChildContainerTypes() throws BiobankCheckException {
        if (!hasSpecimens) {
            List<Integer> addedTypesIds = childContainerTypesMultiSelect
                .getAddedToSelection();
            List<Integer> removedTypesIds = childContainerTypesMultiSelect
                .getRemovedToSelection();
            List<ContainerTypeWrapper> addedContainerTypes = new ArrayList<ContainerTypeWrapper>();
            List<ContainerTypeWrapper> removedContainerTypes = new ArrayList<ContainerTypeWrapper>();
            if (availSubContainerTypes != null) {
                for (ContainerTypeWrapper containerType : availSubContainerTypes) {
                    if (addedTypesIds.indexOf(containerType.getId()) >= 0) {
                        addedContainerTypes.add(containerType);
                    }
                    if (removedTypesIds.indexOf(containerType.getId()) >= 0) {
                        removedContainerTypes.add(containerType);
                    }
                }
            }
            if (addedTypesIds.size() != addedContainerTypes.size()) {
                throw new BiobankCheckException(
                    Messages.ContainerTypeEntryForm_save_error_msg_subcontainer_added);
            }
            if (removedTypesIds.size() != removedContainerTypes.size()) {
                throw new BiobankCheckException(
                    Messages.ContainerTypeEntryForm_save_error_msg_subcontainer_removed);
            }
            containerType
                .addToChildContainerTypeCollection(addedContainerTypes);
            containerType
                .removeFromChildContainerTypeCollection(removedContainerTypes);
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
    protected void onReset() throws Exception {
        SiteWrapper site = containerType.getSite();
        containerType.reset();
        containerType.setSite(site);

        if (containerType.isNew()) {
            containerType.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
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
