package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.ScanCell;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.LinkSampleTypeWidget;
import edu.ualberta.med.biobank.widgets.ScanPaletteWidget;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;

public class LinkSamplesEntryForm extends BiobankEntryForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.LinkSamplesEntryForm";

	private Button submit;

	private Button scan;

	private PatientVisitAdapter pvAdapter;

	private PatientVisit patientVisit;

	private Composite typesSelectionSection;

	private ScanPaletteWidget spw;

	private List<LinkSampleTypeWidget> sampleTypeWidgets;

	private ScanCell[][] cells;

	private IObservableValue scannedValue = new WritableValue(Boolean.FALSE,
		Boolean.class);
	private IObservableValue plateToScan = new WritableValue("", String.class);

	private Button cancel;

	private Text plateToScanText;

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

		setPartName("Link samples for " + patientVisit.getPatient().getNumber());
	}

	@Override
	protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage("Linking samples.", IMessageProvider.NONE);
			submit.setEnabled(true);
		} else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
			submit.setEnabled(false);
			if (plateToScan.getValue() == null
					|| plateToScan.getValue().toString().isEmpty()) {
				scan.setEnabled(false);
			} else {
				scan.setEnabled(true);
			}
		}
	}

	@Override
	protected void createFormContent() {
		form.setText("Link samples for patient "
				+ patientVisit.getPatient().getNumber() + " for visit "
				+ patientVisit.getNumber());

		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);

		createPaletteSection();
		createTypesSelectionSection();
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
		scannedValue.setValue(false);
	}

	private void createPaletteSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(1, false);
		client.setLayout(layout);

		spw = new ScanPaletteWidget(client, false);
		GridData gd = new GridData(SWT.CENTER, SWT.TOP, true, false);
		gd.heightHint = spw.getHeight();
		gd.widthHint = spw.getWidth();
		client.setLayoutData(gd);

		toolkit.adapt(spw);
	}

	private void createTypesSelectionSection() {
		typesSelectionSection = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		typesSelectionSection.setLayout(layout);
		toolkit.paintBordersFor(typesSelectionSection);

		List<SampleType> sampleTypes = getAllSampleTypes();
		sampleTypeWidgets = new ArrayList<LinkSampleTypeWidget>();
		char letter = 'A';
		for (int i = 0; i < ScanCell.ROW_MAX; i++) {
			LinkSampleTypeWidget typeWidget = new LinkSampleTypeWidget(
				typesSelectionSection, letter, sampleTypes, toolkit);
			typeWidget
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						setDirty(true);
					}

				});
			typeWidget.addBinding(dbc);
			sampleTypeWidgets.add(typeWidget);
			letter += 1;
		}
		typesSelectionSection.setEnabled(false);
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
				scan();
			}
		});

		cancel = toolkit.createButton(client, "Cancel", SWT.PUSH);
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancel();
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

	private void scan() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					// TODO launch scanner instead of random function
					cells = ScanCell.getRandomScanLink();
					scannedValue.setValue(true);

					for (int i = 0; i < cells.length; i++) { // rows
						int samplesNumber = 0;
						sampleTypeWidgets.get(i).initSelection();
						for (int j = 0; j < cells[i].length; j++) { // columns
							if (cells[i][j] != null) {
								samplesNumber++;
								cells[i][j].setStatus(CellStatus.FILLED);
							}
						}
						sampleTypeWidgets.get(i).setNumber(samplesNumber);
					}
					// Show result in grid
					spw.setScannedElements(cells);
					typesSelectionSection.setEnabled(true);
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					e.printStackTrace();
					typesSelectionSection.setEnabled(true);
				}
			}
		});
	}

	private List<SampleType> getAllSampleTypes() {
		try {
			return appService.search(SampleType.class, new SampleType());
		} catch (final RemoteConnectFailureException exp) {
			BioBankPlugin.openRemoteConnectErrorMessage();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return null;
	}

	@Override
	protected void saveForm() throws Exception {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();
		for (int indexRow = 0; indexRow < cells.length; indexRow++) {
			LinkSampleTypeWidget typeWidget = sampleTypeWidgets.get(indexRow);
			if (typeWidget.needToSave()) {
				SampleType type = typeWidget.getSelection();
				for (int indexColumn = 0; indexColumn < cells[indexRow].length; indexColumn++) {
					ScanCell cell = cells[indexRow][indexColumn];
					if (cell != null
							&& cell.getStatus().equals(CellStatus.FILLED)) {
						// add new samples
						Sample sample = new Sample();
						sample.setInventoryId(cells[indexRow][indexColumn]
							.getValue());
						sample.setPatientVisit(patientVisit);
						sample.setSampleType(type);
						queries.add(new InsertExampleQuery(sample));
					}
				}
			}
		}
		// FIXME Should roll back if something wrong in one of them = not
		// sure
		// it works !!
		appService.executeBatchQuery(queries);
		getSite().getPage().closeEditor(this, false);
		// FIXME Close and display the PatientVisit with added samples ?
	}

	protected void cancel() {
		cells = null;
		spw.setScannedElements(null);
	}

	@Override
	public void setFocus() {
		if (plateToScan.getValue().toString().isEmpty()) {
			plateToScanText.setFocus();
		}
	}

}
