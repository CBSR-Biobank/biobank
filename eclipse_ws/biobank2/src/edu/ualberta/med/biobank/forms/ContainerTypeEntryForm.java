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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
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
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerTypeEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ContainerTypeEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeEntryForm";

    private static final String MSG_NEW_STORAGE_TYPE_OK = "Creating a new storage type.";

    private static final String MSG_STORAGE_TYPE_OK = "Editing an existing storage type.";

    private static final String MSG_NO_CONTAINER_TYPE_NAME = "Container type must have a name";

    private static final String MSG_NO_CONTAINER_TYPE_NAME_SHORT = "Container type must have a short name";

    public static final String MSG_CHILD_LABELING_SCHEME_EMPTY = "Select a child labeling scheme";

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType;

    private MultiSelectWidget samplesMultiSelect;

    private MultiSelectWidget childContainerTypesMultiSelect;

    private List<SampleTypeWrapper> allSampleTypes;

    private List<ContainerTypeWrapper> availSubContainerTypes;

    private SiteWrapper site;

    private BiobankEntryFormWidgetListener multiSelectListener;

    private ComboViewer labelingSchemeComboViewer;

    private Map<Integer, String> labelingSchemeMap;

    private ComboViewer activityStatusComboViewer;

    protected boolean hasSamples = false;

    private Button hasContainersRadio;

    private Button hasSamplesRadio;

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
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        containerTypeAdapter = (ContainerTypeAdapter) adapter;
        containerType = containerTypeAdapter.getContainerType();
        retrieveSiteAndType();
        availSubContainerTypes = new ArrayList<ContainerTypeWrapper>();
        for (ContainerTypeWrapper type : site.getContainerTypeCollection()) {
            if (type.getTopLevel().equals(Boolean.FALSE)) {
                availSubContainerTypes.add(type);
            }
        }
        String tabName;
        if (containerType.isNew()) {
            tabName = "New Container Type";
            containerType.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else {
            tabName = "Container Type " + containerType.getName();
        }
        setPartName(tabName);
    }

    private void retrieveSiteAndType() {
        site = containerTypeAdapter.getParentFromClass(SiteAdapter.class)
            .getWrapper();
        try {
            site.reload();
        } catch (Exception e) {
            logger.error("Can't retrieve site", e);
        }
        try {
            containerType.reload();
        } catch (Exception e) {
            logger.error(
                "Error while retrieving type " + containerType.getName(), e);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container Type Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        createContainerTypeSection();
        createContainsSection();
    }

    protected void createContainerTypeSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        BiobankText siteLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Repository Site");
        setTextValue(siteLabel, containerType.getSite().getName());
        setFirstControl(createBoundWidgetWithLabel(client, BiobankText.class,
            SWT.NONE, "Name", null, containerType, "name",
            new NonEmptyStringValidator(MSG_NO_CONTAINER_TYPE_NAME)));

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            "Short Name", null, containerType, "nameShort",
            new NonEmptyStringValidator(MSG_NO_CONTAINER_TYPE_NAME_SHORT));

        if (containerType.getTopLevel() == null) {
            containerType.setTopLevel(false);
        }
        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            "Top Level Container", null, containerType, "topLevel", null);
        toolkit.paintBordersFor(client);

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE, "Rows",
            null, containerType, "rowCapacity", new IntegerNumberValidator(
                "Row capacity is not a valid number", false));

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            "Columns", null, containerType, "colCapacity",
            new IntegerNumberValidator("Column capacity is not a valid nubmer",
                false));

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            "Default Temperature\n(Celcius)", null, containerType,
            "defaultTemperature", new DoubleNumberValidator(
                "Default temperature is not a valid number"));

        String currentScheme = containerType.getChildLabelingSchemeName();
        labelingSchemeMap = new HashMap<Integer, String>();
        for (ContainerLabelingSchemeWrapper scheme : ContainerLabelingSchemeWrapper
            .getAllLabelingSchemesMap(appService).values()) {
            labelingSchemeMap.put(scheme.getId(), scheme.getName());
        }
        labelingSchemeComboViewer = createComboViewer(client,
            "Child Labeling Scheme", labelingSchemeMap.values(), currentScheme,
            MSG_CHILD_LABELING_SCHEME_EMPTY, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    try {
                        containerType
                            .setChildLabelingSchemeName((String) selectedObject);
                    } catch (Exception e) {
                        BioBankPlugin.openAsyncError(
                            "Error setting the labeling scheme", e);
                    }
                }
            });

        activityStatusComboViewer = createComboViewer(client,
            "Activity status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            containerType.getActivityStatus(),
            "Container type must have an activity status",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    containerType
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, containerType, "comment", null);
    }

    private void createContainsSection() throws Exception {
        Composite client = createSectionWithClient("Contents");
        hasContainersRadio = toolkit.createButton(client,
            "Contains Containers", SWT.RADIO);
        hasSamplesRadio = toolkit.createButton(client, "Contains aliquots",
            SWT.RADIO);
        hasContainersRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hasSamples = !hasContainersRadio.getSelection();
                if (hasContainersRadio.getSelection()) {
                    showSamples(false);
                }
            }
        });
        hasSamplesRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hasSamples = hasSamplesRadio.getSelection();
                if (hasSamplesRadio.getSelection()) {
                    showSamples(true);
                }
            }
        });

        createChildContainerTypesSection(client);
        createSampleTypesSection(client);
        showContainersOrSamples();
    }

    protected void showSamples(boolean show) {
        samplesMultiSelect.setVisible(show);
        ((GridData) samplesMultiSelect.getLayoutData()).exclude = !show;
        childContainerTypesMultiSelect.setVisible(!show);
        ((GridData) childContainerTypesMultiSelect.getLayoutData()).exclude = show;
        form.layout(true, true);
    }

    private void createSampleTypesSection(Composite parent) throws Exception {
        allSampleTypes = SampleTypeWrapper.getAllSampleTypes(appService, true);

        samplesMultiSelect = new MultiSelectWidget(parent, SWT.NONE,
            "Selected Sample Types", "Available Sample Types", 100);
        samplesMultiSelect.adaptToToolkit(toolkit, true);
        samplesMultiSelect.addSelectionChangedListener(multiSelectListener);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        samplesMultiSelect.setLayoutData(gd);

        setSampleTypesSelection();
    }

    private void setSampleTypesSelection() {
        Collection<SampleTypeWrapper> stSamplesTypes = containerType
            .getSampleTypeCollection();
        LinkedHashMap<Integer, String> availSampleTypes = new LinkedHashMap<Integer, String>();
        List<Integer> selSampleTypes = new ArrayList<Integer>();

        if (stSamplesTypes != null) {
            for (SampleTypeWrapper sampleType : stSamplesTypes) {
                selSampleTypes.add(sampleType.getId());
            }
        }

        for (SampleTypeWrapper sampleType : allSampleTypes) {
            availSampleTypes.put(sampleType.getId(), sampleType.getName());
        }
        samplesMultiSelect.setSelections(availSampleTypes, selSampleTypes);
    }

    private void createChildContainerTypesSection(Composite parent) {
        childContainerTypesMultiSelect = new MultiSelectWidget(parent,
            SWT.NONE, "Selected Sub-Container Types",
            "Available Sub-Container Types", 100);
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
        setSampleTypes();
        setChildContainerTypes();
        // associate the storage type to it's site
        containerType.setSite(site);
        containerType.persist();
        containerTypeAdapter.getParent().performExpand();
    }

    private void setSampleTypes() throws BiobankCheckException {
        List<Integer> addedIds = new ArrayList<Integer>();
        List<Integer> removedIds = new ArrayList<Integer>();
        if (hasSamples) {
            addedIds = samplesMultiSelect.getAddedToSelection();
            removedIds = samplesMultiSelect.getRemovedToSelection();
            List<SampleTypeWrapper> addedSampleTypes = new ArrayList<SampleTypeWrapper>();
            List<SampleTypeWrapper> removedSampleTypes = new ArrayList<SampleTypeWrapper>();
            for (SampleTypeWrapper sampleType : allSampleTypes) {
                if (addedIds.indexOf(sampleType.getId()) >= 0) {
                    addedSampleTypes.add(sampleType);
                }
                if (removedIds.indexOf(sampleType.getId()) >= 0) {
                    removedSampleTypes.add(sampleType);
                }
            }
            if (addedIds.size() != addedSampleTypes.size()) {
                throw new BiobankCheckException(
                    "Problem with added sample types");
            }
            if (removedIds.size() != removedSampleTypes.size()) {
                throw new BiobankCheckException(
                    "Problem with removed sample types");
            }
            containerType.addSampleTypes(addedSampleTypes);
            containerType.removeSampleTypes(removedSampleTypes);
        } else {
            containerType.removeSampleTypes(containerType
                .getSampleTypeCollection());
        }
    }

    private void setChildContainerTypes() throws BiobankCheckException {
        List<Integer> addedTypesIds = new ArrayList<Integer>();
        List<Integer> removedTypesIds = new ArrayList<Integer>();
        if (!hasSamples) {
            addedTypesIds = childContainerTypesMultiSelect
                .getAddedToSelection();
            removedTypesIds = childContainerTypesMultiSelect
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
                    "Problem with added child container types");
            }
            if (removedTypesIds.size() != removedContainerTypes.size()) {
                throw new BiobankCheckException(
                    "Problem with removed child container types");
            }
            containerType.addChildContainerTypes(addedContainerTypes);
            containerType.removeChildContainers(removedContainerTypes);
        } else {
            containerType.removeChildContainers(containerType
                .getChildContainerTypeCollection());
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return ContainerTypeViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();

        setChildContainerTypeSelection();
        setSampleTypesSelection();
        showContainersOrSamples();
        setLabelingScheme();
        setActivityStatus();
    }

    private void showContainersOrSamples() {
        boolean containsSamples = containerType.getSampleTypeCollection() != null
            && containerType.getSampleTypeCollection().size() > 0;
        showSamples(containsSamples);
        hasSamplesRadio.setSelection(containsSamples);
        hasContainersRadio.setSelection(!containsSamples);
    }

    private void setLabelingScheme() {
        String currentScheme = containerType.getChildLabelingSchemeName();
        if (currentScheme == null) {
            labelingSchemeComboViewer.getCombo().deselectAll();
        } else {
            labelingSchemeComboViewer.setSelection(new StructuredSelection(
                currentScheme));
        }
    }

    private void setActivityStatus() {
        ActivityStatusWrapper activity = containerType.getActivityStatus();
        if (activity == null) {
            activityStatusComboViewer.getCombo().deselectAll();
        } else {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                activity));
        }
    }
}
