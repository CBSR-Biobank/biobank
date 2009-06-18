package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleCellStatus;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.ScanCell;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.NodeSearchVisitor;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.validators.ScannerBarcodeValidator;
import edu.ualberta.med.biobank.widgets.ScanPaletteWidget;
import edu.ualberta.med.biobank.widgets.ViewStorageContainerWidget;
import edu.ualberta.med.biobank.wizard.ContainerChooserWizard;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class ProcessSamplesEntryForm extends BiobankEntryForm implements
		CancelConfirmForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.ProcessSamplesEntryForm";

	private ScanPaletteWidget paletteWidget;
	private ViewStorageContainerWidget hotelWidget;
	private ViewStorageContainerWidget freezerWidget;

	private Text plateToScanText;
	private Text paletteCodeText;
	private Button scanButton;
	private Text confirmCancelText;
	private Button cancelButton;
	private Button submitButton;
	private Button locateButton;

	private IObservableValue plateToScanValue = new WritableValue("",
		String.class);
	private IObservableValue paletteCodeValue = new WritableValue("",
		String.class);
	private IObservableValue scanLaunchedValue = new WritableValue(
		Boolean.FALSE, Boolean.class);
	private IObservableValue scanValidValue = new WritableValue(Boolean.TRUE,
		Boolean.class);

	private ScanCell[][] cells;

	private Study currentStudy;

	protected StorageContainer currentPalette;

	protected Sample[][] currentPaletteSamples;

	private SessionAdapter sessionAdapter;

	private Label freezerLabel;

	private Label paletteLabel;

	private Label hotelLabel;

	private Composite containersComposite;

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");

		Assert
			.isTrue((node instanceof SessionAdapter),
				"Invalid editor input: object of type "
						+ node.getClass().getName());

		sessionAdapter = (SessionAdapter) node;
		appService = node.getAppService();

		setPartName("Process samples");
	}

	@Override
	protected void createFormContent() {
		form.setText("Processing samples");

		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);

		createFieldsSection();

		createContainersSection();

		createButtonsSection();

		addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
			scanLaunchedValue, "Scanner should be launched");
		addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
			scanValidValue, "Error in scanning result");
	}

	private void createContainersSection() {
		containersComposite = toolkit.createComposite(form.getBody());
		GridLayout layout = getNeutralGridLayout();
		layout.numColumns = 2;
		containersComposite.setLayout(layout);
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		containersComposite.setLayoutData(gd);
		toolkit.paintBordersFor(containersComposite);

		showAllContainers(false);

		Composite freezerComposite = toolkit
			.createComposite(containersComposite);
		freezerComposite.setLayout(getNeutralGridLayout());
		GridData gdFreezer = new GridData();
		gdFreezer.horizontalSpan = 2;
		freezerComposite.setLayoutData(gdFreezer);
		freezerLabel = toolkit.createLabel(freezerComposite, "Freezer");
		freezerLabel.setLayoutData(new GridData());
		freezerWidget = new ViewStorageContainerWidget(freezerComposite);
		toolkit.adapt(freezerWidget);
		freezerWidget.setGridSizes(5, 10, ScanPaletteWidget.PALETTE_WIDTH, 100);

		Composite paletteComposite = toolkit
			.createComposite(containersComposite);
		paletteComposite.setLayout(getNeutralGridLayout());
		paletteComposite.setLayoutData(new GridData());
		paletteLabel = toolkit.createLabel(paletteComposite, "Palette");
		paletteWidget = new ScanPaletteWidget(paletteComposite);
		toolkit.adapt(paletteWidget);

		Composite hotelComposite = toolkit.createComposite(containersComposite);
		hotelComposite.setLayout(getNeutralGridLayout());
		hotelComposite.setLayoutData(new GridData());
		hotelLabel = toolkit.createLabel(hotelComposite, "Hotel");
		hotelWidget = new ViewStorageContainerWidget(hotelComposite);
		toolkit.adapt(hotelWidget);
		hotelWidget.setGridSizes(11, 1, 100,
			ScanPaletteWidget.PALETTE_HEIGHT_AND_LEGEND);
		hotelWidget.setFirstColSign(null);
		hotelWidget.setFirstRowSign(1);
	}

	private GridLayout getNeutralGridLayout() {
		GridLayout layout;
		layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		return layout;
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

		plateToScanText = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.NONE, "Plate to scan", new String[0], plateToScanValue,
			ScannerBarcodeValidator.class, "Enter a valid plate barcode");
		plateToScanText.removeKeyListener(keyListener);
		plateToScanText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

		paletteCodeText = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.NONE, "Palette barcode", new String[0], paletteCodeValue,
			NonEmptyString.class, "Enter palette barcode");
		paletteCodeText.removeKeyListener(keyListener);
		paletteCodeText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

		scanButton = toolkit.createButton(client, "Scan", SWT.PUSH);
		scanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scan();
			}
		});
	}

	private void createButtonsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);

		locateButton = toolkit
			.createButton(client, "Choose Location", SWT.PUSH);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		locateButton.setLayoutData(gd);
		locateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				chooseLocation();
			}
		});

		confirmCancelText = toolkit.createText(client, "");
		confirmCancelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gd = new GridData();
		gd.widthHint = 100;
		confirmCancelText.setLayoutData(gd);
		confirmCancelText.addKeyListener(new CancelSubmitKeyListener(this));

		cancelButton = toolkit.createButton(client, "Cancel", SWT.PUSH);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelForm();
			}
		});

		submitButton = toolkit.createButton(client, "Submit", SWT.PUSH);
		submitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doSaveInternal();
			}
		});
		confirmCancelText.addKeyListener(new CancelSubmitKeyListener(this));
	}

	protected void chooseLocation() {
		if (currentStudy == null) {
			BioBankPlugin.openError("Wizard Problem",
				"No study has been found on this palette");
			return;
		}
		ContainerChooserWizard wizard = new ContainerChooserWizard(currentStudy
			.getSite());
		WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
		dialog.open();
		initPalettePosition(wizard.getSelectedPosition());
	}

	private void initPalettePosition(ContainerPosition position) {
		currentPalette.setLocatedAtPosition(position);
		currentPalette.setName(paletteCodeValue.getValue().toString());
		currentPalette.setBarcode(paletteCodeValue.getValue().toString());
		showOnlyPalette(false);
		showPalettePosition(currentPalette);
		containersComposite.layout(true, true);
	}

	protected void scan() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					boolean showResult = getPaletteInformation();
					if (showResult) {
						// TODO launch real scanner
						System.out.println("Plate to scan : "
								+ BioBankPlugin.getDefault().getPlateNumber(
									plateToScanValue.getValue().toString()));
						cells = ScanCell.getRandomScanProcess();

						currentStudy = null;
						boolean result = true;
						for (int i = 0; i < cells.length; i++) { // rows
							for (int j = 0; j < cells[i].length; j++) { // columns
								Sample positionSample = null;
								if (currentPaletteSamples != null) {
									positionSample = currentPaletteSamples[i][j];
								}
								result = setStatus(cells[i][j], positionSample)
										&& result;
							}
						}
						scanValidValue.setValue(result);
						paletteWidget.setScannedElements(cells);
						showStudyInformation();
						scanLaunchedValue.setValue(true);
						setDirty(true);

						showAllContainers(true);

						confirmCancelText.setFocus();
					} else {
						showAllContainers(false);
					}
					form.layout(true, true);
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					SessionManager.getLogger().error("Error while scanning", e);
					scanValidValue.setValue(false);
				}
			}
		});
	}

	private void showAllContainers(boolean show) {
		((GridData) containersComposite.getLayoutData()).exclude = !show;
		containersComposite.setVisible(show);
	}

	private void showOnlyPalette(boolean show) {
		freezerLabel.getParent().setVisible(!show);
		((GridData) freezerLabel.getParent().getLayoutData()).exclude = show;
		hotelLabel.getParent().setVisible(!show);
		((GridData) hotelLabel.getParent().getLayoutData()).exclude = show;
		locateButton.setVisible(show);
		((GridData) locateButton.getLayoutData()).exclude = !show;
	}

	protected void showPalettePosition(StorageContainer palette) {
		ContainerPosition palettePosition = palette.getLocatedAtPosition();
		if (palettePosition != null) {
			StorageContainer hotelContainer = palettePosition
				.getParentContainer();
			ContainerPosition hotelPosition = hotelContainer
				.getLocatedAtPosition();
			StorageContainer freezerContainer = hotelPosition
				.getParentContainer();

			freezerLabel.setText(freezerContainer.getName());
			int dim1 = freezerContainer.getStorageType().getCapacity()
				.getDimensionOneCapacity();
			int dim2 = freezerContainer.getStorageType().getCapacity()
				.getDimensionTwoCapacity();
			freezerWidget.setStorageSize(dim1, dim2);
			freezerWidget.setSelectedBox(new Point(hotelPosition
				.getPositionDimensionOne() - 1, hotelPosition
				.getPositionDimensionTwo() - 1));

			hotelLabel.setText(hotelContainer.getName());
			dim1 = hotelContainer.getStorageType().getCapacity()
				.getDimensionOneCapacity();
			dim2 = hotelContainer.getStorageType().getCapacity()
				.getDimensionTwoCapacity();
			hotelWidget.setStorageSize(dim1, dim2);
			hotelWidget.setSelectedBox(new Point(palettePosition
				.getPositionDimensionOne() - 1, palettePosition
				.getPositionDimensionTwo() - 1));

			paletteLabel.setText(palette.getName());
		}
	}

	/**
	 * if a study is found, show the name in title
	 */
	protected void showStudyInformation() {
		if (currentStudy == null) {
			form.setText("Processing samples");
		} else {
			form.setText("Processing samples for study "
					+ currentStudy.getNameShort());
		}
	}

	protected boolean setStatus(ScanCell scanCell, Sample positionSample)
			throws ApplicationException {
		String value = scanCell.getValue();
		if (value == null) {
			if (positionSample == null) {
				return true;
			}
			scanCell.setStatus(SampleCellStatus.MISSING);
			scanCell.setInformation("Sample " + positionSample.getInventoryId()
					+ " missing");
			scanCell.setTitle("?");
			return false;
		}
		if (value.isEmpty()) {
			scanCell.setStatus(SampleCellStatus.ERROR);
			scanCell.setInformation("Error retrieving bar code");
			scanCell.setTitle("?");
			return false;
		}
		Sample sample = new Sample();
		sample.setInventoryId(value);
		List<Sample> samples = appService.search(Sample.class, sample);
		if (samples.size() == 0) {
			scanCell.setStatus(SampleCellStatus.ERROR);
			scanCell.setInformation("Sample not found");
			scanCell.setTitle("-");
			return false;
		} else if (samples.size() == 1) {
			sample = samples.get(0);
			if (positionSample != null
					&& !sample.getId().equals(positionSample.getId())) {
				scanCell.setStatus(SampleCellStatus.ERROR);
				scanCell
					.setInformation("Sample different from the one registered");
				scanCell.setTitle("!");
				return false;
			}
			scanCell.setSample(sample);
			if (positionSample != null) {
				scanCell.setStatus(SampleCellStatus.FILLED);
				scanCell.setSample(positionSample);
			} else {
				if (sample.getSamplePosition() != null
						&& !sample.getSamplePosition().getStorageContainer()
							.getId().equals(currentPalette.getId())) {
					scanCell.setStatus(SampleCellStatus.ERROR);
					scanCell
						.setInformation("Sample registered onto another palette !");
					scanCell.setTitle("!");
					return false;
				}
				scanCell.setStatus(SampleCellStatus.NEW);
			}
			Study cellStudy = sample.getPatientVisit().getPatient().getStudy();
			if (currentStudy == null) {
				// look which study is on the palette from the first cell
				currentStudy = cellStudy;
			} else if (!currentStudy.getId().equals(cellStudy.getId())) {
				// FIXME problem if try currentStudy.equals(cellStudy)... should
				// work !!
				scanCell.setStatus(SampleCellStatus.ERROR);
				scanCell.setInformation("Not same study (study="
						+ cellStudy.getNameShort() + ")");
				return false;
			}
			scanCell
				.setTitle(sample.getPatientVisit().getPatient().getNumber());
			return true;
		} else {
			Assert.isTrue(false, "InventoryId should be unique !");
			return false;
		}
	}

	private boolean isNewPalette() {
		return currentPalette.getId() == null;
	}

	@Override
	protected void saveForm() throws Exception {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					SDKQuery query;
					if (isNewPalette()) {
						// insert new container into selected
						// set position in the choosen container
						query = new InsertExampleQuery(currentPalette);
						SDKQueryResult res = appService.executeQuery(query);
						currentPalette = (StorageContainer) res
							.getObjectResult();
					}

					List<SDKQuery> queries = new ArrayList<SDKQuery>();
					for (int i = 0; i < cells.length; i++) {
						for (int j = 0; j < cells[i].length; j++) {
							ScanCell cell = cells[i][j];
							// cell.getStatus()
							if (cell != null) {
								Sample sample = cell.getSample();
								if (sample != null
										&& sample.getSamplePosition() == null) {
									SamplePosition samplePosition = new SamplePosition();
									samplePosition.setPositionDimensionOne(i);
									samplePosition.setPositionDimensionTwo(j);
									samplePosition
										.setStorageContainer(currentPalette);
									samplePosition.setSample(sample);
									sample.setSamplePosition(samplePosition);
									queries.add(new UpdateExampleQuery(sample));
								}
							}
						}
					}
					appService.executeBatchQuery(queries);

					getSite().getPage().closeEditor(
						ProcessSamplesEntryForm.this, false);
					Node node = sessionAdapter.accept(new NodeSearchVisitor(
						StorageContainer.class, currentPalette.getId()));
					SessionManager.getInstance().getTreeViewer().setSelection(
						new StructuredSelection(node));
					node.performDoubleClick();
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					SessionManager.getLogger().error(
						"Error when saving palette process", e);
				}
			}
		});
	}

	protected void cancelForm() {
		freezerWidget.setSelectedBox(null);
		hotelWidget.setSelectedBox(null);
		paletteWidget.setScannedElements(null);
		cells = null;
		currentStudy = null;
		scanLaunchedValue.setValue(false);
		setDirty(false);
	}

	@Override
	protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage("Processing samples.", IMessageProvider.NONE);
			submitButton.setEnabled(true);
			scanButton.setEnabled(true);
		} else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
			submitButton.setEnabled(false);
			if (!BioBankPlugin.getDefault().isValidPlateBarcode(
				plateToScanText.getText())) {
				scanButton.setEnabled(false);
			} else {
				if (status.getMessage() != null
						&& status.getMessage().contains("position")) {
					scanButton.setEnabled(false);
				} else {
					scanButton.setEnabled(true);
				}
			}
		}
	}

	@Override
	public void setFocus() {
		if (plateToScanValue.getValue().toString().isEmpty()) {
			plateToScanText.setFocus();
		}
	}

	public void cancel() throws Exception {
		cancelForm();
	}

	public void confirm() throws Exception {
		saveForm();
	}

	public boolean isConfirmEnabled() {
		return submitButton.isEnabled();
	}

	/**
	 * From the palette barcode, get existing information form database
	 */
	private boolean getPaletteInformation() throws ApplicationException {
		currentPaletteSamples = null;
		currentPalette = new StorageContainer();
		currentPalette.setBarcode((String) paletteCodeValue.getValue());
		List<StorageContainer> containers = appService.search(
			StorageContainer.class, currentPalette);
		if (containers.size() == 1) {
			currentPalette = containers.get(0);
			boolean result = MessageDialog
				.openConfirm(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "Palette barcode",
					"This palette is already registered in the database. Do you want to continue ?");
			if (!result) {
				return false;
			}
			showPalettePosition(currentPalette);
			Capacity paletteCapacity = currentPalette.getStorageType()
				.getCapacity();
			currentPaletteSamples = new Sample[paletteCapacity
				.getDimensionOneCapacity()][paletteCapacity
				.getDimensionTwoCapacity()];
			for (SamplePosition position : currentPalette
				.getSamplePositionCollection()) {
				currentPaletteSamples[position.getPositionDimensionOne()][position
					.getPositionDimensionTwo()] = position.getSample();
			}
			showOnlyPalette(false);
		} else {
			showOnlyPalette(true);
			paletteLabel.setText("New Palette");
		}
		return true;
	}

}
