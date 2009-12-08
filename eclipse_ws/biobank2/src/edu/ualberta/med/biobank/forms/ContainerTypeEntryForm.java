package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.BeansObservables;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.LabelingScheme;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

public class ContainerTypeEntryForm extends BiobankEntryForm {

    private static Logger LOGGER = Logger
        .getLogger(ContainerTypeEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeEntryForm";

    private static final String MSG_NEW_STORAGE_TYPE_OK = "Creating a new storage type.";

    private static final String MSG_STORAGE_TYPE_OK = "Editing an existing storage type.";

    private static final String MSG_NO_CONTAINER_TYPE_NAME = "Container type must have a name";

    private static final String MSG_NO_CONTAINER_TYPE_NAME_SHORT = "Container type must have a short name";

    public static final String MSG_CHILD_LABELING_SCHEME_EMPTY = "Select a child labeling scheme";

    static Logger log4j = Logger.getLogger(SessionManager.class.getName());

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerTypeWrapper containerType;

    private MultiSelectWidget samplesMultiSelect;

    private MultiSelectWidget childContainerTypesMultiSelect;

    private List<SampleTypeWrapper> allSampleTypes;

    private List<ContainerTypeWrapper> allContainerTypes;

    private SiteWrapper site;

    private BiobankEntryFormWidgetListener multiSelectListener;

    private ComboViewer labelingSchemeComboViewer;

    private List<LabelingScheme> labelingSchemeCollection;

    private Button hasSamples;

    private Button hasContainers;

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
    public void init() {
        Assert.isTrue((adapter instanceof ContainerTypeAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        containerTypeAdapter = (ContainerTypeAdapter) adapter;
        containerType = containerTypeAdapter.getContainerType();
        retrieveSiteAndType();
        allContainerTypes = site.getContainerTypeCollection();
        for (ContainerTypeWrapper type : new ArrayList<ContainerTypeWrapper>(
            allContainerTypes)) {
            if (Boolean.TRUE.equals(type.getTopLevel())) {
                allContainerTypes.remove(type);
            }
        }
        String tabName;
        if (containerType.isNew()) {
            tabName = "New Container Type";
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
            LOGGER.error("Can't retrieve site", e);
        }
        try {
            containerType.reload();
        } catch (Exception e) {
            LOGGER.error("Error while retrieving type "
                + containerType.getName(), e);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container Type Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getIconForTypeName(
            containerType.getName()));

        createContainerTypeSection();
        createContainsSection();
    }

    protected void createContainerTypeSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Label siteLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Site");
        setTextValue(siteLabel, containerType.getSite().getName());
        firstControl = createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Name", null, BeansObservables.observeValue(containerType, "name"),
            new NonEmptyStringValidator(MSG_NO_CONTAINER_TYPE_NAME));

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Short Name",
            null, BeansObservables.observeValue(containerType, "nameShort"),
            new NonEmptyStringValidator(MSG_NO_CONTAINER_TYPE_NAME_SHORT));

        if (containerType.getTopLevel() == null) {
            containerType.setTopLevel(false);
        }
        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            "Top Level Container", null, BeansObservables.observeValue(
                containerType, "topLevel"), null);
        toolkit.paintBordersFor(client);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Rows", null,
            BeansObservables.observeValue(containerType, "rowCapacity"),
            new IntegerNumberValidator("Row capacity is not a valid number",
                false));

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Columns",
            null, BeansObservables.observeValue(containerType, "colCapacity"),
            new IntegerNumberValidator("Column capacity is not a valid nubmer",
                false));

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Default Temperature\n(Celcius)", null, BeansObservables
                .observeValue(containerType, "defaultTemperature"),
            new DoubleNumberValidator(
                "Default temperature is not a valid number"));

        LabelingScheme currentScheme = getCurrentLabelingScheme();
        labelingSchemeComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Child Labeling Scheme", labelingSchemeCollection,
            currentScheme, MSG_CHILD_LABELING_SCHEME_EMPTY);

        createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
            "Activity Status", FormConstants.ACTIVITY_STATUS, BeansObservables
                .observeValue(containerType, "activityStatus"), null);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, BeansObservables.observeValue(
                containerType, "comment"), null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
    }

    private LabelingScheme getCurrentLabelingScheme() {
        LabelingScheme currentScheme = null;
        Integer currentSchemeId = containerType.getChildLabelingScheme();
        Map<Integer, String> labelingSchemeMap = ContainerTypeWrapper
            .getAllLabelingSchemes(appService);
        labelingSchemeCollection = new ArrayList<LabelingScheme>();
        for (Integer id : labelingSchemeMap.keySet()) {
            LabelingScheme ls = new LabelingScheme(id, labelingSchemeMap
                .get(id));
            labelingSchemeCollection.add(ls);
            if (id.equals(currentSchemeId)) {
                currentScheme = ls;
            }
        }
        return currentScheme;
    }

    private void createContainsSection() throws Exception {
        Composite client = createSectionWithClient("Contains");
        hasContainers = toolkit.createButton(client, "Contains Containers",
            SWT.RADIO);
        hasSamples = toolkit
            .createButton(client, "Contains samples", SWT.RADIO);
        hasContainers.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (hasContainers.getSelection()) {
                    showSamples(false);
                }
            }
        });
        hasSamples.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (hasSamples.getSelection()) {
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
        allSampleTypes = site.getAllSampleTypeCollection();

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
        ListOrderedMap availSampleTypes = new ListOrderedMap();
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
        ListOrderedMap availContainerTypes = new ListOrderedMap();
        if (allContainerTypes != null) {
            for (ContainerTypeWrapper type : allContainerTypes) {
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
        // set sampletypes
        List<Integer> selectedIds = new ArrayList<Integer>();
        if (hasSamples.getSelection()) {
            selectedIds = samplesMultiSelect.getSelected();
        }
        containerType.setSampleTypes(selectedIds, allSampleTypes);
        // set childcontainers
        selectedIds = new ArrayList<Integer>();
        if (hasContainers.getSelection()) {
            selectedIds = childContainerTypesMultiSelect.getSelected();
        }
        containerType.setChildContainerTypes(selectedIds, allContainerTypes);

        // associate the storage type to it's site
        containerType.setSite(site);

        // set the labeling scheme
        LabelingScheme currentScheme = (LabelingScheme) ((StructuredSelection) labelingSchemeComboViewer
            .getSelection()).getFirstElement();
        containerType.setChildLabelingScheme(currentScheme.id);

        containerType.persist();
        containerTypeAdapter.getParent().performExpand();
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

    }

    private void showContainersOrSamples() {
        boolean containsSamples = containerType.getSampleTypeCollection() != null
            && containerType.getSampleTypeCollection().size() > 0;
        showSamples(containsSamples);
        hasSamples.setSelection(containsSamples);
        hasContainers.setSelection(!containsSamples);

        LabelingScheme currentScheme = getCurrentLabelingScheme();
        if (currentScheme == null) {
            labelingSchemeComboViewer.getCombo().deselectAll();
        } else {
            labelingSchemeComboViewer.setSelection(new StructuredSelection(
                currentScheme));
        }
    }
}
