package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;

public class BatchOpViewForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory
        .getI18n(BatchOpViewForm.class);

    private BgcBaseText typeText;
    private BgcBaseText executedByText;

    @Override
    protected Image getFormImage() {
        return BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_DATABASE_GO);
    }

    @Override
    protected void init() throws Exception {
    }

    @Override
    protected void createFormContent() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        typeText = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Type and Action"));
        executedByText = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Executed By"));
        
        // TODO: add button to download
        // TODO: create specimen table
    }

    @Override
    public void setValues() throws Exception {
        setTextValue(typeText, "asdf");
    }
}
