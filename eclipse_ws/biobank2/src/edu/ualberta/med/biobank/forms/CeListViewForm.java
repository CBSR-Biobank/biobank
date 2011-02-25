package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridLayout;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.widgets.infotables.CeListInfoTable;

public class CeListViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PvListViewForm";

    private CeListInfoTable patientVisits;

    private List<CollectionEventWrapper> pvs;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter == null, "adapter should be null");
        FormInput input = (FormInput) getEditorInput();
        pvs = (List<CollectionEventWrapper>) input.getAdapter(ArrayList.class);
        Assert.isNotNull(pvs, "aliquots are null");
        setPartName("Patient Visits");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Visits");
        page.setLayout(new GridLayout(1, false));
        // FIXME should we use this icon ?
        form.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_BOX));

        patientVisits = new CeListInfoTable(page, pvs);
        patientVisits.adaptToToolkit(toolkit, true);
        patientVisits.addClickListener(collectionDoubleClickListener);
    }

    @Override
    public void reload() throws Exception {
    }

}
