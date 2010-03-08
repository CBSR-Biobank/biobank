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

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;

public class SampleTypeDialog extends BiobankDialog {

    private static final String TITLE = "Aliquot Type ";

    private static final String MSG_NO_ST_NAME = "Aliquot type must have a name.";
    private static final String MSG_NO_ST_SNAME = "Aliquot type must have a short name.";

    private SampleTypeWrapper origSampleType;

    // this is the object that is modified via the bound widgets
    private SampleTypeWrapper sampleType;

    private String message;

    public SampleTypeDialog(Shell parent, SampleTypeWrapper sampleType,
        String message) {
        super(parent);
        Assert.isNotNull(sampleType);
        origSampleType = sampleType;
        this.sampleType = new SampleTypeWrapper(sampleType.getAppService());
        this.sampleType.setName(sampleType.getName());
        this.sampleType.setNameShort(sampleType.getNameShort());
        this.message = message;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(((origSampleType.getId() == null) ? "Add " : "Edit ")
            + TITLE);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if (origSampleType.isNew()) {
            setTitle("Add Aliquot Type");
        } else {
            setTitle("Edit Aliquot Type");
        }
        setMessage(message);
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(content, Text.class, SWT.BORDER, "Name",
            null, PojoObservables.observeValue(sampleType, "name"),
            new NonEmptyStringValidator(MSG_NO_ST_NAME));

        createBoundWidgetWithLabel(content, Text.class, SWT.BORDER,
            "Short Name", null, PojoObservables.observeValue(sampleType,
                "nameShort"), new NonEmptyStringValidator(MSG_NO_ST_SNAME));
    }

    @Override
    protected void okPressed() {
        origSampleType.setName(sampleType.getName());
        origSampleType.setNameShort(sampleType.getNameShort());
        super.okPressed();
    }

    public SampleTypeWrapper getSampleType() {
        return sampleType;
    }

}
