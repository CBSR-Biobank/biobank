package edu.ualberta.med.biobank.forms;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.forms.listener.CancelSubmitKeyListener;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.SamplePosition;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.validators.CabinetIdValidator;
import edu.ualberta.med.biobank.validators.CabinetPositionCodeValidator;
import edu.ualberta.med.biobank.widgets.CabinetBinWidget;
import edu.ualberta.med.biobank.widgets.StorageContainerWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ProcessCabinetEntryForm extends BiobankEntryForm implements
		CancelConfirmForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.ProcessCabinetEntryForm";

	private static final Pattern HAIR_PATTERN = Pattern
		.compile("^\\p{Upper}{4}$");

	private static final Pattern DNA_PATTERN = Pattern
		.compile("^\\p{Lower}{4}$");

	private PatientVisitAdapter pvAdapter;
	private PatientVisit patientVisit;

	private StorageContainerWidget drawer;
	private CabinetBinWidget bin;
	private Label typeText;

	private Text inventoryIdText;
	private Text positionText;
	private Button showResult;

	private Text confirmCancelText;
	private Button cancel;
	private Button submit;

	private Sample sample = new Sample();

	private IObservableValue cabinetPosition = new WritableValue("",
		String.class);
	private IObservableValue resultShown = new WritableValue(Boolean.FALSE,
		Boolean.class);

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
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		client.setLayout(layout);
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		client.setLayoutData(gd);
		toolkit.paintBordersFor(client);

		toolkit.createLabel(client, "Drawer");
		toolkit.createLabel(client, "Bin");

		drawer = new StorageContainerWidget(client);
		toolkit.adapt(drawer);
		drawer.setGridSizes(4, 1, 150, 150);
		drawer.setFirstColSign('A');
		drawer.setShowColumnFirst(true);
		GridData gdDrawer = new GridData();
		gdDrawer.verticalAlignment = SWT.TOP;
		drawer.setLayoutData(gdDrawer);

		bin = new CabinetBinWidget(client);
		toolkit.adapt(bin);
		GridData gdBin = new GridData();
		gdBin.widthHint = CabinetBinWidget.WIDTH;
		gdBin.heightHint = CabinetBinWidget.HEIGHT;
		gdBin.verticalSpan = 2;
		bin.setLayoutData(gdBin);

		typeText = toolkit.createLabel(client, "");
		GridData gdText = new GridData();
		gdText.grabExcessHorizontalSpace = true;
		typeText.setLayoutData(gdText);
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

		inventoryIdText = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.NONE, "Inventory ID", new String[0], PojoObservables
				.observeValue(sample, "inventoryId"), CabinetIdValidator.class,
			"Enter Inventory Id (eg cdfg or DYUO)");
		inventoryIdText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

		positionText = (Text) createBoundWidgetWithLabel(client, Text.class,
			SWT.NONE, "Position", new String[0], cabinetPosition,
			CabinetPositionCodeValidator.class,
			"Enter a position (eg 01AA01AB)");
		positionText.removeKeyListener(keyListener);
		positionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

		showResult = toolkit.createButton(client, "Show Result", SWT.PUSH);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.CENTER;
		showResult.setLayoutData(gd);
		showResult.addSelectionListener(new SelectionAdapter() {
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
					resultShown.setValue(Boolean.TRUE);

					// FIXME parse position, get Sample if exists and get Exact
					// Positions !
					Random r = new Random();
					drawer.setSelectedBox(new int[] { r.nextInt(4), 0 });
					bin.setPosition(r.nextInt(36) + 1);

					SamplePosition sp = new SamplePosition();

					sample.setSampleType(getSampleType());

				} catch (RemoteConnectFailureException exp) {
					BioBankPlugin.openRemoteConnectErrorMessage();
				} catch (Exception e) {
					e.printStackTrace();
				}
				setDirty(true);
			}

		});
	}

	private SampleType getSampleType() throws ApplicationException {
		String inventoryId = sample.getInventoryId();
		SampleType sampleType = null;
		String s = "";
		Matcher m = DNA_PATTERN.matcher(inventoryId);
		if (m.matches()) {
			s = "DNA";

		} else {
			m = HAIR_PATTERN.matcher(inventoryId);
			if (m.matches()) {
				s = "HAIR";
			}
		}

		HQLCriteria criteria = new HQLCriteria(
			"from edu.ualberta.med.biobank.model.SampleType where name like '"
					+ s + "%'");
		List<SampleType> list = appService.query(criteria);
		if (list.size() == 1) {
			sampleType = list.get(0);
			typeText.setText("Type = " + sampleType.getNameShort());
			typeText.redraw();
		} else {
			Assert.isTrue(true, "Should find one sample type corresponding to "
					+ s);
		}
		return sampleType;
	}

	protected void cancelForm() {
		typeText.setText("");
		drawer.setSelectedBox(null);
		bin.setPosition(0);
		resultShown.setValue(Boolean.FALSE);
		inventoryIdText.setText("");
		positionText.setText("");
	}

	@Override
	protected void saveForm() throws Exception {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				// TODO
			}
		});
	}

	@Override
	protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage("Processing samples.", IMessageProvider.NONE);
			submit.setEnabled(true);
			showResult.setEnabled(true);
		} else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
			submit.setEnabled(false);
			if (status.getMessage() != null
					&& status.getMessage().contains("check values")) {
				showResult.setEnabled(true);
			} else {
				showResult.setEnabled(false);
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
