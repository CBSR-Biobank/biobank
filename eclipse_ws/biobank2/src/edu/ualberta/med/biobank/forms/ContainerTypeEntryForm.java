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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.CapacityPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.BiobankLogger;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeEntryForm extends BiobankEntryForm {

    @SuppressWarnings("unused")
    private static BiobankLogger logger = BiobankLogger
        .getLogger(ContainerTypeEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_STORAGE_TYPE_OK = Messages
        .getString("ContainerTypeEntryForm.creation.msg"); //$NON-NLS-1$

    private static final String MSG_STORAGE_TYPE_OK = Messages
        .getString("ContainerTypeEntryForm.edition.msg"); //$NON-NLS-1$

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType;

    private MultiSelectWidget specimensMultiSelect;

    private MultiSelectWidget childContainerTypesMultiSelect;

    private List<SpecimenTypeWrapper> allSpecimenTypes;

    private List<ContainerTypeWrapper> availSubContainerTypes;

    private BiobankEntryFormWidgetListener multiSelectListener;

    private ComboViewer labelingSchemeComboViewer;

    private Map<Integer, String> labelingSchemeMap;

    private ComboViewer activityStatusComboViewer;

    protected boolean hasSpecimens = false;

    private Button hasContainersRadio;

    private Button hasSpecimensRadio;

    public ContainerTypeEntryForm() {
        super();
        multiSelectListener = new BiobankEntryFormWidgetListener() {
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
            tabName = Messages.getString("ContainerTypeEntryForm.new.title"); //$NON-NLS-1$
            containerType.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else {
            tabName = Messages.getString("ContainerTypeEntryForm.edit.title", //$NON-NLS-1$
                containerType.getName());
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ContainerTypeEntryForm.main.title")); //$NON-NLS-1$
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

        BiobankText name = (BiobankText) createBoundWidgetWithLabel(
            client,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("label.name"), //$NON-NLS-1$
            null,
            containerType,
            ContainerTypePeer.NAME.getName(),
            new NonEmptyStringValidator(Messages
                .getString("ContainerTypeEntryForm.name.validation.msg"))); //$NON-NLS-1$

        setFirstControl(name);

        createBoundWidgetWithLabel(
            client,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("label.nameShort"), //$NON-NLS-1$
            null,
            containerType,
            ContainerTypePeer.NAME_SHORT.getName(),
            new NonEmptyStringValidator(Messages
                .getString("ContainerTypeEntryForm.nameShort.validation.msg"))); //$NON-NLS-1$

        if (containerType.getTopLevel() == null) {
            containerType.setTopLevel(false);
        }
        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            Messages.getString("containerType.field.label.topLevel"), null, //$NON-NLS-1$
            containerType, ContainerTypePeer.TOP_LEVEL.getName(), null);
        toolkit.paintBordersFor(client);

        createBoundWidgetWithLabel(
            client,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("containerType.field.label.rows"), //$NON-NLS-1$
            null,
            containerType,
            CapacityPeer.ROW_CAPACITY.getName(),
            new IntegerNumberValidator(Messages
                .getString("ContainerTypeEntryForm.rows.validation.msg"), false)); //$NON-NLS-1$

        createBoundWidgetWithLabel(
            client,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("containerType.field.label.cols"), //$NON-NLS-1$
            null,
            containerType,
            CapacityPeer.COL_CAPACITY.getName(),
            new IntegerNumberValidator(Messages
                .getString("ContainerTypeEntryForm.cols.validation.msg"), false)); //$NON-NLS-1$

        createBoundWidgetWithLabel(
            client,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("containerType.field.label.temperature"), //$NON-NLS-1$
            null,
            containerType,
            ContainerTypePeer.DEFAULT_TEMPERATURE.getName(),
            new DoubleNumberValidator(Messages
                .getString("ContainerTypeEntryForm.temperature.validation.msg"))); //$NON-NLS-1$

        String currentScheme = containerType.getChildLabelingSchemeName();
        labelingSchemeMap = new HashMap<Integer, String>();
        for (ContainerLabelingSchemeWrapper scheme : ContainerLabelingSchemeWrapper
            .getAllLabelingSchemesMap(appService).values()) {
            labelingSchemeMap.put(scheme.getId(), scheme.getName());
        }
        labelingSchemeComboViewer = createComboViewer(
            client,
            Messages.getString("containerType.field.label.scheme"), //$NON-NLS-1$
            labelingSchemeMap.values(), currentScheme,
            Messages.getString("ContainerTypeEntryForm.scheme.validation.msg"), //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    try {
                        containerType
                            .setChildLabelingSchemeName((String) selectedObject);
                    } catch (Exception e) {
                        BiobankGuiCommonPlugin.openAsyncError(
                            Messages
                                .getString("ContainerTypeEntryForm.scheme.error.msg"), //$NON-NLS-1$
                            e);
                    }
                }
            });

        activityStatusComboViewer = createComboViewer(
            client,
            Messages.getString("label.activity"), //$NON-NLS-1$
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            containerType.getActivityStatus(),
            Messages
                .getString("ContainerTypeEntryForm.activity.validation.msg"), //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    containerType
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            Messages.getString("label.comments"), null, containerType, //$NON-NLS-1$
            ContainerTypePeer.COMMENT.getName(), null);

    }

    private void createContainsSection() throws Exception {
        Composite client = createSectionWithClient(Messages
            .getString("ContainerTypeEntryForm.contents.title")); //$NON-NLS-1$
        hasContainersRadio = toolkit.createButton(client, Messages
            .getString("ContainerTypeEntryForm.contents.button.container"), //$NON-NLS-1$
            SWT.RADIO);
        hasSpecimensRadio = toolkit.createButton(client, Messages
            .getString("ContainerTypeEntryForm.contents.button.specimen"), //$NON-NLS-1$
            SWT.RADIO);
        hasContainersRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hasSpecimens = !hasContainersRadio.getSelection();
                if (hasContainersRadio.getSelection()) {
                    showSpecimens(false);
                }
            }
        });
        hasSpecimensRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hasSpecimens = hasSpecimensRadio.getSelection();
                if (hasSpecimensRadio.getSelection()) {
                    showSpecimens(true);
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

        specimensMultiSelect = new MultiSelectWidget(
            parent,
            SWT.NONE,
            Messages
                .getString("ContainerTypeEntryForm.contents.specimen.available"), //$NON-NLS-1$
            Messages
                .getString("ContainerTypeEntryForm.contents.specimen.selected"), //$NON-NLS-1$
            100);
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
        childContainerTypesMultiSelect = new MultiSelectWidget(
            parent,
            SWT.NONE,
            Messages
                .getString("ContainerTypeEntryForm.contents.subcontainer.available"), //$NON-NLS-1$
            Messages
                .getString("ContainerTypeEntryForm.contents.subcontainer.selected"), //$NON-NLS-1$
            100);
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
                    Messages
                        .getString("ContainerTypeEntryForm.save.error.msg.specimen.added")); //$NON-NLS-1$
            }
            if (removedIds.size() != removedSpecimenTypes.size()) {
                throw new BiobankCheckException(
                    Messages
                        .getString("ContainerTypeEntryForm.save.error.msg.specimen.removed")); //$NON-NLS-1$
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
                    Messages
                        .getString("ContainerTypeEntryForm.save.error.msg.subcontainer.added")); //$NON-NLS-1$
            }
            if (removedTypesIds.size() != removedContainerTypes.size()) {
                throw new BiobankCheckException(
                    Messages
                        .getString("ContainerTypeEntryForm.save.error.msg.subcontainer.removed")); //$NON-NLS-1$
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
