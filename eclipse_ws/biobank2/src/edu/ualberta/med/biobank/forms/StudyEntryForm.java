package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyInfo;
import edu.ualberta.med.biobank.model.Worksheet;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.MultiSelect;
import edu.ualberta.med.biobank.widgets.StudyInfoWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

@SuppressWarnings("serial")
public class StudyEntryForm extends BiobankEntryForm {
	public static final String ID = "edu.ualberta.med.biobank.forms.StudyEntryForm";

	private static final String MSG_NEW_STUDY_OK = "Creating a new study.";

	private static final String MSG_STUDY_OK = "Editing an existing study.";

	public static final String[] ORDERED_FIELDS = new String[] { "name",
			"nameShort", "activityStatus", "comment" };

	public static final ListOrderedMap FIELDS = new ListOrderedMap() {
		{
			put("name", new FieldInfo("Name", Text.class, SWT.NONE, null,
				NonEmptyString.class, "Study name cannot be blank"));
			put("nameShort", new FieldInfo("Short Name", Text.class, SWT.NONE,
				null, NonEmptyString.class, "Study short name cannot be blank"));
			put("activityStatus", new FieldInfo("Activity Status", Combo.class,
				SWT.NONE, FormConstants.ACTIVITY_STATUS, null, null));
			put("comment", new FieldInfo("Comments", Text.class, SWT.MULTI,
				null, null, null));
		}
	};

	private MultiSelect clinicsMultiSelect;

	private StudyAdapter studyAdapter;

	private Study study;

	private Site site;

	private Collection<Clinic> allClinics;

	private Collection<StudyInfo> allStudyInfos;

	private TreeMap<String, StudyInfoWidget> studyInfoWidgets;

	public StudyEntryForm() {
		super();
		studyInfoWidgets = new TreeMap<String, StudyInfoWidget>();
	}

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {

		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");

		Assert
			.isTrue((node instanceof StudyAdapter),
				"Invalid editor input: object of type "
						+ node.getClass().getName());

		studyAdapter = (StudyAdapter) node;
		study = studyAdapter.getStudy();
		study.setWorksheet(new Worksheet());
		site = ((SiteAdapter) studyAdapter.getParent().getParent()).getSite();

		if (study.getId() == null) {
			setPartName("New Study");
		} else {
			setPartName("Study " + study.getName());
		}
	}

	@Override
	protected void createFormContent() {
		form.setText("Study Information");
		form.setMessage(getOkMessage(), IMessageProvider.NONE);
		form.getBody().setLayout(new GridLayout(1, false));

		Composite client = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		client.setLayout(layout);
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);

		createWidgetsFromMap(FIELDS, study, client);
		Text comments = (Text) controls.get("comment");
		GridData gd = (GridData) comments.getLayoutData();
		gd.heightHint = 40;
		// comments.setLayoutData(gd);

