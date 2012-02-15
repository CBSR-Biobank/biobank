package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridLayout;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;

public class SpecimenListViewForm extends BiobankViewForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenListViewForm"; //$NON-NLS-1$

    private SpecimenInfoTable specimensWidget;

    private List<SpecimenWrapper> specimens;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter == null, "adapter should be null"); //$NON-NLS-1$
        FormInput input = (FormInput) getEditorInput();
        specimens = (List<SpecimenWrapper>) input.getAdapter(ArrayList.class);
        Assert.isNotNull(specimens, "specimens are null"); //$NON-NLS-1$
        setPartName(Messages.SpecimenListViewForm_title);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.SpecimenListViewForm_nonactive_label);
        page.setLayout(new GridLayout(1, false));
        form.setImage(BiobankPlugin.getDefault().getImage(
            BgcPlugin.IMG_SPECIMEN));

        specimensWidget = new SpecimenInfoTable(page, specimens,
            SpecimenInfoTable.ColumnsShown.ALL, 20);
        specimensWidget.adaptToToolkit(toolkit, true);
        specimensWidget.addClickListener(collectionDoubleClickListener);
        specimensWidget.createDefaultEditItem();
    }

    @Override
    public void setValues() throws Exception {
        // TODO Auto-generated method stub

    }

}
