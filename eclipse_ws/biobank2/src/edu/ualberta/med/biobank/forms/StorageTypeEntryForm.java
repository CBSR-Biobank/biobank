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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.helpers.GetHelper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.MultiSelect;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StorageTypeEntryForm extends BiobankEntryForm {
	public static final String ID = "edu.ualberta.med.biobank.forms.StorageTypeEntryForm";

	private static final String MSG_NEW_STORAGE_TYPE_OK = "Creating a new storage type.";

	private static final String MSG_STORAGE_TYPE_OK = "Editing an existing storage type.";

	private static final String MSG_NO_STORAGE_TYPE_NAME = "Storage type must have a name";

	private static final String MSG_NO_DIMENSION_LABEL = "Dimension labels must be assigned";

	static Logger log4j = Logger.getLogger(SessionManager.class.getName());

	private StorageTypeAdapter storageTypeAdapter;

	private StorageType storageType;

	private Capacity capacity;

	private Button submit;

	private MultiSelect samplesMultiSelect;

	private MultiSelect childStorageTypesMultiSelect;

	private List<SampleType> allSampleDerivTypes;

	private Collection<StorageType> allStorageTypes;

	private Site site;

	public StorageTypeEntryForm() {
		super();
	}

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");

		storageTypeAdapter = (StorageTypeAdapter) node;
		appService = storageTypeAdapter.getAppService();
		storageType = storageTypeAdapter.getStorageType();
		site = ((SiteAdapter) storageTypeAdapter.getParent().getParent())
			.getSite();
		allStorageTypes = site.getStorageTypeCollection();

		if (storageType.getId() == null) {
			setPartName("New Storage Type");
			capacity = new Capacity();
		} else {
			setPartName("Storage Type " + storageType.getName());
			capacity = storageType.getCapacity();
		}
	}

	@Override
	protected void createFormContent() {
		form.setText("Storage Type Information");
		form.setMessage(getOkMessage(), IMessageProvider.NONE);
		form.getBody().setLayout(new GridLayout(1, false));

		createStorageTypeSection();
		createDimensionsSection();
		createSampleDerivTypesSection();
		createChildStorageTypesSection();
		createButtons();
	}

	protected void createStorageTypeSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Name", null,
			PojoObservables.observeValue(storageType, "name"),
			NonEmptyString.class, MSG_NO_STORAGE_TYPE_NAME);

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
			"Default Temperature\n(Celcius)", null, PojoObservables
				.observeValue(storageType, "defaultTemperature"),
			DoubleNumber.class, "Default temperature is not a valid number");

		createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
			"Activity Status", FormConstants.ACTIVITY_STATUS, PojoObservables
				.observeValue(storageType, "activityStatus"), null, null);

		Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.MULTI, "Comments", null, PojoObservables.observeValue(
				storageType, "comment"), null, null);
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

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
			"Dimension One Label", null, PojoObservables.observeValue(
				storageType, "dimensionOneLabel"), NonEmptyString.class,
			MSG_NO_DIMENSION_LABEL);

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
			"Dimension One Capacity", null, PojoObservables.observeValue(
				capacity, "dimensionOneCapacity"), IntegerNumber.class,
			"Dimension one capacity is not a valid number");

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
			"Dimension Two Label", null, PojoObservables.observeValue(
				storageType, "dimensionTwoLabel"), NonEmptyString.class,
			MSG_NO_DIMENSION_LABEL);

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
			"Dimension Two Capacity", null, PojoObservables.observeValue(
				capacity, "dimensionTwoCapacity"), IntegerNumber.class,
			"Dimension two capacity is not a valid nubmer");
	}

	private void createSampleDerivTypesSection() {
		Composite client = createSectionWithClient("Contains Sample Derivative Types");
		GridLayout layout = (GridLayout) client.getLayout();
		layout.numColumns = 2;

		Collection<SampleType> stSamplesTypes = storageType
			.getSampleTypeCollection();

		GetHelper<SampleType> helper = new GetHelper<SampleType>();

		allSampleDerivTypes = helper.getModelObjects(appService,
			SampleType.class);

		samplesMultiSelect = new MultiSelect(client, SWT.NONE,
			"Selected Sample Derivatives", "Available Sample Derivatives", 100);
		samplesMultiSelect.adaptToToolkit(toolkit);

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

	private void createChildStorageTypesSection() {
		Composite client = createSectionWithClient("Contains Storage Types");
		GridLayout layout = (GridLayout) client.getLayout();
		layout.numColumns = 2;

		childStorageTypesMultiSelect = new MultiSelect(client, SWT.NONE,
			"Selected Storage Types", "Available Storage Types", 100);
		childStorageTypesMultiSelect.adaptToToolkit(toolkit);

		ListOrderedMap availStorageTypes = new ListOrderedMap();
		List<Integer> selChildStorageTypes = new ArrayList<Integer>();

		Collection<StorageType> childStorageTypes = storageType
			.getChildStorageTypeCollection();
		if (childStorageTypes != null) {
			for (StorageType childStorageType : childStorageTypes) {
				selChildStorageTypes.add(childStorageType.getId());
			}
		}

		int myId = 0;
		if (storageType.getId() != null) {
			myId = storageType.getId();
		}

		if (allStorageTypes != null)
			for (StorageType storageType : allStorageTypes) {
				int id = storageType.getId();
				if (myId != id) {
					availStorageTypes.put(id, storageType.getName());
				}
			}
		childStorageTypesMultiSelect.addSelections(availStorageTypes,
			selChildStorageTypes);
	}

	protected void createButtons() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 2;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);

		submit = toolkit.createButton(client, "Submit", SWT.PUSH);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().saveEditor(StorageTypeEntryForm.this,
						false);
			}
		});
	}

	@Override
	protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage(getOkMessage(), IMessageProvider.NONE);
			submit.setEnabled(true);
		} else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
			submit.setEnabled(false);
		}
	}

	private String getOkMessage() {
		if (storageType.getId() == null) {
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

		if ((storageType.getId() == null) && !checkStorageTypeNameUnique()) {
			setDirty(true);
			return;
		}

		saveSampleTypes();
		saveChildStorageTypes();
		saveCapacity();

		// associate the storage type to it's site
		storageType.setSite(site);

		if ((storageType.getId() == null) || (storageType.getId() == 0)) {
			query = new InsertExampleQuery(storageType);
		} else {
			query = new UpdateExampleQuery(storageType);
		}

		result = appService.executeQuery(query);
		storageType = (StorageType) result.getObjectResult();
		if (allStorageTypes == null) {
			allStorageTypes = new ArrayList<StorageType>();
		}
		allStorageTypes.add(storageType);
		site.setStorageTypeCollection(allStorageTypes);

		storageTypeAdapter.getParent().performExpand();
		getSite().getPage().closeEditor(this, false);
	}

	private void saveCapacity() throws Exception {
		SDKQuery query;
		SDKQueryResult result;

		Integer id = capacity.getId();

		if ((id == null) || (id == 0)) {
			query = new InsertExampleQuery(capacity);
		} else {
			query = new UpdateExampleQuery(capacity);
		}

		result = appService.executeQuery(query);
		storageType.setCapacity((Capacity) result.getObjectResult());
	}

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
		storageType.setSampleTypeCollection(selSampleTypes);
	}

	private void saveChildStorageTypes() {
		List<Integer> selStorageTypeIds = childStorageTypesMultiSelect
			.getSelected();
		Set<StorageType> selStorageTypes = new HashSet<StorageType>();
		if (allStorageTypes != null) {
			for (StorageType storageType : allStorageTypes) {
				int id = storageType.getId();
				if (selStorageTypeIds.indexOf(id) >= 0) {
					selStorageTypes.add(storageType);
				}
			}
		}
		Assert.isTrue(selStorageTypes.size() == selStorageTypeIds.size(),
			"problem with sample type selections");
		storageType.setChildStorageTypeCollection(selStorageTypes);
	}

	private boolean checkStorageTypeNameUnique() throws ApplicationException {
		WritableApplicationService appService = storageTypeAdapter
			.getAppService();
		HQLCriteria c = new HQLCriteria(
			"from edu.ualberta.med.biobank.model.StorageType as st "
					+ "inner join fetch st.site " + "where st.site.id='"
					+ site.getId() + "' " + "and st.name = '"
					+ storageType.getName() + "'");

		List<Object> results = appService.query(c);
		if (results.size() == 0)
			return true;

		BioBankPlugin.openAsyncError("Site Name Problem",
			"A storage type with name \"" + storageType.getName()
					+ "\" already exists.");
		return false;
	}
}