		createClinicSection();
		createStudyInfoSection();
		createButtonsSection();
	}

	private void createClinicSection() {
		Composite client = createSectionWithClient("Available Clinics");
		Collection<Clinic> studyClinics = study.getClinicCollection();
		allClinics = site.getClinicCollection();

		ListOrderedMap availClinics = new ListOrderedMap();
		List<Integer> selClinics = new ArrayList<Integer>();

		if (studyClinics != null) {
			for (Clinic clinic : studyClinics) {
				selClinics.add(clinic.getId());
			}
		}

		for (Clinic clinic : allClinics) {
			availClinics.put(clinic.getId(), clinic.getName());
		}

		clinicsMultiSelect = new MultiSelect(client, SWT.NONE,
			"Selected Clinics", "Available Clinics", 100);
		clinicsMultiSelect.adaptToToolkit(toolkit);
		clinicsMultiSelect.addSelections(availClinics, selClinics);
	}

	private void createStudyInfoSection() {
		Composite client = createSectionWithClient("Study Information Selection");
		// Collection<StudyInfo> siCollection = study.getStudyInfoCollection();
		// HashMap<Integer, StudyInfo> selected = new HashMap<Integer,
		// StudyInfo>();
		// GridLayout gl = (GridLayout) client.getLayout();
		// gl.numColumns = 1;
		//
		// if (siCollection != null) {
		// for (StudyInfo studyInfo : siCollection) {
		// selected.put(studyInfo.getStudyInfoType().getId(), studyInfo);
		// }
		// }

		allStudyInfos = getAllStudyInfos();
		Assert.isNotNull(allStudyInfos);

		for (StudyInfo studyInfo : allStudyInfos) {
			String value = "";
			boolean itemSelected = false;
			// StudyInfo studyInfo = selected.get(studyInfoType.getId());
			// if (studyInfo != null) {
			// itemSelected = true;
			// value = studyInfo.getPossibleValues();
			// }

			StudyInfoWidget w = new StudyInfoWidget(client, SWT.NONE, studyInfo
				.getStudyInfoType(), itemSelected, value);
			w.adaptToToolkit(toolkit);
			studyInfoWidgets.put(studyInfo.getLabel(), w);
		}
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

	private String getOkMessage() {
		if (study.getId() == null) {
			return MSG_NEW_STUDY_OK;
		}
		return MSG_STUDY_OK;
	}

	@Override
	protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage(getOkMessage(), IMessageProvider.NONE);
			confirmButton.setEnabled(true);
		} else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
			confirmButton.setEnabled(false);
		}
	}

	@Override
	protected void saveForm() {
		try {
			if ((study.getId() == null) && !checkStudyNameUnique()) {
				setDirty(true);
				return;
			}

			// get the selected clinics from widget
			List<Integer> selClinicIds = clinicsMultiSelect.getSelected();
			Set<Clinic> selClinics = new HashSet<Clinic>();
			for (Clinic clinic : allClinics) {
				int id = clinic.getId();
				if (selClinicIds.indexOf(id) >= 0) {
					selClinics.add(clinic);
				}

			}
			Assert.isTrue(selClinics.size() == selClinicIds.size(),
				"problem with clinic selections");
			study.setClinicCollection(selClinics);

			List<StudyInfo> studyInfoList = new ArrayList<StudyInfo>();
			for (StudyInfo studyInfo : allStudyInfos) {
				String type = studyInfo.getStudyInfoType().getType();
				String value = studyInfoWidgets.get(type).getResult();
				if ((value.length() == 0) || value.equals("no"))
					continue;
				if (value.equals("yes")) {
					value = "";
				}
				studyInfo.setPossibleValues(value);
				studyInfoList.add(studyInfo);
			}
			study.setStudyInfoCollection(studyInfoList);

			saveStudy(study);
			studyAdapter.getParent().performExpand();
			getSite().getPage().closeEditor(this, false);
		} catch (RemoteAccessException exp) {
			BioBankPlugin.openRemoteAccessErrorMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveStudy(Study study) throws ApplicationException {
		SDKQuery query;
		SDKQueryResult result;
		Set<StudyInfo> savedStudyInfoList = new HashSet<StudyInfo>();

		study.setSite(site);
		study.setWorksheet(null);

		if (study.getStudyInfoCollection().size() > 0) {
			for (StudyInfo studyInfo : study.getStudyInfoCollection()) {
				if ((studyInfo.getId() == null) || (studyInfo.getId() == 0)) {
					query = new InsertExampleQuery(studyInfo);
				} else {
					query = new UpdateExampleQuery(studyInfo);
				}

				result = studyAdapter.getAppService().executeQuery(query);
				savedStudyInfoList.add((StudyInfo) result.getObjectResult());
			}
		}
		study.setStudyInfoCollection(savedStudyInfoList);

		if ((study.getId() == null) || (study.getId() == 0)) {
			query = new InsertExampleQuery(study);
		} else {
			query = new UpdateExampleQuery(study);
		}

		result = studyAdapter.getAppService().executeQuery(query);
		study = (Study) result.getObjectResult();
	}

	private List<StudyInfo> getAllStudyInfos() {
		StudyInfo criteria = new StudyInfo();

		try {
			return studyAdapter.getAppService().search(StudyInfo.class,
				criteria);
		} catch (final RemoteConnectFailureException exp) {
			BioBankPlugin.openRemoteConnectErrorMessage();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return null;
	}

	private boolean checkStudyNameUnique() throws ApplicationException {
		WritableApplicationService appService = studyAdapter.getAppService();
		Site site = ((SiteAdapter) studyAdapter.getParent().getParent())
			.getSite();

		HQLCriteria c = new HQLCriteria(
			"from edu.ualberta.med.biobank.model.Study as study "
					+ "inner join fetch study.site " + "where study.site.id='"
					+ site.getId() + "' " + "and study.name = '"
					+ study.getName() + "'");

		List<Object> results = appService.query(c);

		if (results.size() > 0) {
			BioBankPlugin
				.openAsyncError("Study Name Problem", "A study with name \""
						+ study.getName() + "\" already exists.");
			return false;
		}

		c = new HQLCriteria(
			"from edu.ualberta.med.biobank.model.Study as study "
					+ "inner join fetch study.site " + "where study.site.id='"
					+ site.getId() + "' " + "and study.nameShort = '"
					+ study.getNameShort() + "'");

		results = appService.query(c);

		if (results.size() > 0) {
			BioBankPlugin.openAsyncError("Study Name Problem",
				"A study with short name \"" + study.getName()
						+ "\" already exists.");
			return false;
		}

		return true;
	}

	@Override
	protected void cancelForm() {
		// TODO Auto-generated method stub

	}
}
