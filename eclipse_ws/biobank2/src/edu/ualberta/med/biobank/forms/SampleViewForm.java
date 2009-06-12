package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SampleAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SampleViewForm extends BiobankViewForm {

	public static final String ID = "edu.ualberta.med.biobank.forms.SampleViewForm";

	private SampleAdapter sampleAdapter;
	private Sample sample;

	private Label label;

	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		if (node instanceof SampleAdapter) {
			sampleAdapter = (SampleAdapter) node;
			retrieveSample();
			setPartName("Sample: " + sample.getInventoryId());
		} else {
			Assert.isTrue(false, "Invalid editor input: object of type "
					+ node.getClass().getName());
		}
	}

	private void retrieveSample() {
		List<Sample> result;
		Sample searchSample = new Sample();
		searchSample.setId(sampleAdapter.getSample().getId());
		try {
			result = sampleAdapter.getAppService().search(Sample.class,
				searchSample);
			Assert.isTrue(result.size() == 1);
			sample = result.get(0);
			sampleAdapter.setSample(sample);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void createFormContent() {
		if (sample.getInventoryId() != null) {
			form.setText("Sample: " + sample.getInventoryId());
		}

		addRefreshToolbarAction();

		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createInformationSection();
	}

	private void createInformationSection() {
		Composite client = createSectionWithClient("Information");
		GridLayout layout = new GridLayout(1, false);
		client.setLayout(layout);

		String string = "Type = " + sample.getSampleType().getName() + "\n";
		if (sample.getSamplePosition() == null) {
			string += "No position - should be processed";
		} else {
			string += "Position = "
					+ sample.getSamplePosition().getPositionDimensionOne()
					+ ":"
					+ sample.getSamplePosition().getPositionDimensionTwo();
		}
		label = toolkit.createLabel(client, string);
	}

	@Override
	protected void reload() {
		retrieveSample();
		setPartName("Sample: " + sample.getInventoryId());
		form.setText("Sample: " + sample.getInventoryId());
	}

}
