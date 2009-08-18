package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;

public class SampleTypesEntryForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleTypesEntryForm";

    private Site site;
    private Collection<SampleType> sampleTypes;

    @Override
    public void init() {
        site = SessionManager.getInstance().getCurrentSite();
        sampleTypes = site.getSampleTypeCollection();
        createFormContent();
    }

    @Override
    protected void createFormContent() {
        // form.setText("Sample: " + sample.getInventoryId());
        // addRefreshToolbarAction();
        // GridLayout layout = new GridLayout(1, false);
        // form.getBody().setLayout(layout);
        // form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // createInformationSection();
    }

    @Override
    protected void reload() {
        // retrieveSample();
        // setPartName("Sample: " + sample.getInventoryId());
        // form.setText("Sample: " + sample.getInventoryId());
    }

}
