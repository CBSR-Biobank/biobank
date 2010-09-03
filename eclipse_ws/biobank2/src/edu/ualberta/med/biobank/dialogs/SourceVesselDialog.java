package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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

    private String currentTitle;

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
        currentTitle = ((origSourceVessel.getName() == null) ? "Add " : "Edit ")
            + TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return message;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
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
