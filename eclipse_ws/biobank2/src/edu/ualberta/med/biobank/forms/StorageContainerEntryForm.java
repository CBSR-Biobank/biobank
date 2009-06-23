package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageContainerAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.BiobankContentProvider;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StorageContainerEntryForm extends BiobankEntryForm {
	public static final String ID = "edu.ualberta.med.biobank.forms.StorageContainerEntryForm";

	public static final String MSG_STORAGE_CONTAINER_NEW_OK = "Creating a new storage container.";

	public static final String MSG_STORAGE_CONTAINER_OK = "Editing an existing storage container.";

	public static final String MSG_CONTAINER_NAME_EMPTY = "Storage container must have a name";

	public static final String MSG_STORAGE_TYPE_EMPTY = "Storage container must have a container type";

	public static final String MSG_INVALID_POSITION = "Position is empty or not a valid number";

	private StorageContainerAdapter storageContainerAdapter;

	private StorageContainer storageContainer;

	private ContainerPosition position;

	private Site site;

	private Text tempWidget;

	private Label dimensionOneLabel;

	private Label dimensionTwoLabel;

	private Button submit;

	private StorageType currentStorageType;

	private ComboViewer storageTypeComboViewer;

	private IObservableValue typeSelected = new WritableValue("", String.class);

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");

		storageContainerAdapter = (StorageContainerAdapter) node;
		appService = storageContainerAdapter.getAppService();
		storageContainer = storageContainerAdapter.getStorageContainer();
		site = storageContainerAdapter.getSite();
		position = storageContainer.getLocatedAtPosition();

		if (storageContainer.getId() == null) {
			setPartName("Storage Container");
		} else {
			setPartName("Storage Container " + storageContainer.getName());
		}
	}

	@Override
	protected void createFormContent() {
		currentStorageType = storageContainer.getStorageType();

		form.setText("Storage Container");
		form.getBody().setLayout(new GridLayout(1, false));

		createContainerSection();
		createButtonsSection();
	}

	private void createContainerSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Name", null,
			PojoObservables.observeValue(storageContainer, "name"),
			NonEmptyString.class, MSG_CONTAINER_NAME_EMPTY);

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Barcode",
			null, PojoObservables.observeValue(storageContainer, "barcode"),
			null, null);

		createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
			"Activity Status", FormConstants.ACTIVITY_STATUS, PojoObservables
				.observeValue(storageContainer, "activityStatus"), null, null);

		Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.MULTI, "Comments", null, PojoObservables.observeValue(
				storageContainer, "comment"), null, null);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 40;
		comment.setLayoutData(gd);

		createStorageTypesSection(client);
	}

	private void createStorageTypesSection(Composite client) {
		Collection<StorageType> storageTypes = new ArrayList<StorageType>();
		if (position.getParentContainer() == null) {
			storageTypes = site.getStorageTypeCollection();
		} else {
			storageTypes = position.getParentContainer().getStorageType()
				.getChildStorageTypeCollection();
		}
		StorageType[] arr = new StorageType[storageTypes.size()];
		int count = 0;
		for (StorageType st : storageTypes) {
			arr[count] = st;
			if ((currentStorageType != null)
					&& currentStorageType.getId().equals(st.getId())) {
				currentStorageType = st;
			}
			count++;
		}
		Label storageTypeLabel = toolkit.createLabel(client, "Container Type:",
			SWT.LEFT);

		storageTypeComboViewer = new ComboViewer(client, SWT.READ_ONLY);
		storageTypeComboViewer.setContentProvider(new BiobankContentProvider());
		storageTypeComboViewer.setLabelProvider(new BiobankLabelProvider());
		storageTypeComboViewer.setInput(arr);
		if (currentStorageType != null) {
			storageTypeComboViewer.setSelection(new StructuredSelection(
				currentStorageType));
		}

		Combo combo = storageTypeComboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		storageTypeComboViewer
			.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
					StorageType storageType = (StorageType) selection
						.getFirstElement();
					Double temp = storageType.getDefaultTemperature();
					if (temp == null) {
						tempWidget.setText("");
					} else {
						tempWidget.setText(temp.toString());
					}
					setDirty(true);
				}
			});
		bindStorageTypeCombo(storageTypeLabel, combo);

		tempWidget = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.NONE, "Temperature (Celcius)", null, PojoObservables
				.observeValue(storageContainer, "temperature"),
			DoubleNumber.class, "Default temperature is not a valid number");

		createLocationSection();
	}

	private void createLocationSection() {
		StorageContainer parentContainer = position.getParentContainer();
		if (parentContainer != null) {
			String dim1Label = null, dim2Label = null;
			Integer dim1Max = null, dim2Max = null;

			Composite locationComposite = createSectionWithClient("Location");
			dim1Label = parentContainer.getStorageType().getDimensionOneLabel();
			dim2Label = parentContainer.getStorageType().getDimensionTwoLabel();

			Capacity capacity = parentContainer.getStorageType().getCapacity();
			if (capacity != null) {
				dim1Max = capacity.getDimensionOneCapacity();
				dim2Max = capacity.getDimensionTwoCapacity();
				if (dim1Max != null) {
					dim1Label += "\n(1 - " + dim1Max + ")";
				}
				if (dim2Max != null) {
					dim2Label += "\n(1 - " + dim2Max + ")";
				}
			}

			// could be that the dimension labels are not assigned in the
			// database objects
			if (dim1Label == null) {
				dim1Label = "Dimension 1";
				dim2Label = "Dimension 2";
			}

			dimensionOneLabel = toolkit.createLabel(locationComposite,
				dim1Label + ":", SWT.LEFT);

			IntegerNumber validator = new IntegerNumber(MSG_INVALID_POSITION,
				FormUtils.createDecorator(dimensionOneLabel,
					MSG_INVALID_POSITION), false);

			createBoundWidget(locationComposite, Text.class, SWT.NONE, null,
				PojoObservables.observeValue(position, "positionDimensionOne"),
				validator);

			dimensionTwoLabel = toolkit.createLabel(locationComposite,
				dim2Label + ":", SWT.LEFT);

			validator = new IntegerNumber(MSG_INVALID_POSITION, FormUtils
				.createDecorator(dimensionTwoLabel, MSG_INVALID_POSITION),
				false);

			createBoundWidget(locationComposite, Text.class, SWT.NONE, null,
				PojoObservables.observeValue(position, "positionDimensionTwo"),
				validator);
		}
	}

	private void createButtonsSection() {
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
					.getActivePage().saveEditor(StorageContainerEntryForm.this,
						false);
			}
		});
	}

	private void bindStorageTypeCombo(Label label, Combo combo) {
		IValidator validator = createValidator(NonEmptyString.class, FormUtils
			.createDecorator(label, MSG_STORAGE_TYPE_EMPTY),
			MSG_STORAGE_TYPE_EMPTY);
		UpdateValueStrategy uvs = new UpdateValueStrategy();
		uvs.setAfterGetValidator(validator);

		dbc.bindValue(SWTObservables.observeSelection(combo), typeSelected,
			uvs, null);
	}

	private String getOkMessage() {
		if (storageContainer.getId() == null) {
			return MSG_STORAGE_CONTAINER_NEW_OK;
		}
		return MSG_STORAGE_CONTAINER_OK;
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

	@Override
	protected void saveForm() throws Exception {
		if ((storageContainer.getId() == null)
				&& !checkStorageContainerUnique()) {
			setDirty(true);
			return;
		}

		StorageType storageType = (StorageType) ((StructuredSelection) storageTypeComboViewer
			.getSelection()).getFirstElement();
		storageContainer.setStorageType(storageType);
		storageContainer.setLocatedAtPosition(position);
		storageContainer.setSite(site);

		SDKQuery query;
		if (storageContainer.getId() == null) {
			query = new InsertExampleQuery(storageContainer);
		} else {
			query = new UpdateExampleQuery(storageContainer);
		}

		SDKQueryResult result = appService.executeQuery(query);
		storageContainer = (StorageContainer) result.getObjectResult();

		storageContainerAdapter.getParent().performExpand();
		getSite().getPage().closeEditor(this, false);

	}

	// protected void savePosition() throws Exception {
	// if (position != null) {
	// SDKQuery query;
	// SDKQueryResult result;
	//
	// Integer id = position.getId();
	//
	// if ((id == null) || (id == 0)) {
	// query = new InsertExampleQuery(position);
	// } else {
	// query = new UpdateExampleQuery(position);
	// }
	//
	// result = appService.executeQuery(query);
	// storageContainer.setLocatedAtPosition((ContainerPosition) result
	// .getObjectResult());
	// }
	// }

	private boolean checkStorageContainerUnique() throws Exception {
		// FIXME set contraint directly into the model ?
		HQLCriteria c = new HQLCriteria(
			"from edu.ualberta.med.biobank.model.StorageContainer as sc "
					+ "inner join fetch sc.site " + "where sc.site.id='"
					+ site.getId() + "' " + "and (sc.name = '"
					+ storageContainer.getName() + "' " + "or sc.barcode = '"
					+ storageContainer.getBarcode() + "')");

		List<Object> results = appService.query(c);
		if (results.size() == 0)
			return true;

		BioBankPlugin.openAsyncError("Site Name Problem",
			"A storage container with name \"" + storageContainer.getName()
					+ "\" already exists.");
		return false;
	}
}
