package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.SampleViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Sample;

public class SampleAdapter extends AdaptorBase {

	private Sample sample;

	public SampleAdapter(AdaptorBase parent, Sample sample) {
		super(parent);
		this.sample = sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public Sample getSample() {
		return sample;
	}

	@Override
	public void addChild(AdaptorBase child) {
		Assert.isTrue(false, "Cannot add children to this adapter");
	}

	@Override
	public Integer getId() {
		Assert.isNotNull(sample, "Sample is null");
		return sample.getId();
	}

	@Override
	public String getName() {
		Assert.isNotNull(sample, "Clinic is null");
		return sample.getInventoryId();
	}

	@Override
	public String getTitle() {
		return getTitle("Sample");
	}

	@Override
	public void performDoubleClick() {
		openForm(new FormInput(this), SampleViewForm.ID);
	}

	@Override
	public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
		MenuItem mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("View Sample");
		mi.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				openForm(new FormInput(SampleAdapter.this), SampleViewForm.ID);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void loadChildren(boolean updateNode) {
	}

	@Override
	public AdaptorBase accept(NodeSearchVisitor visitor) {
		return null;
	}

}
