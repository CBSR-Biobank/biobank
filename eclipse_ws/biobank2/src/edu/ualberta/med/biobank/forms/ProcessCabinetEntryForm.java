package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.forms.listener.CancelSubmitKeyListener;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.validators.CabinetPositionCodeValidator;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.ViewStorageContainerWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.example.InsertExampleQuery;

public class ProcessCabinetEntryForm extends BiobankEntryForm implements
		CancelConfirmForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.ProcessCabinetEntryForm";

	private PatientVisitAdapter pvAdapter;
	private PatientVisit patientVisit;

	private Label cabinetLabel;
	private Label drawerLabel;
	private ViewStorageContainerWidget cabinetWidget;
	private CabinetDrawerWidget drawerWidget;

	private ComboViewer comboViewerSampleTypes;
	private Text inventoryIdText;
	private Text positionText;
	private Button showPosition;

	private Text confirmCancelText;
	private Button cancel;
	private Button submit;

	private IObservableValue cabinetPosition = new WritableValue("",
		String.class);
	private IObservableValue resultShown = new WritableValue(Boolean.FALSE,
		Boolean.class);
	private IObservableValue selectedSampleType = new WritableValue("",
		String.class);

	private StorageType cabinetType;
	private Sample sample = new Sample();
	private StorageContainer cabinet;
	private StorageContainer drawer;
	private StorageContainer bin;

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");

		Assert
			.isTrue((node instanceof PatientVisitAdapter),
				"Invalid editor input: object of type "
						+ node.getClass().getName());

		pvAdapter = (PatientVisitAdapter) node;
		patientVisit = pvAdapter.getPatientVisit();
		appService = pvAdapter.getAppService();

		setPartName("Process cabinet sample for "
				+ patientVisit.getPatient().getNumber());
	}

	@Override
	protected void createFormContent() {
		form.setText("Process cabinet samples for patient "
				+ patientVisit.getPatient().getNumber() + " for visit "
				+ patientVisit.getNumber());

		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);

		createLocationSection();
		addSeparator();
		createFieldsSection();
		addSeparator();
		createButtonsSection();

		addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
			resultShown, "Show results to check values");
	}

	private void createLocationSection() {
		// get storage type Cabinet from database
		cabinetType = ModelUtils.getCabinetType(appService);
		if (cabinetType == null) {
			BioBankPlugin.openAsyncError("Cabinet error",
				"Cannot find a storage type for cabinet !");
			form.setEnabled(false);
		}

		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		client.setLayout(layout);
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		client.setLayoutData(gd);
		toolkit.paintBordersFor(client);

		cabinetLabel = toolkit.createLabel(client, "Cabinet");
		drawerLabel = toolkit.createLabel(client, "Drawer");

		cabinetWidget = new ViewStorageContainerWidget(client);
		toolkit.adapt(cabinetWidget);
		cabinetWidget.setGridSizes(4, 1, 150, 150);
		cabinetWidget.setFirstColSign('A');
		cabinetWidget.setShowColumnFirst(true);
		GridData gdDrawer = new GridData();
		gdDrawer.verticalAlignment = SWT.TOP;
		cabinetWidget.setLayoutData(gdDrawer);

		drawerWidget = new CabinetDrawerWidget(client);
		toolkit.adapt(drawerWidget);
		GridData gdBin = new GridData();
		gdBin.widthHint = CabinetDrawerWidget.WIDTH;
		gdBin.heightHint = CabinetDrawerWidget.HEIGHT;
		gdBin.verticalSpan = 2;
		drawerWidget.setLayoutData(gdBin);

	}

	private void createFieldsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		GridData gd = new GridData();
		gd.widthHint = 200;
		client.setLayoutData(gd);
		toolkit.paintBordersFor(client);

		Combo comboSampleType = (Combo) createBoundWidgetWithLabel(client,
			Combo.class, SWT.NONE, "Sample type", new String[0],
			selectedSampleType, NonEmptyString.class,
			"A sample type should be selected");
		comboViewerSampleTypes = new ComboViewer(comboSampleType);
		comboViewerSampleTypes.setContentProvider(new ArrayContentProvider());
		comboViewerSampleTypes.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				SampleType st = (SampleType) element;
				return st.getName();
			}
		});
		if (cabinetType != null) {
			comboViewerSampleTypes.setInput(cabinetType
				.getSampleTypeCollection());
		}

		inventoryIdText = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.NONE, "Inventory ID", new String[0], PojoObservables
				.observeValue(sample, "inventoryId"), NonEmptyString.class,
			"Enter Inventory Id (eg cdfg or DYUO)");
		inventoryIdText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

		positionText = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.NONE, "Position", new String[0], cabinetPosition,
			CabinetPositionCodeValidator.class,
			"Enter a position (eg 01AA01AB)");
		positionText.removeKeyListener(keyListener);
		positionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

		showPosition = toolkit.createButton(client, "Show Position", SWT.PUSH);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.CENTER;
		showPosition.setLayoutData(gd);
		showPosition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showPositionResult();
			}
		});
	}

	private void createButtonsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);

		confirmCancelText = toolkit.createText(client, "");
		confirmCancelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridData gd = new GridData();
		gd.widthHint = 100;
		confirmCancelText.setLayoutData(gd);
		confirmCancelText.addKeyListener(new CancelSubmitKeyListener(this));

		cancel = toolkit.createButton(client, "Cancel", SWT.PUSH);
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelForm();
			}
		});

		submit = toolkit.createButton(client, "Submit", SWT.PUSH);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doSaveInternal();
			}
		});
		confirmCancelText.addKeyListener(new CancelSubmitKeyListener(this));
	}

	protected void showPositionResult() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					String positionString = cabinetPosition.getValue()
						.toString();

					StorageContainer sc = ModelUtils
						.getStorageContainerWithBarcode(appService,
							positionString);
					if (sc == null) {
						SamplePosition sp = getSamplePosition(positionString);
						if (sp == null) {
							BioBankPlugin.openError("Cabinet error",
								"Parent containers not found");
							return;
						}
						Point drawerPosition = new Point(
							drawer.getLocatedAtPosition()
								.getPositionDimensionOne() - 1, drawer
								.getLocatedAtPosition()
								.getPositionDimensionTwo() - 1);
						cabinetWidget.setSelectedBox(drawerPosition);
						cabinetLabel.setText("Cabinet " + cabinet.getBarcode());
						drawerWidget.setSelectedBin(bin.getLocatedAtPosition()
							.getPositionDimensionTwo());
						drawerLabel.setText("Drawer " + drawer.getBarcode());

						sp.setSample(sample);
						sample.setSamplePosition(sp);

						IStructuredSelection stSelection = (IStructuredSelection) comboViewerSampleTypes
							.getSelection();
						sample.setSampleType((SampleType) stSelection
							.getFirstElement());

						resultShown.setValue(Boolean.TRUE);
					} else {
						BioBankPlugin.openError("Cabinet error",
							"This position is already in use");
					}
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					e.printStackTrace();
				}
				setDirty(true);
			}

		});
	}

	protected SamplePosition getSamplePosition(String positionString)
			throws ApplicationException {
		int end = 2;
		String cabinetString = positionString.substring(0, end);
		cabinet = ModelUtils.getStorageContainerWithBarcode(appService,
			cabinetString);
		if (cabinet == null) {
			return null;
		}
		end += 2;
		String drawerString = positionString.substring(0, end);
		drawer = ModelUtils.getStorageContainerWithBarcode(appService,
			drawerString);
		if (drawer == null
				|| !drawer.getLocatedAtPosition().getParentContainer().getId()
					.equals(cabinet.getId())) {
			return null;
		}
		end += 2;
		String binString = positionString.substring(0, end);
		bin = ModelUtils.getStorageContainerWithBarcode(appService, binString);
		if (bin == null
				|| !bin.getLocatedAtPosition().getParentContainer().getId()
					.equals(drawer.getId())) {
			return null;
		}
		SamplePosition sp = new SamplePosition();
		sp.setStorageContainer(bin);
		char letter = positionString.substring(6, 7).toCharArray()[0];
		// positions start at A
		sp.setPositionDimensionOne(letter - 'A' + 1);
		letter = positionString.substring(7).toCharArray()[0];
		sp.setPositionDimensionTwo(letter - 'A' + 1);
		return sp;
	}

	protected void cancelForm() {
		sample = null;
		cabinet = null;
		drawer = null;
		bin = null;
		cabinetWidget.setSelectedBox(null);
		drawerWidget.setSelectedBin(0);
		resultShown.setValue(Boolean.FALSE);
		selectedSampleType.setValue("");
		inventoryIdText.setText("");
		positionText.setText("");
	}

	@Override
	protected void saveForm() throws Exception {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					sample.setPatientVisit(patientVisit);
					appService.executeQuery(new InsertExampleQuery(sample));
					if (BioBankPlugin.isAskPrint()) {
						boolean doPrint = MessageDialog.openQuestion(PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), "Print",
							"Do you want to print information ?");
						if (doPrint) {
							// FIXME implement print functionnality
						}
					}
					getSite().getPage().closeEditor(
						ProcessCabinetEntryForm.this, false);
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					SessionManager.getLogger().error(
						"Error when saving cabinet position", e);
				}
			}
		});
	}

	@Override
	protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage("Processing samples.", IMessageProvider.NONE);
			submit.setEnabled(true);
			showPosition.setEnabled(true);
		} else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
			submit.setEnabled(false);
			if (status.getMessage() != null
					&& status.getMessage().contains("check values")) {
				showPosition.setEnabled(true);
			} else {
				showPosition.setEnabled(false);
			}
		}
	}

	// CancelConfirmForm implementation

	public boolean isConfirmEnabled() {
		return submit.isEnabled();
	}

	public void confirm() throws Exception {
		saveForm();
	}

	public void cancel() throws Exception {
		cancelForm();
	}
}
