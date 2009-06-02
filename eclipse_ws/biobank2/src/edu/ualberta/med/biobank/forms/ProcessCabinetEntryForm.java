package edu.ualberta.med.biobank.forms;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.forms.listener.CancelSubmitListener;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.validators.CabinetIdValidator;
import edu.ualberta.med.biobank.validators.CabinetPositionCodeValidator;
import edu.ualberta.med.biobank.widgets.CabinetBinWidget;
import edu.ualberta.med.biobank.widgets.StorageContainerWidget;

public class ProcessCabinetEntryForm extends BiobankEntryForm implements
		CancelConfirmForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.ProcessCabinetEntryForm";

	private PatientVisitAdapter pvAdapter;
	private PatientVisit patientVisit;
	private StorageContainerWidget drawer;

	private CabinetBinWidget bin;

	private Button cancel;

	private Button submit;

	private Text inventoryIdText;

	private Text positionText;

	private Text confirmCancelText;

	private IObservableValue inventoryId = new WritableValue("", String.class);

	private IObservableValue cabinetPosition = new WritableValue("",
		String.class);

	private IObservableValue confirmCancel = new WritableValue("", String.class);

	private Button showResult;

	private Label resultText;

	private static final Pattern HAIR_PATTERN = Pattern
		.compile("^\\p{Upper}{4}$");

	private static final Pattern DNA_PATTERN = Pattern
		.compile("^\\p{Lower}{4}$");

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
		createFieldsSection();
		createButtonsSection();
	}

	private void createLocationSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		client.setLayoutData(gd);
		client.setLayout(layout);
		toolkit.paintBordersFor(client);

		toolkit.createLabel(client, "Drawer");
		toolkit.createLabel(client, "Bin");

		drawer = new StorageContainerWidget(client);
		toolkit.adapt(drawer);
		drawer.setGridSizes(4, 1, 150, 150);
		drawer.setFirstColSign('A');
		drawer.setShowColumnFirst(true);
		GridData gdDrawer = new GridData();
		gdDrawer.widthHint = drawer.getWidth();
		gdDrawer.heightHint = drawer.getHeight();
		gdDrawer.verticalAlignment = SWT.TOP;
		drawer.setLayoutData(gdDrawer);

		bin = new CabinetBinWidget(client);
		toolkit.adapt(bin);
		GridData gdBin = new GridData();
		gdBin.widthHint = CabinetBinWidget.WIDTH;
		gdBin.heightHint = CabinetBinWidget.HEIGHT;
		gdBin.verticalSpan = 2;
		bin.setLayoutData(gdBin);

		resultText = toolkit.createLabel(client, "");
		GridData gdText = new GridData();
		gdText.grabExcessHorizontalSpace = true;
		gdText.widthHint = drawer.getWidth();
		resultText.setLayoutData(gdText);
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
			SWT.NONE, "Inventory ID", new String[0], inventoryId,
			CabinetIdValidator.class, "Enter Inventory Id (eg cdfg or DYUO)");
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

		// Use a composite for the confirmCancelText because of the weird
		// alignment with button that happen if there is not this extra
		// composite
		Composite comp = toolkit.createComposite(client);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		comp.setLayout(layout);
		toolkit.paintBordersFor(comp);
		confirmCancelText = (Text) createBoundWidgetWithLabel(comp, Text.class,
			SWT.NONE, "Submit / Cancel", new String[0], confirmCancel, null,
			null);
		confirmCancelText.removeKeyListener(keyListener);

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

	protected void showPositionResult() {
		// FIXME parse position, get Sample if exists and get Exact Positions !

		Random r = new Random();
		drawer.setSelectedBox(new int[] { r.nextInt(4), 0 });
		bin.setPosition(r.nextInt(36) + 1);

		// Show the full position and type (hair or DNA).

		Matcher m = DNA_PATTERN.matcher(inventoryId.getValue().toString());
		if (m.matches()) {
			resultText.setText("DNA");
		} else {
			m = HAIR_PATTERN.matcher(inventoryId.getValue().toString());
			if (m.matches()) {
				resultText.setText("HAIRS");
			}
		}

		setDirty(true);
	}

	protected void cancelForm() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveForm() throws Exception {
		// TODO Auto-generated method stub

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
			showResult.setEnabled(false);
		}
	}

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
