package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.validators.NonEmptyString;

public class SampleTypeDialog extends BiobankDialog {

    private static final String TITLE = "Sample Type ";

    private static final String MSG_NO_ST_NAME = "Sample type must have a name.";
    private static final String MSG_NO_ST_SNAME = "Sample type must have a short name.";

    private SampleType sampleType;

    public SampleTypeDialog(Shell parent, SampleType sampleType) {
        super(parent);
        Assert.isNotNull(sampleType);
        this.sampleType = sampleType;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        Integer id = sampleType.getId();
        String title = new String();

        if (id == null) {
            title = "Add";
        } else {
            title = "Edit ";
        }
        title += TITLE;
        shell.setText(title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite client = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        client.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Control c = createBoundWidgetWithLabel(client, Text.class, SWT.BORDER,
            "Name", null, PojoObservables.observeValue(sampleType, "name"),
            new NonEmptyString(MSG_NO_ST_NAME));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 200;
        c.setLayoutData(gd);

        createBoundWidgetWithLabel(client, Text.class, SWT.BORDER,
            "Short Name", null, PojoObservables.observeValue(sampleType,
                "nameShort"), new NonEmptyString(MSG_NO_ST_SNAME));

        return client;
    }

    public SampleType getSampleType() {
        return sampleType;
    }

}
