package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.PojoObservables;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.helpers.GetHelper;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ContainerTypeComparator;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.SampleTypeComparator;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;

public class ContainerTypeEntryForm extends BiobankEntryForm {
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

    private List<SampleType> allSampleTypes;

    private List<ContainerType> allContainerTypes;

    private Site site;

    private BiobankEntryFormWidgetListener multiSelectListener;

    private ComboViewer labelingSchemeComboViewer;

    private Button hasSamples;

    private Button hasContainers;

    private Control firstControl;

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
        allContainerTypes = new ArrayList<ContainerType>(site
            .getContainerTypeCollection());
        Collections.sort(allContainerTypes, new ContainerTypeComparator());
        String tabName;
        if (containerType.isNew()) {
            tabName = "New Container Type";
        } else {
            tabName = "Container Type " + containerType.getName();
        }
        setPartName(tabName);
    }

    private void retrieveSiteAndType() {
        // FIXME once siteAdapter contains a wrapper, call reload on it
        // to get last inserted types
        site = containerTypeAdapter.getParentFromClass(SiteAdapter.class)
            .getSite();
        try {
            site = ModelUtils.getObjectWithId(appService, Site.class, site
                .getId());
        } catch (Exception e) {
            SessionManager.getLogger().error("Can't retrieve site", e);
        }
        try {
            containerType.reload();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while retrieving type " + containerType.getName(), e);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container Type Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));

        createContainerTypeSection();
        createDimensionsSection();
        createContainsSection();
    }

    protected void createContainerTypeSection() throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Label siteLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Site");
        FormUtils.setTextValue(siteLabel, containerType.getSite().getName());

        firstControl = createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Name", null, PojoObservables.observeValue(containerType, "name"),
            NonEmptyString.class, MSG_NO_CONTAINER_TYPE_NAME);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Short Name",
            null, PojoObservables.observeValue(containerType, "nameShort"),
            NonEmptyString.class, MSG_NO_CONTAINER_TYPE_NAME_SHORT);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Default Temperature\n(Celcius)", null, PojoObservables
                .observeValue(containerType, "defaultTemperature"),
            DoubleNumber.class, "Default temperature is not a valid number");

        List<ContainerLabelingScheme> schemes = appService.search(
            ContainerLabelingScheme.class, new ContainerLabelingScheme());

        ContainerLabelingScheme currentScheme = containerType
            .getChildLabelingScheme();
        if (currentScheme != null) {
            for (ContainerLabelingScheme scheme : schemes) {
                if (currentScheme.getId().equals(scheme.getId())) {
                    currentScheme = scheme;
                    break;
                }
            }
        }

        labelingSchemeComboViewer = createCComboViewerWithNoSelectionValidator(
            client, "Child Labeling Scheme", schemes, currentScheme,
            MSG_CHILD_LABELING_SCHEME_EMPTY);

        createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
            "Activity Status", FormConstants.ACTIVITY_STATUS, PojoObservables
                .observeValue(containerType, "activityStatus"), null, null);

        if (containerType.getTopLevel() == null) {
            containerType.setTopLevel(false);
        }
        createBoundWidgetWithLabel(client, Button.class, SWT.CHECK,
            "Top Level Container", null, PojoObservables.observeValue(
                containerType, "topLevel"), null);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, PojoObservables.observeValue(
                containerType, "comment"), null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
    }

    private void createDimensionsSection() {
        Composite client = createSectionWithClient("Default Capacity");

        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Rows", null,
            PojoObservables.observeValue(containerType.getCapacity(),
                "rowCapacity"), new IntegerNumber(
                "Row capactiy is not a valid number", false));

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Columns",
            null, PojoObservables.observeValue(containerType.getCapacity(),
                "colCapacity"), new IntegerNumber(
                "Column capacity is not a valid nubmer", false));
    }

    private void createContainsSection() {
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
        boolean containsSamples = containerType.getSampleTypeCollection() != null
            && containerType.getSampleTypeCollection().size() > 0;
        showSamples(containsSamples);
        hasSamples.setSelection(containsSamples);
        hasContainers.setSelection(!containsSamples);
    }

    protected void showSamples(boolean show) {
        samplesMultiSelect.setVisible(show);
        ((GridData) samplesMultiSelect.getLayoutData()).exclude = !show;
        childContainerTypesMultiSelect.setVisible(!show);
        ((GridData) childContainerTypesMultiSelect.getLayoutData()).exclude = show;
        form.layout(true, true);
    }

    private void createSampleTypesSection(Composite parent) {
        Collection<SampleType> stSamplesTypes = containerType
            .getSampleTypeCollection();

        GetHelper<SampleType> helper = new GetHelper<SampleType>();

        allSampleTypes = helper.getModelObjects(appService, SampleType.class);
        Collections.sort(allSampleTypes, new SampleTypeComparator());

        samplesMultiSelect = new MultiSelectWidget(parent, SWT.NONE,
            "Selected Sample Types", "Available Sample Types", 100);
        samplesMultiSelect.adaptToToolkit(toolkit, true);
        samplesMultiSelect.addSelectionChangedListener(multiSelectListener);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        samplesMultiSelect.setLayoutData(gd);

        ListOrderedMap availSampleTypes = new ListOrderedMap();
        List<Integer> selSampleTypes = new ArrayList<Integer>();

        if (stSamplesTypes != null) {
            for (SampleType sampleType : stSamplesTypes) {
                selSampleTypes.add(sampleType.getId());
            }
        }

        for (SampleType sampleType : allSampleTypes) {
            availSampleTypes.put(sampleType.getId(), sampleType.getName());
        }
        samplesMultiSelect.addSelections(availSampleTypes, selSampleTypes);
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

        ListOrderedMap availContainerTypes = new ListOrderedMap();
        List<Integer> selChildContainerTypes = new ArrayList<Integer>();

        Collection<ContainerType> childContainerTypes = containerType
            .getChildContainerTypeCollection();
        if (childContainerTypes != null) {
            for (ContainerType childContainerType : childContainerTypes) {
                selChildContainerTypes.add(childContainerType.getId());
            }
        }

        Integer myId = new Integer(0);
        if (containerType.getId() != null) {
            myId = containerType.getId();
        }

        if (allContainerTypes != null)
            for (ContainerType type : allContainerTypes) {
                Integer id = type.getId();
                if (myId.compareTo(id) != 0
                    && (type.getTopLevel() == null || type.getTopLevel() == false)) {
                    availContainerTypes.put(id, type.getName());
                }
            }
        childContainerTypesMultiSelect.addSelections(availContainerTypes,
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
        List<Integer> selectedIds = null;
        if (hasSamples.getSelection()) {
            selectedIds = samplesMultiSelect.getSelected();
        }
        containerType.setSampleTypes(selectedIds, allSampleTypes);
        // set childcontainers
        selectedIds = null;
        if (hasContainers.getSelection()) {
            selectedIds = childContainerTypesMultiSelect.getSelected();
        }
        containerType.setChildContainerTypes(selectedIds, allContainerTypes);

        // associate the storage type to it's site
        containerType.setSite(site);

        // set the labeling scheme
        ContainerLabelingScheme scheme = (ContainerLabelingScheme) ((StructuredSelection) labelingSchemeComboViewer
            .getSelection()).getFirstElement();
        containerType.setChildLabelingScheme(scheme);

        containerType.persist();
        containerTypeAdapter.getParent().performExpand();
    }

    @Override
    public String getNextOpenedFormID() {
        return ContainerTypeViewForm.ID;
    }

    @Override
    public void setFocus() {
        firstControl.setFocus();
    }
}
