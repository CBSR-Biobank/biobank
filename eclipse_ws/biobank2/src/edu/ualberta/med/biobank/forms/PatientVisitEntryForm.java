package edu.ualberta.med.biobank.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.gface.date.DatePickerCombo;
import com.gface.date.DatePickerStyle;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientVisitEntryForm extends BiobankEntryForm {
	public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitEntryForm";

	public static final String MSG_NEW_PATIENT_VISIT_OK = "Creating a new patient visit record.";

	public static final String MSG_PATIENT_VISIT_OK = "Editing an existing patient visit record.";

	public static final String MSG_NO_VISIT_NUMBER = "Visit must have a number";

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	private PatientVisitAdapter patientVisitAdapter;

	private PatientVisit patientVisit;

	private Study study;

	private ListOrderedMap pvInfoMap;

	class CombinedPvInfo {
		PvInfo pvInfo;
		PvInfoData pvInfoData;
		Control control;

		public CombinedPvInfo() {
			pvInfo = null;
			pvInfoData = null;
			control = null;
		}
	}

	public PatientVisitEntryForm() {
		super();
		pvInfoMap = new ListOrderedMap();
	}

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");

		patientVisitAdapter = (PatientVisitAdapter) node;
		patientVisit = patientVisitAdapter.getPatientVisit();
		appService = patientVisitAdapter.getAppService();

		if (patientVisit.getId() == null) {
			setPartName("New Patient Visit");
		} else {
			setPartName("Patient Visit " + patientVisit.getNumber());
		}
	}

	@Override
	protected void createFormContent() {
		form.setText("Patient Visit Information");
		form.setMessage(getOkMessage(), IMessageProvider.NONE);
		form.getBody().setLayout(new GridLayout(1, false));

		createPvSection();
		createButtonsSection();
	}

	private void createPvSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);

		createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
				"Visit Number", null, PojoObservables.observeValue(
						patientVisit, "number"), NonEmptyString.class,
				MSG_NO_VISIT_NUMBER);

		study = ((StudyAdapter) patientVisitAdapter.getParent().getParent()
				.getParent()).getStudy();

		for (PvInfo pvInfo : study.getPvInfoCollection()) {
			CombinedPvInfo combinedPvInfo = new CombinedPvInfo();
			combinedPvInfo.pvInfo = pvInfo;
			pvInfoMap.put(pvInfo.getId(), combinedPvInfo);
		}

		Collection<PvInfoData> pvDataCollection = patientVisit
				.getPvInfoDataCollection();
		if (pvDataCollection != null) {
			for (PvInfoData pvInfoData : pvDataCollection) {
				Integer key = pvInfoData.getPvInfo().getId();
				CombinedPvInfo combinedPvInfo = (CombinedPvInfo) pvInfoMap
						.get(key);
				Assert.isNotNull(combinedPvInfo);
				combinedPvInfo.pvInfoData = pvInfoData;
			}
		}

		MapIterator it = pvInfoMap.mapIterator();
		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			CombinedPvInfo combinedPvInfo = (CombinedPvInfo) it.getValue();
			int typeId = combinedPvInfo.pvInfo.getPvInfoType().getId();
			String value = null;

			if (combinedPvInfo.pvInfoData != null) {
				value = combinedPvInfo.pvInfoData.getValue();
			}

			Label labelWidget = toolkit.createLabel(client,
					combinedPvInfo.pvInfo.getLabel() + ":", SWT.LEFT);
			labelWidget.setLayoutData(new GridData(
					GridData.VERTICAL_ALIGN_BEGINNING));

			switch (typeId) {
			case 1: // number
				combinedPvInfo.control = toolkit.createText(client, value,
						SWT.LEFT);
				break;

			case 2: // text
				combinedPvInfo.control = toolkit.createText(client, value,
						SWT.LEFT | SWT.MULTI);
				break;

			case 3: // date_time
				combinedPvInfo.control = createDatePickerSection(client, value);
				break;

			case 4: // select_single
				combinedPvInfo.control = createComboSection(client,
						combinedPvInfo.pvInfo.getPossibleValues().split(";"),
						value);
				break;

			case 5: // select_single_and_quantity
				break;

			case 6: // select_multiple
				break;

			default:
				Assert.isTrue(false, "Invalid pvInfo type: " + typeId);
			}
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			if (typeId == 11) {
				gd.heightHint = 40;
			}
			combinedPvInfo.control.setLayoutData(gd);
			controls.put(combinedPvInfo.pvInfo.getLabel(),
					combinedPvInfo.control);
		}
	}

	private Control createDatePickerSection(Composite client, String value) {
		DatePickerCombo datePicker = new DatePickerCombo(client, SWT.BORDER,
				DatePickerStyle.BUTTONS_ON_BOTTOM
						| DatePickerStyle.YEAR_BUTTONS
						| DatePickerStyle.HIDE_WHEN_NOT_IN_FOCUS);
		datePicker.setLayout(new GridLayout(1, false));
		datePicker.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		datePicker.setDateFormat(new SimpleDateFormat(DATE_FORMAT));

		if ((value != null) && (value.length() > 0)) {
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
			try {
				datePicker.setDate(df.parse(value));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		return datePicker;
	}

	private Control createComboSection(Composite client, String[] values,
			String selected) {

		Combo combo = new Combo(client, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		combo.setItems(values);

		if (selected != null) {
			int count = 0;
			for (String value : values) {
				if (selected.equals(value)) {
					combo.select(count);
					break;
				}
				++count;
			}
		}

		toolkit.adapt(combo, true, true);

		return combo;
	}

	private void createButtonsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);

		initConfirmButton(client, true, false);
	}

	@Override
	protected String getOkMessage() {
		if (patientVisit.getId() == null) {
			return MSG_NEW_PATIENT_VISIT_OK;
		}
		return MSG_PATIENT_VISIT_OK;
	}

	@Override
	protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage(getOkMessage(), IMessageProvider.NONE);
			getConfirmButton().setEnabled(true);
		} else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
			getConfirmButton().setEnabled(false);
		}
	}

	@Override
	protected void saveForm() throws Exception {
		if ((patientVisit.getId() == null) && !checkVisitNumberUnique()) {
			setDirty(true);
			return;
		}

		SDKQuery query;
		SDKQueryResult result;

		PatientAdapter patientAdapter = (PatientAdapter) patientVisitAdapter
				.getParent();

		System.out.println("*** patient visit id: " + patientVisit.getId());

		if (patientVisit.getPvInfoDataCollection() != null) {
			for (PvInfoData pvInfoData : patientVisit.getPvInfoDataCollection()) {
				System.out.println("*** id: " + pvInfoData.getId()
						+ ", value: " + pvInfoData.getValue() + ", pv_id: "
						+ pvInfoData.getPatientVisit().getId());
			}
		}

		patientVisit.setPatient(patientAdapter.getPatient());
		savePvInfoData();

		for (PvInfoData pvInfoData : patientVisit.getPvInfoDataCollection()) {
			System.out.println("id: " + pvInfoData.getId() + ", value: "
					+ pvInfoData.getValue() + ", pv_id: "
					+ pvInfoData.getPatientVisit().getId());
		}

		System.out.println("pv data size: "
				+ patientVisit.getPvInfoDataCollection().size());

		if ((patientVisit.getId() == null) || (patientVisit.getId() == 0)) {
			query = new InsertExampleQuery(patientVisit);
		} else {
			query = new UpdateExampleQuery(patientVisit);
		}

		result = appService.executeQuery(query);
		patientVisit = (PatientVisit) result.getObjectResult();

		patientAdapter.performExpand();
		getSite().getPage().closeEditor(this, false);
	}

	private void savePvInfoData() {
		boolean newCollection = false;
		Collection<PvInfoData> pvDataCollection;

		pvDataCollection = patientVisit.getPvInfoDataCollection();
		if (pvDataCollection == null) {
			pvDataCollection = new HashSet<PvInfoData>();
			newCollection = true;
		}

		for (String key : controls.keySet()) {
			CombinedPvInfo pvInfo = (CombinedPvInfo) pvInfoMap.get(key);
			Control control = controls.get(key);
			String value = "";

			if (control instanceof Text) {
				value = ((Text) control).getText();
				System.out.println(key + ": " + ((Text) control).getText());
			} else if (control instanceof Combo) {
				String[] options = pvInfo.pvInfo.getPossibleValues().split(";");
				int index = ((Combo) control).getSelectionIndex();
				if (index >= 0) {
					Assert.isTrue(index < options.length,
							"Invalid combo box selection " + index);
					value = options[index];
					System.out.println(key + ": " + options[index]);
				}
			} else if (control instanceof DatePickerCombo) {
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				Date date = ((DatePickerCombo) control).getDate();
				if (date != null) {
					System.out.println(key + ": " + sdf.format(date));
					value = sdf.format(date);
				}
			}

			PvInfoData pvInfoData = pvInfo.pvInfoData;

			if (pvInfo.pvInfoData == null) {
				pvInfoData = new PvInfoData();
				pvInfoData.setPvInfo(pvInfo.pvInfo);
				pvInfoData.setPatientVisit(patientVisit);
			}
			pvInfoData.setValue(value);

			// pvInfoData.getId()

			if (pvInfo.pvInfoData == null) {
				pvDataCollection.add(pvInfoData);
			}
		}

		if (newCollection) {
			patientVisit.setPvInfoDataCollection(pvDataCollection);
		}
	}

	private boolean checkVisitNumberUnique() throws ApplicationException {
		WritableApplicationService appService = patientVisitAdapter
				.getAppService();
		Patient patient = ((PatientAdapter) patientVisitAdapter.getParent())
				.getPatient();

		HQLCriteria c = new HQLCriteria(
				"from edu.ualberta.med.biobank.model.PatientVisit as v "
						+ "inner join fetch v.patient "
						+ "where v.patient.id='" + patient.getId() + "' "
						+ "and v.number = '" + patientVisit.getNumber() + "'");

		List<Object> results = appService.query(c);

		if (results.size() > 0) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell(),
							"Patient Visit Number Problem",
							"A patient visit with number \""
									+ patientVisit.getNumber()
									+ "\" already exists.");
				}
			});
			return false;
		}

		return true;
	}

	@Override
	protected void cancelForm() {

	}
}
