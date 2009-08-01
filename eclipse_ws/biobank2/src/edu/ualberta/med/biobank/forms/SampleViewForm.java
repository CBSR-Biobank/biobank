package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.treeview.AdaptorBase;
import edu.ualberta.med.biobank.treeview.SampleAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SampleViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleViewForm";

    private SampleAdapter sampleAdapter;
    private Sample sample;

    @Override
    public void init(AdaptorBase adaptor) {
        Assert.isTrue((adaptor instanceof SampleAdapter),
            "Invalid editor input: object of type "
                + adaptor.getClass().getName());

        sampleAdapter = (SampleAdapter) adaptor;
        retrieveSample();
        setPartName("Sample: " + sample.getInventoryId());
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
        form.setText("Sample: " + sample.getInventoryId());
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
            string += "No position - should be assign to a location";
        } else {
            string += "Position = "
                + sample.getSamplePosition().getPositionDimensionOne() + ":"
                + sample.getSamplePosition().getPositionDimensionTwo();
        }
        toolkit.createLabel(client, string);
    }

    @Override
    protected void reload() {
        retrieveSample();
        setPartName("Sample: " + sample.getInventoryId());
        form.setText("Sample: " + sample.getInventoryId());
    }

}
