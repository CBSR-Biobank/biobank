package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.helpers.GetHelper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.MultiSelect;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectListener;
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

    private Button sampleBox;

    private MultiSelect samplesMultiSelect;

    private MultiSelect childContainerTypesMultiSelect;

    private List<SampleType> allSampleDerivTypes;

    private Collection<ContainerType> allContainerTypes;

    private Site site;

    private MultiSelectListener multiSelectListener;

    private ComboViewer labelingSchemeComboViewer;

    public ContainerTypeEntryForm() {
        super();
        multiSelectListener = new MultiSelectListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
                if (samplesMultiSelect.getSelected().size() == 0)
                    sampleBox.setEnabled(true);
                else
                    sampleBox.setEnabled(false);
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
        site = ((SiteAdapter) containerTypeAdapter
            .getParentFromClass(SiteAdapter.class)).getSite();
        allContainerTypes = site.getContainerTypeCollection();

        String tabName;
        if (containerType.getId() == null) {
            tabName = "New Container Type";
            capacity = new Capacity();
        } else {
            tabName = "Container Type " + containerType.getName();
            capacity = containerType.getCapacity();
        }

        setPartName(tabName);
    }

    @Override
    protected void createFormContent() {
        form.setText("Container Type Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));

        createContainerTypeSection();
        createDimensionsSection();
        createSampleDerivTypesSection();
        createChildContainerTypesSection();
        createButtons();
    }

    protected void createContainerTypeSection() {
        try {
            Composite client = toolkit.createComposite(form.getBody());
            GridLayout layout = new GridLayout(2, false);
            layout.horizontalSpacing = 10;
            client.setLayout(layout);
            client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            toolkit.paintBordersFor(client);
            createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Name",
                null, PojoObservables.observeValue(containerType, "name"),
                NonEmptyString.class, MSG_NO_CONTAINER_TYPE_NAME);

            createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
                "Short Name", null, PojoObservables.observeValue(containerType,
                    "nameShort"), NonEmptyString.class,
                MSG_NO_CONTAINER_TYPE_NAME_SHORT);

            createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
                "Default Temperature\n(Celcius)", null, PojoObservables
                    .observeValue(containerType, "defaultTemperature"),
                DoubleNumber.class, "Default temperature is not a valid number");

            List<ContainerLabelingScheme> schemes = appService.search(
                ContainerLabelingScheme.class, new ContainerLabelingScheme());

            labelingSchemeComboViewer = createComboViewerWithNoSelectionValidator(
                client, "Child Labeling Scheme", schemes,
                MSG_CHILD_LABELING_SCHEME_EMPTY);
            ContainerLabelingScheme currentScheme = containerType
                .getChildLabelingScheme();
            if (currentScheme != null) {
                for (ContainerLabelingScheme scheme : schemes) {
                    if (currentScheme.getId().equals(scheme.getId())) {
                        currentScheme = scheme;
                        break;
                    }
                }
                labelingSchemeComboViewer.setSelection(new StructuredSelection(
                    currentScheme));
            }

            createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
                "Activity Status", FormConstants.ACTIVITY_STATUS,
                PojoObservables.observeValue(containerType, "activityStatus"),
                null, null);

            Text comment = (Text) createBoundWidgetWithLabel(client,
                Text.class, SWT.MULTI, "Comments", null, PojoObservables
                    .observeValue(containerType, "comment"), null, null);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.heightHint = 40;
            comment.setLayoutData(gd);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    private void createDimensionsSection() {
        Composite client = createSectionWithClient("Default Capacity");

        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Dimension One Capacity", null, PojoObservables.observeValue(
                capacity, "dimensionOneCapacity"), IntegerNumber.class,
            "Dimension one capacity is not a valid number");

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Dimension Two Capacity", null, PojoObservables.observeValue(
                capacity, "dimensionTwoCapacity"), IntegerNumber.class,
            "Dimension two capacity is not a valid nubmer");
    }

    private void createSampleDerivTypesSection() {
        Composite client = createSectionWithClient("Contains Sample Derivative Types");
        sampleBox = new Button(client, SWT.CHECK);
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        sampleBox.setText("Holds Samples");
        sampleBox.setSelection(true);
        sampleBox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                samplesMultiSelect.setEnabled(!samplesMultiSelect.getEnabled());
                samplesMultiSelect.setVisible(!samplesMultiSelect.getVisible());
                form.reflow(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
            | GridData.GRAB_HORIZONTAL);
        sampleBox.setLayoutData(gd);
        Collection<SampleType> stSamplesTypes = containerType
            .getSampleTypeCollection();

        GetHelper<SampleType> helper = new GetHelper<SampleType>();

        allSampleDerivTypes = helper.getModelObjects(appService,
            SampleType.class);

        samplesMultiSelect = new MultiSelect(client, SWT.NONE,
            "Selected Sample Derivatives", "Available Sample Derivatives", 100);
        samplesMultiSelect.adaptToToolkit(toolkit);
        samplesMultiSelect.addSelectionChangedListener(multiSelectListener);
        ListOrderedMap availSampleDerivTypes = new ListOrderedMap();
        List<Integer> selSampleDerivTypes = new ArrayList<Integer>();

        if (stSamplesTypes != null) {
            for (SampleType sampleType : stSamplesTypes) {
                selSampleDerivTypes.add(sampleType.getId());
            }
        }

        for (SampleType sampleType : allSampleDerivTypes) {
            availSampleDerivTypes.put(sampleType.getId(), sampleType
                .getNameShort());
        }
        samplesMultiSelect.addSelections(availSampleDerivTypes,
            selSampleDerivTypes);
    }

    private void createChildContainerTypesSection() {
        Composite client = createSectionWithClient("Contains Container Types");
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        Button topBox = new Button(client, SWT.CHECK);
        topBox.setText("Top Level Container");
        GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
            | GridData.GRAB_HORIZONTAL);
        topBox.setLayoutData(gd);
        topBox.setSelection(false);
        topBox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                // topLevel = true;
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        childContainerTypesMultiSelect = new MultiSelect(client, SWT.NONE,
            "Selected Container Types", "Available Container Types", 100);
        childContainerTypesMultiSelect.adaptToToolkit(toolkit);
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

        int myId = 0;
        if (containerType.getId() != null) {
            myId = containerType.getId();
        }

        if (allContainerTypes != null)
            for (ContainerType containerType : allContainerTypes) {
                int id = containerType.getId();
                if (myId != id) {
                    availContainerTypes.put(id, containerType.getName());
                }
            }
        childContainerTypesMultiSelect.addSelections(availContainerTypes,
            selChildContainerTypes);

    }

    protected void createButtons() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.numColumns = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);

        initConfirmButton(client, false, true);
        // initConfirmAddButton(client, false, true);
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
        // saveCapacity();
        containerType.setCapacity(capacity);

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
        if (allContainerTypes == null) {
            allContainerTypes = new ArrayList<ContainerType>();
        }
        allContainerTypes.add(containerType);
        site.setContainerTypeCollection(allContainerTypes);
        containerTypeAdapter.setContainerType(containerType);
        containerTypeAdapter.getParent().performExpand();

    }

    // private void saveCapacity() throws Exception {
    // SDKQuery query;
    // SDKQueryResult result;
    //
    // Integer id = capacity.getId();
    //
    // if ((id == null) || (id == 0)) {
    // query = new InsertExampleQuery(capacity);
    // } else {
    // query = new UpdateExampleQuery(capacity);
    // }
    //
    // result = appService.executeQuery(query);
    // containerType.setCapacity((Capacity) result.getObjectResult());
    // }

    private void saveSampleTypes() {
        List<Integer> selSampleTypeIds = samplesMultiSelect.getSelected();
        Set<SampleType> selSampleTypes = new HashSet<SampleType>();
        for (SampleType sampleType : allSampleDerivTypes) {
            int id = sampleType.getId();
            if (selSampleTypeIds.indexOf(id) >= 0) {
                selSampleTypes.add(sampleType);
            }

        }
        Assert.isTrue(selSampleTypes.size() == selSampleTypeIds.size(),
            "problem with sample type selections");
        containerType.setSampleTypeCollection(selSampleTypes);
    }

    private void saveChildContainerTypes() throws Exception {
        List<Integer> selContainerTypeIds = childContainerTypesMultiSelect
            .getSelected();

        Collection<ContainerType> selContainerTypes = new HashSet<ContainerType>();

        if (allContainerTypes != null) {
            for (ContainerType containerType : allContainerTypes) {
                int id = containerType.getId();
                if (selContainerTypeIds.indexOf(id) >= 0) {
                    selContainerTypes.add(containerType);
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

    private boolean HQLSafeToRemove(List<Integer> missing)
        throws ApplicationException {
        String missingString = " where cp.container.id='"
            + missing.get(0).toString() + "'";
        for (int i = 1; i < missing.size(); i++) {
            missingString += "OR cp.container.id='" + missing.get(i).toString()
                + "'";
        }
        HQLCriteria c = new HQLCriteria("from "
            + ContainerPosition.class.getName()
            + " as cp inner join on cp.parentContainer.id='"
            + containerType.getId() + "'" + missingString);
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
        // TODO Auto-generated method stub

    }

    @Override
    public String getNextOpenedFormID() {
        return ContainerTypeViewForm.ID;
    }
}
