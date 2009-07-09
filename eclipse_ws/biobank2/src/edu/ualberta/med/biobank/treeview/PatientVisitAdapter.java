package edu.ualberta.med.biobank.treeview;

import java.text.SimpleDateFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.AddCabinetSampleEntryForm;
import edu.ualberta.med.biobank.forms.AddPaletteSamplesEntryForm;
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
		SimpleDateFormat sdf = new SimpleDateFormat(BioBankPlugin.DATE_FORMAT);
		return sdf.format(patientVisit.getDateDrawn());
	}

	@Override
	public String getTitle() {
		return getTitle("Patient Visit");
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
				closeScannersEditors();
				openForm(new FormInput(PatientVisitAdapter.this),
						AddPaletteSamplesEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Add Cabinet Sample");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				closeCabinetsEditors();
				openForm(new FormInput(PatientVisitAdapter.this),
						AddCabinetSampleEntryForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void loadChildren(boolean updateNode) {
		// try {
		// // read from database again
		// patientVisit = (PatientVisit) ModelUtils.getObjectWithId(
		// getAppService(), PatientVisit.class, patientVisit.getId());
		//
		// Collection<Sample> samples = patientVisit.getSampleCollection();
		//
		// Map<SampleType, List<Sample>> samplesMap = new HashMap<SampleType,
		// List<Sample>>();
		// for (Sample sample : samples) {
		// List<Sample> samplesForType = samplesMap.get(sample.getSampleType());
		// if (samplesForType == null) {
		// samplesForType = new ArrayList<Sample>();
		// samplesMap.put(sample.getSampleType(), samplesForType);
		// }
		// samplesForType.add(sample);
		// }
		// for (SampleType type : samplesMap.keySet()) {
		// SampleTypeAdapter node = (SampleTypeAdapter) getChild(type.getId());
		// if (node == null) {
		// node = new SampleTypeAdapter(this, type);
		// addChild(node);
		// }
		// SessionManager.getInstance().getTreeViewer().update(node, null);
		// for (Sample sample : samplesMap.get(type)) {
		// SampleAdapter sampleNode = (SampleAdapter)
		// node.getChild(sample.getId());
		// if (sampleNode == null) {
		// sampleNode = new SampleAdapter(node, sample);
		// node.addChild(sampleNode);
		// }
		// if (updateNode) {
		// SessionManager.getInstance().getTreeViewer().update(
		// sampleNode, null);
		// }
		// }
		// }
		//
		// }
		// catch (Exception e) {
		// SessionManager.getLogger().error(
		// "Error while loading children of patient visit "
		// + patientVisit.getNumber(), e);
		// }

	}

	@Override
	public Node accept(NodeSearchVisitor visitor) {
		return visitor.visit(this);
	}

}
