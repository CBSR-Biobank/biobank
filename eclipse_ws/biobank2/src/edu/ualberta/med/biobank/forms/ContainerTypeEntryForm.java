package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.helpers.GetHelper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ContainerTypeComparator;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.SampleTypeComparator;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerTypeEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerTypeEntryForm";

    private static final String MSG_NEW_STORAGE_TYPE_OK = "Creating a new storage type.";

    private static final String MSG_STORAGE_TYPE_OK = "Editing an existing storage type.";

    private static final String MSG_NO_CONTAINER_TYPE_NAME = "Container type must have a name";

    private static final String MSG_NO_CONTAINER_TYPE_NAME_SHORT = "Container type must have a short name";

    public static final String MSG_CHILD_LABELING_SCHEME_EMPTY = "Select a child labeling scheme";

    static Logger log4j = Logger.getLogger(SessionManager.class.getName());

    private ContainerTypeAdapter containerTypeAdapter;

    private ContainerType containerType;

    private Capacity capacity;

    private MultiSelectWidget samplesMultiSelect;

    private MultiSelectWidget childContainerTypesMultiSelect;

    private List<SampleType> allSampleTypes;

    private List<ContainerType> allContainerTypes;

    private Site site;

    private BiobankEntryFormWidgetListener multiSelectListener;

    private ComboViewer labelingSchemeComboViewer;

    private boolean topLevel;

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
        retrieveSite();
        allContainerTypes = new ArrayList<ContainerType>(site
            .getContainerTypeCollection());
        Collections.sort(allContainerTypes, new ContainerTypeComparator());
        capacity = new Capacity();
        String tabName;
        if (containerType.getId() == null) {
            topLevel = false;
            tabName = "New Container Type";

        } else {
            tabName = "Container Type " + containerType.getName();
            topLevel = new Boolean(containerType.getTopLevel());
            capacity.setRowCapacity(new Integer(containerType.getCapacity()
                .getRowCapacity()));
            capacity.setColCapacity(new Integer(containerType.getCapacity()
                .getColCapacity()));
        }

        setPartName(tabName);
    }

    private void retrieveSite() {
        // to get last inserted types
        site = containerTypeAdapter.getParentFromClass(SiteAdapter.class)
            .getSite();
        try {
            site = ModelUtils.getObjectWithId(appService, Site.class, site
                .getId());
        } catch (Exception e) {
            e.printStackTrace();
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

        initCancelConfirmWidget(form.getBody());
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
        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Name", null,
            PojoObservables.observeValue(containerType, "name"),
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
            PojoObservables.observeValue(capacity, "rowCapacity"),
            new IntegerNumber("Row capactiy is not a valid number", false));

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Columns",
            null, PojoObservables.observeValue(capacity, "colCapacity"),
            new IntegerNumber("Column capacity is not a valid nubmer", false));
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

    protected void initConfirmAddButton(Composite parent,
        boolean doSaveInternalAction, boolean doSaveEditorAction) {
        Button confirmAddButton = toolkit.createButton(parent,
            "Confirm and Add Child", SWT.PUSH);
        if (doSaveInternalAction) {
            confirmAddButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    doSaveInternal();
                }
            });
        }
        if (doSaveEditorAction) {
            confirmAddButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {

                    Collection<ContainerType> children = containerType
                        .getChildContainerTypeCollection();
                    if (children == null)
                        children = new ArrayList<ContainerType>();
                    ContainerType childContainerType = new ContainerType();
                    children.add(childContainerType);
                    containerType.setChildContainerTypeCollection(children);
                    ContainerTypeAdapter childAdapter = new ContainerTypeAdapter(
                        containerTypeAdapter.getParent(), childContainerType);

                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().saveEditor(
                            ContainerTypeEntryForm.this, false);
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().closeEditor(
                            ContainerTypeEntryForm.this, false);

                    AdapterBase.openForm(new FormInput(childAdapter),
                        ContainerTypeEntryForm.ID);
                    containerTypeAdapter.getParent().performExpand();
                }
            });
        }
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
        SDKQuery query;
        SDKQueryResult result;

        if ((containerType.getId() == null) && !checkContainerTypeNameUnique()) {
            setDirty(true);
            return;
        }
        saveSampleTypes();

        saveChildContainerTypes();

        if (containerType.getId() != null) {
            Integer localRows = capacity.getRowCapacity();
            Integer localCols = capacity.getColCapacity();
            Integer dbRows = containerType.getCapacity().getRowCapacity();
            Integer dbCols = containerType.getCapacity().getColCapacity();

            // if new type, set is ok, if unmodified, set is ok. if doesn't
            // exist in
            // db, set is ok

            if (!(localRows.equals(dbRows) && localCols.equals(dbCols))
                && !HQLSafeToChange())
                BioBankPlugin
                    .openError(
                        "ContainerType Change Failed",
                        "Unable to alter dimensions. A container of this type exists in storage. Remove all instances before attempting to modify this container type.");
            else {
                containerType.setCapacity(capacity);
            }
        } else {
            containerType.setCapacity(capacity);
        }

        if (topLevel != containerType.getTopLevel() && !HQLSafeToChange()) {
            containerType.setTopLevel(topLevel);
            BioBankPlugin
                .openError(
                    "ContainerType Change Failed",
                    "Unable to change the \"Top Level\" property. A container requiring this property exists in storage. Remove all instances before attempting to modify this container type.");
        }

        // associate the storage type to it's site
        containerType.setSite(site);

        ContainerLabelingScheme scheme = (ContainerLabelingScheme) ((StructuredSelection) labelingSchemeComboViewer
            .getSelection()).getFirstElement();

        Assert.isNotNull(scheme);
        containerType.setChildLabelingScheme(scheme);

        if ((containerType.getId() == null) || (containerType.getId() == 0)) {
            query = new InsertExampleQuery(containerType);
        } else {
            query = new UpdateExampleQuery(containerType);
        }

        result = appService.executeQuery(query);
        containerType = (ContainerType) result.getObjectResult();

        containerTypeAdapter.setContainerType(containerType);
        containerTypeAdapter.getParent().performExpand();
    }

    private void saveSampleTypes() {
        Set<SampleType> selSampleTypes = new HashSet<SampleType>();
        if (hasSamples.getSelection()) {
            List<Integer> selSampleTypeIds = samplesMultiSelect.getSelected();
            for (SampleType sampleType : allSampleTypes) {
                int id = sampleType.getId();
                if (selSampleTypeIds.indexOf(id) >= 0) {
                    selSampleTypes.add(sampleType);
                }
            }
            Assert.isTrue(selSampleTypes.size() == selSampleTypeIds.size(),
                "problem with sample type selections");
        }
        containerType.setSampleTypeCollection(selSampleTypes);
    }

    private void saveChildContainerTypes() throws Exception {
        Collection<ContainerType> selContainerTypes = new HashSet<ContainerType>();
        List<Integer> selContainerTypeIds = new ArrayList<Integer>();
        if (hasContainers.getSelection()) {
            selContainerTypeIds = childContainerTypesMultiSelect.getSelected();
            if (allContainerTypes != null) {
                for (ContainerType containerType : allContainerTypes) {
                    int id = containerType.getId();
                    if (selContainerTypeIds.indexOf(id) >= 0) {
                        selContainerTypes.add(containerType);
                    }
                }
            }
        }

        Collection<ContainerType> children = containerType
            .getChildContainerTypeCollection();
        List<Integer> missing = new ArrayList<Integer>();
        if (children != null) {
            for (ContainerType child : children) {
                int id = child.getId();
                if (selContainerTypeIds.indexOf(id) < 0) {
                    missing.add(id);
                }
            }
        }

        if (missing.size() == 0 || HQLSafeToRemove(missing)) {
            containerType.setChildContainerTypeCollection(selContainerTypes);
        } else {
            BioBankPlugin
                .openError(
                    "ContainerType Removal Failed",
                    "Unable to remove child type. This parent/child relationship exists in storage. Remove all instances before attempting to delete a child type.");
        }

    }

    private boolean HQLSafeToChange() throws ApplicationException {
        String queryString = "select c.containerType from "
            + Container.class.getName() + " as c where c.containerType.id=?)";
        List<Object> params = new ArrayList<Object>();
        params.add(containerType.getId());
        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = appService.query(c);
        if (results.size() == 0)
            return true;
        else {
            containerType = (ContainerType) results.get(0);
            return false;
        }
    }

    private boolean HQLSafeToRemove(List<Integer> missing)
        throws ApplicationException {
        String queryString = "from "
            + ContainerPosition.class.getName()
            + " as cp inner join cp.parentContainer as cparent"
            + " where cparent.containerType.id=? and cp.container.containerType.id in (select id from "
            + ContainerType.class.getName() + " as ct where ct.id=?";
        List<Object> params = new ArrayList<Object>();
        params.add(containerType.getId());
        params.add(missing.get(0));
        for (int i = 1; i < missing.size(); i++) {
            queryString += "OR ct.id=?";
            params.add(missing.get(i));
        }
        queryString += ")";

        HQLCriteria c = new HQLCriteria(queryString);
        c.setParameters(params);
        List<Object> results = appService.query(c);
        if (results.size() == 0)
            return true;
        else
            return false;
    }

    private boolean checkContainerTypeNameUnique() throws ApplicationException {
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.ContainerType as st "
                + "inner join fetch st.site " + "where st.site.id='"
                + site.getId() + "' " + "and st.name = '"
                + containerType.getName() + "'");

        List<Object> results = appService.query(c);
        if (results.size() == 0)
            return true;

        BioBankPlugin.openAsyncError("Site Name Problem",
            "A storage type with name \"" + containerType.getName()
                + "\" already exists.");
        return false;
    }

    @Override
    public void cancelForm() {

    }

    @Override
    public String getNextOpenedFormID() {
        return ContainerTypeViewForm.ID;
    }
}
