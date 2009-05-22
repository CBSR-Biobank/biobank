package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.CellStatus;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.ScanCell;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.widgets.ScanPaletteWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;

public class LinkSamplesEntryForm extends BiobankEntryForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.LinkSamplesEntryForm";

	private static final String MSG_SAMPLES_OK = "Editing samples.";

	private Button submit;
	
	private Button scan;

	private PatientVisitAdapter pvAdapter;

	private PatientVisit patientVisit;
	
	private Composite typesSelectionSection;

	private ScanPaletteWidget spw;

	private List<ComboViewer> sampleTypeCombos;
	private List<Label> sampleNumberTexts;
	
	private ScanCell[][] cells;

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");

		Assert.isTrue((node instanceof PatientVisitAdapter),
				"Invalid editor input: object of type "
						+ node.getClass().getName());

		pvAdapter = (PatientVisitAdapter) node;
		patientVisit = pvAdapter.getPatientVisit();
		appService = pvAdapter.getAppService();
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
		return MSG_SAMPLES_OK;
	}

	@Override
	protected void saveForm() throws Exception {
		SDKQuery query;
		SDKQueryResult result;
		System.out.println("save samples to patient visit !");
		for (int indexRow = 0; indexRow < cells.length; indexRow++) {
			ComboViewer cv = sampleTypeCombos.get(indexRow);			
			SampleType type = (SampleType)((StructuredSelection)cv.getSelection()).getFirstElement();
			// TODO prendre en compte le cas ou il n'y a pas de selection d'effectue
			for (int indexColumn = 0; indexColumn < cells[indexRow].length; indexColumn++) {			
				// Fait comme si l'echantillon n'existait pas de sur dans un premier temps
				Sample sample = new Sample();
				ScanCell cell = cells[indexRow][indexColumn];
				if (cell != null) {
					sample.setInventoryId(cells[indexRow][indexColumn].getValue());
					sample.setPatientVisit(patientVisit);
					sample.setSampleType(type);
					query = new InsertExampleQuery(sample);
					appService.executeQuery(query);
					// TODO : tout faire dans une seule transaction ou non ?
				}
			}
		}
	}

	@Override
	protected void createFormContent() {
		form.setText("Scan new samples");

		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);

		createPaletteScanSection();
		createTypesSelectionSection();
		createButtonsSection();
	}

	private void createTypesSelectionSection() {
		typesSelectionSection = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		typesSelectionSection.setLayout(layout);
		toolkit.paintBordersFor(typesSelectionSection);

		List<SampleType> sampleTypes = getAllSampleTypes();
		sampleTypeCombos = new ArrayList<ComboViewer>();
		sampleNumberTexts = new ArrayList<Label>();
		char letter = 'A';
		for (int i = 0; i < ScanCell.ROW_MAX; i++) {
			toolkit.createLabel(typesSelectionSection, String.valueOf(letter), SWT.LEFT);
			sampleTypeCombos.add(createSampleTypeCombo(typesSelectionSection, sampleTypes));
			Label text = toolkit.createLabel(typesSelectionSection, "", SWT.RIGHT|SWT.BORDER);
			GridData data = new GridData();
			data.widthHint = 20;
			text.setLayoutData(data);
			sampleNumberTexts.add(text);
			letter += 1;
		}
		typesSelectionSection.setEnabled(false);
	}

	private void createPaletteScanSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(1, false);
		client.setLayout(layout);
		GridData gd = new GridData(SWT.CENTER, SWT.TOP, true, false);
		gd.heightHint = ScanPaletteWidget.HEIGHT;
		gd.widthHint = ScanPaletteWidget.WIDTH;
		client.setLayoutData(gd);
		spw = new ScanPaletteWidget(client);
		toolkit.adapt(spw);
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

	private void scan() {
		// TODO prendre en compte le re-scan pour savoir si change ou non les combos
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					cells = ScanCell.getRandomScan();
					for (int i = 0; i < cells.length; i++) { // rows
						int samplesNumber = 0;
						for (int j = 0; j < cells[i].length; j++) { // columns
							if (cells[i][j] != null) {
								samplesNumber++;
								Sample sample = new Sample();
								sample.setInventoryId(cells[i][j].getValue());
								List<Sample> samples = appService.search(Sample.class, sample);
								if (samples.size() == 0) { // new sample
									cells[i][j].setStatus(CellStatus.NEW);
								} else if (samples.size() == 1) { // sample in DB
									if (samples.get(0).getPatientVisit()
											.equals(patientVisit)) {
										cells[i][j]
												.setStatus(CellStatus.FILLED);
									} else { // sample part of another patient visit !
										cells[i][j].setStatus(CellStatus.ERROR);
									}
								} else {
									cells[i][j].setStatus(CellStatus.ERROR);
								}
							}
						}
						sampleNumberTexts.get(i).setText(
								String.valueOf(samplesNumber));
					}
					spw.setScannedElements(cells);
					typesSelectionSection.setEnabled(true);
				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (ApplicationException ae) {
					ae.printStackTrace();
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

	protected ComboViewer createSampleTypeCombo(Composite parent,
			List<SampleType> list) {
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		toolkit.adapt(combo, true, true);

		ComboViewer vwr = new ComboViewer(combo);
		vwr.setContentProvider(new ArrayContentProvider());
		vwr.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((SampleType) element).getName();
			}
		});
//		vwr.addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				ISelection selection = event.getSelection();
//				if (selection instanceof StructuredSelection
//						&& ((StructuredSelection) selection).size() == 1) {
//					setDirty(true);
//					System.out
//							.println(((SampleType) ((StructuredSelection) selection)
//									.getFirstElement()).getName());
//				}
//			}
//		});
		vwr.setComparer(new IElementComparer() {
			@Override
			public boolean equals(Object a, Object b) {
				if (a instanceof SampleType && b instanceof SampleType) {
					return ((SampleType) a).getId().equals(
							((SampleType) b).getId());
				}
				return false;
			}

			@Override
			public int hashCode(Object element) {
				return element.hashCode();
			}

		});
		vwr.setComparator(new ViewerComparator());
		vwr.setInput(list);
		return vwr;
	}

}
