package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PatientVisitData;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyInfo;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class PatientVisitViewForm extends BiobankViewForm {

	class PatientVisitInfo {
		StudyInfo studyInfo;
		PatientVisitData pvData;

		public PatientVisitInfo() {
			studyInfo = null;
			pvData = null;
		}
	}

	public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitViewForm";

	private PatientVisitAdapter patientVisitAdapter;

	private PatientVisit patientVisit;

	private ListOrderedMap pvInfoMap;

	public PatientVisitViewForm() {
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
		appService = patientVisitAdapter.getAppService();
		patientVisit = patientVisitAdapter.getPatientVisit();

		if (patientVisit.getId() == null) {
			setPartName("New Visit");
		} else {
			setPartName("Visit " + patientVisit.getNumber());
		}
	}

	@Override
	protected void createFormContent() {
		form.setText("Visit: " + patientVisit.getNumber());
		form.getBody().setLayout(new GridLayout(1, false));
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		addRefreshToolbarAction();

		createVisitSection();

	}

	private void createVisitSection() {
		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);

		Study study = ((StudyAdapter) patientVisitAdapter.getParent()
			.getParent().getParent()).getStudy();

		for (StudyInfo studyInfo : study.getStudyInfoCollection()) {
			PatientVisitInfo pvInfo = new PatientVisitInfo();
			pvInfo.studyInfo = studyInfo;
			pvInfoMap.put(studyInfo.getStudyInfoType().getType(), pvInfo);
		}

		Collection<PatientVisitData> pvDataCollection = patientVisit
			.getPatientVisitDataCollection();
		if (pvDataCollection != null) {
			for (PatientVisitData pvData : pvDataCollection) {
				String key = pvData.getStudyInfo().getStudyInfoType().getType();
				PatientVisitInfo pvInfo = (PatientVisitInfo) pvInfoMap.get(key);
				pvInfo.pvData = pvData;
			}
		}

		Label widget;
		MapIterator it = pvInfoMap.mapIterator();
		while (it.hasNext()) {
			String label = (String) it.next();
			PatientVisitInfo pvInfo = (PatientVisitInfo) it.getValue();
			String value = "";
			int typeId = pvInfo.studyInfo.getStudyInfoType().getId();

			if (pvInfo.pvData != null) {
				value = pvInfo.pvData.getValue();
			}

			Label labelWidget = toolkit.createLabel(client, label + ":",
				SWT.LEFT);
			labelWidget.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_BEGINNING));
			widget = toolkit.createLabel(client, value, SWT.BORDER | SWT.LEFT);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			if (typeId == 4) {
				gd.heightHint = 40;
			}
			widget.setLayoutData(gd);
		}

	}

	@Override
	protected void reload() {

	}

}
