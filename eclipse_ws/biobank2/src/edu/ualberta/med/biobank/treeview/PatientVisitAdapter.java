package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.LinkSamplesEntryForm;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.PatientVisitViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientVisitAdapter extends Node {

	private PatientVisit patientVisit;

	public PatientVisitAdapter(Node parent, PatientVisit patientVisit) {
		super(parent);
		this.patientVisit = patientVisit;
	}

	public PatientVisit getPatientVisit() {
		return patientVisit;
	}

	@Override
	public Integer getId() {
		Assert.isNotNull(patientVisit, "patientVisit is null");
		return patientVisit.getId();
	}

	@Override
	public String getName() {
		Assert.isNotNull(patientVisit, "patientVisit is null");
		return patientVisit.getNumber();
	}

	@Override
	public void performDoubleClick() {
		openForm(new FormInput(this), PatientVisitViewForm.ID);
	}

	@Override
	public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
		MenuItem mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Edit Visit");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				openForm(new FormInput(PatientVisitAdapter.this),
					PatientVisitEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("View Visit");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				openForm(new FormInput(PatientVisitAdapter.this),
					PatientVisitViewForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Add Palette Samples");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				openForm(new FormInput(PatientVisitAdapter.this),
					LinkSamplesEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Add Cabinet Sample");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println("add cabinet sample form");
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}
}
