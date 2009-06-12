package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;

public class PatientGroup extends Node {

	public PatientGroup(StudyAdapter parent, int id) {
		super(parent, id, "Patients", true);
	}

	@Override
	public void performDoubleClick() {
		performExpand();
	}

	@Override
	public void performExpand() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				loadChildren();
				SessionManager.getInstance().getTreeViewer().expandToLevel(
					PatientGroup.this, 1);
			}
		});
	}

	@Override
	public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
		MenuItem mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Add Patient");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				PatientAdapter adapter = new PatientAdapter(PatientGroup.this,
					new Patient());
				openForm(new FormInput(adapter), PatientEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void loadChildren() {
		Study parentStudy = ((StudyAdapter) getParent()).getStudy();
		Assert.isNotNull(parentStudy, "null study");
		try {
			// read from database again
			Study searchStudy = new Study();
			searchStudy.setId(parentStudy.getId());
			List<Study> result = getAppService().search(Study.class,
				searchStudy);
			Assert.isTrue(result.size() == 1);
			parentStudy = result.get(0);
			((StudyAdapter) getParent()).setStudy(parentStudy);

			Collection<Patient> patients = parentStudy.getPatientCollection();

			for (Patient patient : patients) {
				PatientAdapter node = (PatientAdapter) getChild(patient.getId());

				if (node == null) {
					node = new PatientAdapter(this, patient);
					addChild(node);
				}

				SessionManager.getInstance().getTreeViewer().update(node, null);
			}

		} catch (Exception e) {
			SessionManager.getLogger().error(
				"Error while loading patient group children for study "
						+ parentStudy.getName(), e);
		}
	}

	@Override
	public Node accept(NodeSearchVisitor visitor) {
		return visitor.visit(this);
	}
}
