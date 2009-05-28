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
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.ScanCell;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.ScanPaletteWidget;
import edu.ualberta.med.biobank.widgets.StorageContainerCanvas;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessSamplesEntryForm extends BiobankEntryForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.ProcessSamplesEntryForm";
	private ScanPaletteWidget palette;
	private Button scan;
	private Button submit;

	private IObservableValue palettePosition = new WritableValue("",
		String.class);
	private IObservableValue scannedValue = new WritableValue(Boolean.FALSE,
		Boolean.class);
	private IObservableValue scanValid = new WritableValue(Boolean.TRUE,
		Boolean.class);

	private ScanCell[][] cells;

	private Study currentStudy;

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");

		Assert
			.isTrue((node instanceof SiteAdapter),
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
		dbc.bindValue(wv, scannedValue, uvs, uvs);

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

		StorageContainerCanvas freezer = new StorageContainerCanvas(client);
		toolkit.adapt(freezer);
		int defaultRows = 5;
		int defaultCols = 10;
		freezer.setStorageSize(defaultRows, defaultCols,
			ScanPaletteWidget.PALETTE_WIDTH / defaultCols, 20);
		GridData gdFreezer = new GridData();
		gdFreezer.widthHint = freezer.getWidth();
		gdFreezer.heightHint = freezer.getHeight();
		freezer.setLayoutData(gdFreezer);

		StorageContainerCanvas hotel = new StorageContainerCanvas(client);
		toolkit.adapt(hotel);
		defaultRows = 19;
		defaultCols = 1;
		hotel.setStorageSize(defaultRows, defaultCols, 100,
			ScanPaletteWidget.PALETTE_HEIGHT_AND_LEGEND / defaultRows);
		GridData gdHotel = new GridData();
		gdHotel.widthHint = hotel.getWidth();
		gdHotel.heightHint = hotel.getHeight();
		gdHotel.verticalSpan = 2;
		gdHotel.verticalAlignment = SWT.END;
		hotel.setLayoutData(gdHotel);

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

	// private void createPaletteSection() {
	// Composite client = toolkit.createComposite(form.getBody());
	// GridLayout layout = new GridLayout(1, false);
	// client.setLayout(layout);
	//
	// palette = new ScanPaletteWidget(client, false);
	// GridData gd = new GridData(SWT.CENTER, SWT.TOP, true, false);
	// gd.heightHint = palette.getHeight();
	// gd.widthHint = palette.getWidth();
	// client.setLayoutData(gd);
	//
	// toolkit.adapt(palette);
	// }

	private void createFieldsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		GridData gd = new GridData();
		gd.widthHint = 200;
		client.setLayoutData(gd);
		toolkit.paintBordersFor(client);

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Position",
			new String[0], palettePosition, NonEmptyString.class,
			"Enter a position");
	}

	private void createButtonsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);

		scan = toolkit.createButton(client, "Scan", SWT.PUSH);
		scan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scan();
			}
		});

		submit = toolkit.createButton(client, "Submit", SWT.PUSH);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doSaveInternal();
			}
		});
	}

	protected void scan() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					currentStudy = null;

					// TODO launch real scanner
					cells = ScanCell.getRandomScanProcess();

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
					if (currentStudy != null) {
						showStudyInformation();
					}
					palette.setScannedElements(cells);
					scannedValue.setValue(true);
					scanValid.setValue(result);
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void showStudyInformation() {
		System.out.println(currentStudy.getNameShort());

		if (currentStudy != null) {
			form.setText("Processing samples for study "
					+ currentStudy.getNameShort());
		}
	}

	protected boolean setStatus(ScanCell scanCell) throws ApplicationException {
		String value = scanCell.getValue();
		if (value == null || value.isEmpty()) {
			scanCell.setStatus(CellStatus.ERROR);
			scanCell.setMessage("Error retrieving bar code");
			return false;
		}
		Sample sample = new Sample();
		sample.setInventoryId(value);
		List<Sample> samples = appService.search(Sample.class, sample);
		if (samples.size() == 0) {
			scanCell.setStatus(CellStatus.ERROR);
			scanCell.setMessage("Sample not found");
			return false;
		} else if (samples.size() == 1) {
			sample = samples.get(0);
			Study cellStudy = sample.getPatientVisit().getPatient().getStudy();
			if (currentStudy == null) {
				// look which study is on the palette from the first cell
				currentStudy = cellStudy;
			} else {
				if (!currentStudy.equals(cellStudy)) {
					scanCell.setStatus(CellStatus.ERROR);
					scanCell.setMessage("Not same study (study="
							+ cellStudy.getNameShort() + ")");
				}
			}
			// TODO dans le cas d'un emplacement deja cree, verifier le cas
			// ou le sample existe deja ou manque
			scanCell.setStatus(CellStatus.NEW);
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
			if (palettePosition.getValue() == null
					|| palettePosition.getValue().toString().isEmpty()) {
				scan.setEnabled(false);
			} else {
				scan.setEnabled(true);
			}
		}
	}

}
