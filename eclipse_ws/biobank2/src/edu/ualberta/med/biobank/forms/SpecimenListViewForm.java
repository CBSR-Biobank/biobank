package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridLayout;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;

public class SpecimenListViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenListViewForm";

    private SpecimenInfoTable specimensWidget;

    private List<SpecimenWrapper> speicmens;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter == null, "adapter should be null");
        FormInput input = (FormInput) getEditorInput();
        speicmens = (List<SpecimenWrapper>) input.getAdapter(ArrayList.class);
        Assert.isNotNull(speicmens, "specimens are null");
        setPartName("Non active specimens");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Non active Specimens");
        page.setLayout(new GridLayout(1, false));
        form.setImage(BiobankPlugin.getDefault().getImage(
            BiobankPlugin.IMG_SPECIMEN));

        specimensWidget = new SpecimenInfoTable(page, speicmens,
            SpecimenInfoTable.ColumnsShown.ALL, 20);
        specimensWidget.adaptToToolkit(toolkit, true);
        specimensWidget.addClickListener(collectionDoubleClickListener);
    }

    @Override
    public void reload() throws Exception {
    }

}
