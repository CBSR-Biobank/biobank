package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.infotables.SampleTypeInfoTable;

public class SampleTypesEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleTypesEntryForm";

    private Site site;
    private Collection<SampleType> sampleTypes;

    private SampleTypeInfoTable sampleTypeTable;

    @Override
    public void init() {
        SiteAdapter siteAdapter = (SiteAdapter) adapter;
        site = siteAdapter.getSite();
        sampleTypes = site.getSampleTypeCollection();

    }

    @Override
    protected void createFormContent() {
        createSampleStorageSection();
    }

    private void createSampleStorageSection() {
        Section section = createSection("Sample Types");

        sampleTypeTable = new SampleTypeInfoTable(section, sampleTypes);
        section.setClient(sampleTypeTable);
        sampleTypeTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sampleTypeTable);
    }

    @Override
    public void cancelForm() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getNextOpenedFormID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getOkMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void saveForm() throws Exception {
        // TODO Auto-generated method stub

    }

}
