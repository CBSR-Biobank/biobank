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
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class SourceVesselDialog extends BiobankDialog {

    private static final String TITLE = "Source Vessel ";

    private static final String MSG_NO_ST_NAME = "Source vessel must have a name.";

    private SourceVesselWrapper origSourceVessel;

    // this is the object that is modified via the bound widgets
    private SourceVesselWrapper sourceVessel;

    private String message;

    private SourceVesselWrapper oldSourceVessel;

    public SourceVesselDialog(Shell parent, SourceVesselWrapper sourceVessel,
        String message) {
        super(parent);
        Assert.isNotNull(sourceVessel);
        origSourceVessel = sourceVessel;
        this.sourceVessel = new SourceVesselWrapper(null);
        this.sourceVessel.setName(sourceVessel.getName());
        this.message = message;
        oldSourceVessel = new SourceVesselWrapper(
            SessionManager.getAppService());
    }

    @Override
    protected String getDialogShellTitle() {
        return ((origSourceVessel.getName() == null) ? "Add " : "Edit ")
            + TITLE;
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if (origSourceVessel.getName() == null) {
            setTitle("Add Source Vessel");
        } else {
            setTitle("Edit Source Vessel");
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
            "Name", null, PojoObservables.observeValue(sourceVessel, "name"),
            new NonEmptyStringValidator(MSG_NO_ST_NAME));

    }

    @Override
    protected void okPressed() {
        oldSourceVessel.setName(origSourceVessel.getName());
        origSourceVessel.setName(sourceVessel.getName());
        super.okPressed();
    }

    public SourceVesselWrapper getOrigSourceVessel() {
        return oldSourceVessel;
    }

}
