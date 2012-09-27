package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class SampleTypeDialog extends BiobankDialog {

    private static final String TITLE = "Sample Type ";

    private static final String MSG_NO_ST_NAME = "Sample type must have a name.";
    private static final String MSG_NO_ST_SNAME = "Sample type must have a short name.";

    private SampleTypeWrapper oldSampleType;
    private SampleTypeWrapper origSampleType;

    // this is the object that is modified via the bound widgets
    private SampleTypeWrapper sampleType;

    private String message;

    public SampleTypeDialog(Shell parent, SampleTypeWrapper sampleType,
        String message) {
        super(parent);
        Assert.isNotNull(sampleType);
        origSampleType = sampleType;
        this.sampleType = new SampleTypeWrapper(null);
        this.sampleType.setName(sampleType.getName());
        this.sampleType.setNameShort(sampleType.getNameShort());
        this.message = message;
        oldSampleType = new SampleTypeWrapper(SessionManager.getAppService());
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(((origSampleType.getName() == null) ? "Add " : "Edit ")
            + TITLE);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if (origSampleType.getName() == null) {
            setTitle("Add Sample Type");
        } else {
            setTitle("Edit Sample Type");
        }
        setMessage(message);
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(content, BiobankText.class, SWT.BORDER,
            "Name", null, PojoObservables.observeValue(sampleType, "name"),
            new NonEmptyStringValidator(MSG_NO_ST_NAME));

        createBoundWidgetWithLabel(content, BiobankText.class, SWT.BORDER,
            "Short Name", null, PojoObservables.observeValue(sampleType,
                "nameShort"), new NonEmptyStringValidator(MSG_NO_ST_SNAME));
    }

    @Override
    protected void okPressed() {
        oldSampleType.setName(origSampleType.getName());
        oldSampleType.setNameShort(origSampleType.getNameShort());
        origSampleType.setName(sampleType.getName());
        origSampleType.setNameShort(sampleType.getNameShort());
        super.okPressed();
    }

    public SampleTypeWrapper getOrigSampleType() {
        return oldSampleType;
    }

}