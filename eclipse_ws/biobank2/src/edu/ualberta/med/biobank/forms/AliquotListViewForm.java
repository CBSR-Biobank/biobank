package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridLayout;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.widgets.infotables.AliquotListInfoTable;

public class AliquotListViewForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.AliquotListViewForm";

    private AliquotListInfoTable aliquotsWidget;

    private List<AliquotWrapper> aliquots;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter == null, "adapter should be null");
        FormInput input = (FormInput) getEditorInput();
        aliquots = (List<AliquotWrapper>) input.getAdapter(ArrayList.class);
        Assert.isNotNull(aliquots, "aliquots are null");
        setPartName("Non Active Aliquots");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Non Active Aliquots");
        page.setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getImage(
            BioBankPlugin.IMG_ALIQUOT));

        aliquotsWidget = new AliquotListInfoTable(page, aliquots,
            AliquotListInfoTable.ColumnsShown.PNUMBER);
        aliquotsWidget.adaptToToolkit(toolkit, true);
        aliquotsWidget.addDoubleClickListener(collectionDoubleClickListener);
    }

    @Override
    public void reload() throws Exception {
    }

}
