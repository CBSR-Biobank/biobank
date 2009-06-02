package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.forms.listener.CancelSubmitListener;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.ScanCell;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.validators.PaletteBarCodeValidator;
import edu.ualberta.med.biobank.widgets.ScanPaletteWidget;
import edu.ualberta.med.biobank.widgets.StorageContainerWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessSamplesEntryForm extends BiobankEntryForm implements
		CancelConfirmForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.ProcessSamplesEntryForm";
	private ScanPaletteWidget palette;
	private Button scan;
	private Button submit;

	private IObservableValue plateToScan = new WritableValue("", String.class);
	private IObservableValue palettePosition = new WritableValue("",
		String.class);
	private IObservableValue confirmCancel = new WritableValue("", String.class);
	private IObservableValue scanLaunched = new WritableValue(Boolean.FALSE,
		Boolean.class);
	private IObservableValue scanValid = new WritableValue(Boolean.TRUE,
		Boolean.class);

	private ScanCell[][] cells;

	private Study currentStudy;
	private Text positionText;
	private StorageContainerWidget hotel;
	private StorageContainerWidget freezer;
	private Text plateToScanText;
	private Text confirmCancelText;
	private Button cancel;

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

		appService = node.getAppService();

		setPartName("Process samples");
	}

	@Override
	protected void createFormContent() {
		form.setText("Processing samples");

		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);

		createPaletteSection();
		createFieldsSection();
		createButtonsSection();

		WritableValue wv = new WritableValue(Boolean.FALSE, Boolean.class);
		UpdateValueStrategy uvs = new UpdateValueStrategy();
		uvs.setAfterConvertValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof Boolean && !(Boolean) value) {
					return ValidationStatus.error("Scanner should be launched");
				} else {
					return Status.OK_STATUS;
				}
			}

		});
		dbc.bindValue(wv, scanLaunched, uvs, uvs);

		wv = new WritableValue(Boolean.TRUE, Boolean.class);
		uvs = new UpdateValueStrategy();
		uvs.setAfterConvertValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof Boolean && !(Boolean) value) {
					return ValidationStatus.error("Error in scanning result");
				} else {
					return Status.OK_STATUS;
				}
			}
		});
		dbc.bindValue(wv, scanValid, uvs, uvs);
	}

	private void createPaletteSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		client.setLayout(layout);

		freezer = new StorageContainerWidget(client);
		toolkit.adapt(freezer);
		freezer.setGridSizes(5, 10, ScanPaletteWidget.PALETTE_WIDTH, 100);
		GridData gdFreezer = new GridData();
		gdFreezer.widthHint = freezer.getWidth();
		gdFreezer.heightHint = freezer.getHeight();
		freezer.setLayoutData(gdFreezer);

		hotel = new StorageContainerWidget(client);
		toolkit.adapt(hotel);
		hotel.setGridSizes(11, 1, 100,
			ScanPaletteWidget.PALETTE_HEIGHT_AND_LEGEND);
		GridData gdHotel = new GridData();
		gdHotel.widthHint = hotel.getWidth();
		gdHotel.heightHint = hotel.getHeight();
		gdHotel.verticalSpan = 2;
		gdHotel.verticalAlignment = SWT.END;
		hotel.setLayoutData(gdHotel);
		hotel.setFirstColSign(null);
		hotel.setFirstRowSign(1);

		palette = new ScanPaletteWidget(client, true);
		toolkit.adapt(palette);
		GridData gdPalette = new GridData();
		gdPalette.widthHint = palette.getWidth();
		gdPalette.heightHint = palette.getHeight();
		palette.setLayoutData(gdPalette);

		GridData gd = new GridData();
		gd.heightHint = palette.getHeight() + freezer.getHeight() + 10;
		gd.widthHint = palette.getWidth() + hotel.getWidth() + 10;
		client.setLayoutData(gd);
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
			SWT.NONE, "Plate to Scan", new String[0], plateToScan,
			NonEmptyString.class, "Enter plate to scan");
		plateToScanText.removeKeyListener(keyListener);
		plateToScanText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

		positionText = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.NONE, "Position", new String[0], palettePosition,
			PaletteBarCodeValidator.class, "Enter a position (eg 01AA01)");
		positionText.removeKeyListener(keyListener);
		positionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

		confirmCancelText = (Text) createBoundWidgetWithLabel(client,
			Text.class, SWT.NONE, "Submit / Cancel", new String[0],
			confirmCancel, null, null);
		confirmCancelText.removeKeyListener(keyListener);
	}

	private void createButtonsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);

		scan = toolkit.createButton(client, "Scan", SWT.PUSH);
		scan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					scan();
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});

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
		confirmCancelText.addKeyListener(new CancelSubmitListener(this));
	}

	protected void scan() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					Container container = new Container();
					container.setBarcode((String) palettePosition.getValue());
					List<Container> containers = appService.search(
						Container.class, container);
					if (containers.size() == 0) {
						// container n'existe pas encore - comment c'est ce que
						// doit ajouter
						// ?
						// doit etre deja la ou creation maintenant ?
					} else {
						container = containers.get(0);
						boolean result = MessageDialog
							.openConfirm(PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getShell(),
								"Palette position",
								"One palette already exists at this position. Do you want to continue ?");
						if (!result)
							return;

						hotel.setStorageSize(container.getCapacity()
							.getDimensionOneCapacity(), container.getCapacity()
							.getDimensionTwoCapacity());
						ContainerPosition cp = container.getLocatedAtPosition();
						Integer x = cp.getPositionDimensionOne();
						Integer y = cp.getPositionDimensionTwo();
					}

					// TODO test to remove
					hotel.setStorageSize(19, 1);
					hotel.setSelectedBox(new int[] { 5, 0 });

					freezer.setStorageSize(6, 10);
					freezer.setSelectedBox(new int[] { 4, 2 });

					currentStudy = null;

					// TODO launch real scanner
					cells = ScanCell.getRandomScanProcess();
					scanLaunched.setValue(true);

					// TODO gerer le cas ou la position existe deja et contient
					// deja qqchose
					boolean result = true;
					for (int i = 0; i < cells.length; i++) { // rows
						for (int j = 0; j < cells[i].length; j++) { // columns
							if (cells[i][j] != null) {
								result = setStatus(cells[i][j]) && result;
							}
						}
					}
					showStudyInformation();
					palette.setScannedElements(cells);
					scanValid.setValue(result);
					setDirty(true);
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					e.printStackTrace();
					scanValid.setValue(false);
				}
			}
		});
	}

	protected void showStudyInformation() {
		if (currentStudy == null) {
			form.setText("Processing samples");
		} else {
			form.setText("Processing samples for study "
					+ currentStudy.getNameShort());
		}
	}

	protected boolean setStatus(ScanCell scanCell) throws ApplicationException {
		String value = scanCell.getValue();
		if (value == null || value.isEmpty()) {
			scanCell.setStatus(CellStatus.ERROR);
			scanCell.setInformation("Error retrieving bar code");
			return false;
		}
		Sample sample = new Sample();
		sample.setInventoryId(value);
		List<Sample> samples = appService.search(Sample.class, sample);
		if (samples.size() == 0) {
			scanCell.setStatus(CellStatus.ERROR);
			scanCell.setInformation("Sample not found");
			return false;
		} else if (samples.size() == 1) {
			scanCell.setStatus(CellStatus.NEW);
			sample = samples.get(0);
			Study cellStudy = sample.getPatientVisit().getPatient().getStudy();
			if (currentStudy == null) {
				// look which study is on the palette from the first cell
				currentStudy = cellStudy;
			} else if (currentStudy.getId().equals(cellStudy.getId())) {
				// FIXME problem if try currentStudy.equals(cellStudy)... should
				// work !!
				scanCell.setStatus(CellStatus.ERROR);
				scanCell.setInformation("Not same study (study="
						+ cellStudy.getNameShort() + ")");
			}
			scanCell
				.setTitle(sample.getPatientVisit().getPatient().getNumber());
			// TODO dans le cas d'un emplacement deja cree, verifier le cas
			// ou le sample existe deja ou manque
			return true;
		} else {
			Assert.isTrue(false, "InventoryId should be unique !");
			return false;
		}
	}

	@Override
	protected void saveForm() throws Exception {
		// FIXME save form
		System.out.println("save");
		getSite().getPage().closeEditor(this, false);
		// FIXME display informations ?
	}

	protected void cancelForm() {
		freezer.setSelectedBox(null);
		hotel.setSelectedBox(null);
		palette.setScannedElements(null);
		cells = null;
		currentStudy = null;
		scanLaunched.setValue(false);
		setDirty(false);
	}

	@Override
	protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage("Processing samples.", IMessageProvider.NONE);
			submit.setEnabled(true);
			scan.setEnabled(true);
		} else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
			submit.setEnabled(false);
			if (plateToScan.getValue() == null
					|| plateToScan.getValue().toString().isEmpty()
					|| palettePosition.getValue() == null
					|| palettePosition.getValue().toString().isEmpty()) {
				scan.setEnabled(false);
			} else {
				scan.setEnabled(true);
			}
		}
	}

	@Override
	public void setFocus() {
		if (plateToScan.getValue().toString().isEmpty()) {
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
		return submit.isEnabled();
	}

}
