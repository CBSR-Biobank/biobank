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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
import edu.ualberta.med.biobank.widgets.ScanLinkPaletteWidget;
import edu.ualberta.med.biobank.widgets.listener.ScanPaletteModificationEvent;
import edu.ualberta.med.biobank.widgets.listener.ScanPaletteModificationListener;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;

public class LinkSamplesEntryForm extends BiobankEntryForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.LinkSamplesEntryForm";

	private Button submit;

	private Button scan;

	private PatientVisitAdapter pvAdapter;

	private PatientVisit patientVisit;

	private Composite typesSelectionPerRowComposite;

	private ScanLinkPaletteWidget spw;

	private List<LinkSampleTypeWidget> sampleTypeWidgets;

	private IObservableValue scannedValue = new WritableValue(Boolean.FALSE,
		Boolean.class);
	private IObservableValue plateToScan = new WritableValue("", String.class);

	private IObservableValue typesFilled = new WritableValue(Boolean.TRUE,
		Boolean.class);

	private Button cancel;

	private Text plateToScanText;

	private Composite typesSelectionCustomComposite;

	private Button radioRowSelection;

	private Button radioCustomSelection;

	private LinkSampleTypeWidget customSelection;

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
		createFieldsSection();
		createTypesSelectionSection();
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

		wv = new WritableValue(Boolean.TRUE, Boolean.class);
		uvs = new UpdateValueStrategy();
		uvs.setAfterConvertValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof Boolean && !(Boolean) value) {
					return ValidationStatus.error("Give a type to each sample");
				} else {
					return Status.OK_STATUS;
				}
			}

		});
		dbc.bindValue(wv, typesFilled, uvs, uvs);
	}

	private void createPaletteSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(1, false);
		client.setLayout(layout);
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		client.setLayoutData(gd);

		spw = new ScanLinkPaletteWidget(client);
		toolkit.adapt(spw);
		spw.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
	}

	private void createTypesSelectionSection() {
		// Radio buttons
		Composite radioComp = toolkit.createComposite(form.getBody());
		RowLayout compLayout = new RowLayout();
		radioComp.setLayout(compLayout);
		toolkit.paintBordersFor(radioComp);

		radioRowSelection = toolkit.createButton(radioComp, "Row choice",
			SWT.RADIO);
		radioCustomSelection = toolkit.createButton(radioComp,
			"Custom Selection choice", SWT.RADIO);

		// stackLayout
		final Composite selectionComp = toolkit.createComposite(form.getBody());
		final StackLayout selectionStackLayout = new StackLayout();
		selectionComp.setLayout(selectionStackLayout);

		List<SampleType> sampleTypes = getAllSampleTypes();
		createTypeSelectionPerRowComposite(selectionComp, sampleTypes);
		createTypeSelectionCustom(selectionComp, sampleTypes);
		radioRowSelection.setSelection(true);
		selectionStackLayout.topControl = typesSelectionPerRowComposite;

		radioRowSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (radioRowSelection.getSelection()) {
					selectionStackLayout.topControl = typesSelectionPerRowComposite;
					selectionComp.layout();
					for (LinkSampleTypeWidget sampleType : sampleTypeWidgets) {
						sampleType.addBinding(dbc);
						sampleType.resetValues(false);
					}
					customSelection.addBinding(dbc);
					spw.disableSelection();
					typesFilled.setValue(Boolean.TRUE);
					spw.redraw();
				}
			}
		});
		radioCustomSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (radioCustomSelection.getSelection()) {
					selectionStackLayout.topControl = typesSelectionCustomComposite;
					selectionComp.layout();
					for (LinkSampleTypeWidget sampleType : sampleTypeWidgets) {
						sampleType.removeBinding(dbc);
					}
					customSelection.addBinding(dbc);
					spw.enableSelection();
					typesFilled.setValue(spw.isEverythingTyped());
					spw.redraw();

				}
			}
		});
	}

	private void createTypeSelectionCustom(Composite parent,
			List<SampleType> sampleTypes) {
		typesSelectionCustomComposite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(3, false);
		typesSelectionCustomComposite.setLayout(layout);
		toolkit.paintBordersFor(typesSelectionCustomComposite);

		Label label = toolkit.createLabel(typesSelectionCustomComposite,
			"Choose type for selected samples:");
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		customSelection = new LinkSampleTypeWidget(
			typesSelectionCustomComposite, null, sampleTypes, toolkit);
		customSelection.resetValues(true);

		Button applyType = toolkit.createButton(typesSelectionCustomComposite,
			"Apply", SWT.PUSH);
		applyType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SampleType type = customSelection.getSelection();
				if (type != null) {
					for (ScanCell cell : spw.getSelectedCells()) {
						cell.setType(type);
						cell.setStatus(CellStatus.TYPE);
					}
					spw.clearSelection();
					customSelection.resetValues(true);
					typesFilled.setValue(spw.isEverythingTyped());
					spw.redraw();
				}
			}
		});
		spw.addModificationListener(new ScanPaletteModificationListener() {
			@Override
			public void modification(ScanPaletteModificationEvent spme) {
				customSelection.setNumber(spme.selections);
			}
		});
	}

	private void createTypeSelectionPerRowComposite(Composite parent,
			List<SampleType> sampleTypes) {
		typesSelectionPerRowComposite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		typesSelectionPerRowComposite.setLayout(layout);
		toolkit.paintBordersFor(typesSelectionPerRowComposite);

		sampleTypeWidgets = new ArrayList<LinkSampleTypeWidget>();
		char letter = 'A';
		for (int i = 0; i < ScanCell.ROW_MAX; i++) {
			LinkSampleTypeWidget typeWidget = new LinkSampleTypeWidget(
				typesSelectionPerRowComposite, letter, sampleTypes, toolkit);
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
	}

	private void createFieldsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);

		Composite comp = toolkit.createComposite(client);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		comp.setLayout(layout);
		GridData gd = new GridData();
		gd.widthHint = 200;
		comp.setLayoutData(gd);
		toolkit.paintBordersFor(comp);
		// TODO : could be a combo as there is not other need of the handheld
		// scanner in this form !
		plateToScanText = (Text) createBoundWidgetWithLabel(comp, Text.class,
			SWT.NONE, "Plate to Scan", new String[0], plateToScan,
			NonEmptyString.class, "Enter plate to scan");
		plateToScanText.removeKeyListener(keyListener);
		plateToScanText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

		scan = toolkit.createButton(client, "Scan", SWT.PUSH);
		scan.addSelectionListener(new SelectionAdapter() {
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
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);

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
					ScanCell[][] cells = ScanCell.getRandomScanLink();
					scannedValue.setValue(true);

					for (int i = 0; i < cells.length; i++) { // rows
						int samplesNumber = 0;
						sampleTypeWidgets.get(i).resetValues(true);
						for (int j = 0; j < cells[i].length; j++) { // columns
							if (cells[i][j] != null) {
								samplesNumber++;
								cells[i][j].setStatus(CellStatus.NEW);
							}
						}
						sampleTypeWidgets.get(i).setNumber(samplesNumber);
					}
					// Show result in grid
					spw.setScannedElements(cells);
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					e.printStackTrace();
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
	protected void saveForm() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					if (radioRowSelection.getSelection()) {
						saveWithRowSelection();
					} else {
						saveWithCustomSelection();
					}
					getSite().getPage().closeEditor(LinkSamplesEntryForm.this,
						false);
					Node.openForm(new FormInput(pvAdapter),
						PatientVisitViewForm.ID);
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void saveWithRowSelection() throws ApplicationException {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();
		ScanCell[][] cells = spw.getScannedElements();
		for (int indexRow = 0; indexRow < cells.length; indexRow++) {
			LinkSampleTypeWidget typeWidget = sampleTypeWidgets.get(indexRow);
			if (typeWidget.needToSave()) {
				SampleType type = typeWidget.getSelection();
				for (int indexColumn = 0; indexColumn < cells[indexRow].length; indexColumn++) {
					ScanCell cell = cells[indexRow][indexColumn];
					if (cell != null
							&& !cell.getStatus().equals(CellStatus.EMPTY)) {
						// add new samples
						Sample sample = new Sample();
						sample.setInventoryId(cell.getValue());
						sample.setPatientVisit(patientVisit);
						sample.setSampleType(type);
						queries.add(new InsertExampleQuery(sample));
					}
				}
			}
		}
		appService.executeBatchQuery(queries);
	}

	protected void saveWithCustomSelection() throws ApplicationException {
		List<SDKQuery> queries = new ArrayList<SDKQuery>();
		ScanCell[][] cells = spw.getScannedElements();
		for (int indexRow = 0; indexRow < cells.length; indexRow++) {
			for (int indexColumn = 0; indexColumn < cells[indexRow].length; indexColumn++) {
				ScanCell cell = cells[indexRow][indexColumn];
				if (cell != null && cell.getStatus().equals(CellStatus.TYPE)) {
					// add new samples
					Sample sample = new Sample();
					sample.setInventoryId(cell.getValue());
					sample.setPatientVisit(patientVisit);
					sample.setSampleType(cell.getType());
					queries.add(new InsertExampleQuery(sample));
				}
			}
		}
		appService.executeBatchQuery(queries);
	}

	protected void cancel() {
		spw.setScannedElements(null);
		for (LinkSampleTypeWidget stw : sampleTypeWidgets) {
			stw.resetValues(true);
		}
	}

	@Override
	public void setFocus() {
		if (plateToScan.getValue().toString().isEmpty()) {
			plateToScanText.setFocus();
		}
	}

}
