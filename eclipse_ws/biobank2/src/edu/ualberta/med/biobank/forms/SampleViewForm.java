package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.treeview.SampleAdapter;

public class SampleViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleViewForm";

    private SampleAdapter sampleAdapter;
    private SampleWrapper sample;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof SampleAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        sampleAdapter = (SampleAdapter) adapter;
        sample = sampleAdapter.getSample();
        retrieveSample();
        setPartName("Sample: " + sample.getInventoryId());
    }

    private void retrieveSample() {
        try {
            sample.reload();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Can't reload sample with id " + sample.getId());
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Sample: " + sample.getInventoryId());
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
            string += "Position = " + sample.getSamplePosition().getRow() + ":"
                + sample.getSamplePosition().getCol();
        }
        toolkit.createLabel(client, string);
    }

    @Override
    protected void reload() {
        retrieveSample();
        setPartName("Sample: " + sample.getInventoryId());
        form.setText("Sample: " + sample.getInventoryId());
    }

    @Override
    protected String getEntryFormId() {
        return null;
    }

}
