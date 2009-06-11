package edu.ualberta.med.biobank.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientGroup;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.treeview.StudyGroup;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SearchView extends ViewPart {

	public static final String ID = "edu.ualberta.med.biobank.views.SearchView";
	private Composite top = null;
	private Button button = null;
	private Text searchField = null;
	private Combo combo = null;
	private ComboViewer comboViewer = null;

	public SearchView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 10;
		top.setLayout(layout);

		createCombo();

		searchField = new Text(top, SWT.BORDER);
		searchField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					search();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});
		GridData gd = new GridData();
		gd.widthHint = 70;
		searchField.setLayoutData(gd);

		button = new Button(top, SWT.NONE);
		button.setText("Search");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search();

			}
		});
		Button button = new Button(top, SWT.NONE);
		button.setText("clear");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SessionManager.getInstance().getTreeFilter().clear();
				SessionManager.getInstance().getTreeViewer().refresh();
				SessionManager.getInstance().getTreeViewer().expandToLevel(
					SessionManager.getInstance().getRootNode(), 2);
			}
		});
	}

	@Override
	public void setFocus() {

	}

	/**
	 * This method initializes combo
	 * 
	 */
	private void createCombo() {
		combo = new Combo(top, SWT.NONE);
		comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setInput(SearchType.values());
	}

	private enum SearchType {
		Site, Study, Patient, Sample, Container;

		public void search(WritableApplicationService appService, String id,
				SessionAdapter sessionAdapter) throws Exception {
			Patient patient = new Patient();
			patient.setNumber(id);
			List<Patient> patients = appService.search(Patient.class, patient);
			if (patients.size() == 1) {
				// StructuredSelection selection = new StructuredSelection(
				// new PatientAdapter(null, patients.get(0)));
				// SessionManager.getInstance().getTreeViewer().setSelection(
				// selection, true);
				// SessionManager.getInstance().getTreeFilter().setSelection(
				// patients.get(0));
				// SessionManager.getInstance().getTreeViewer().refresh(true);

				Patient p = patients.get(0);
				Study study = p.getStudy();
				Site site = study.getSite();
				SiteAdapter siteAdapter = new SiteAdapter(null, site);
				siteAdapter = (SiteAdapter) sessionAdapter
					.getChildByName(siteAdapter.getName());
				StudyGroup studyGroup = (StudyGroup) siteAdapter
					.getChildByName("Studies");
				StudyAdapter studyAdapter = new StudyAdapter(null, study);
				studyAdapter = (StudyAdapter) studyGroup
					.getChildByName(studyAdapter.getName());
				PatientGroup patientGroup = (PatientGroup) studyAdapter
					.getChildByName("Patients");
				PatientAdapter patientAdapter = new PatientAdapter(null, p);
				patientAdapter = (PatientAdapter) patientGroup
					.getChildByName(patientAdapter.getName());
				StructuredSelection selection = new StructuredSelection(
					patientAdapter);
				List liste = new ArrayList();
				liste.add(SessionManager.getInstance().getRootNode());
				liste.add(sessionAdapter);
				liste.add(siteAdapter);
				liste.add(studyGroup);
				liste.add(studyAdapter);
				liste.add(patientGroup);
				liste.add(patientAdapter);
				// selection
				TreeSelection treeSelection = new TreeSelection(new TreePath(
					liste.toArray()));

				SessionManager.getInstance().getTreeViewer().setSelection(
					treeSelection);
			} else {
				throw new Exception("Should not find more than one entry ?");
			}
		}
	}

	private void search() {
		try {
			WritableApplicationService appService = null;
			SessionAdapter sessionAdapter = null;
			IStructuredSelection treeSelection = (IStructuredSelection) SessionManager
				.getInstance().getTreeViewer().getSelection();
			if (treeSelection != null && treeSelection.size() > 0) {
				Node node = (Node) treeSelection.getFirstElement();
				appService = node.getAppService();
				while (sessionAdapter == null) {
					if (node instanceof SessionAdapter) {
						sessionAdapter = (SessionAdapter) node;
					} else {
						node = node.getParent();
					}
				}
			} else {
				getViewSite().getActionBars().getStatusLineManager()
					.setMessage("No selection available for search");
			}
			if (appService == null) {
				// TODO Message d'erreur
			} else {
				IStructuredSelection selection = (IStructuredSelection) comboViewer
					.getSelection();
				if (selection != null && selection.size() > 0) {
					SearchType searchType = (SearchType) selection
						.getFirstElement();
					String id = searchField.getText();
					searchType.search(appService, id, sessionAdapter);
				}
			}
		} catch (Exception ex) {
			// TODO afficher erreur
			ex.printStackTrace();
		}
	}
}
