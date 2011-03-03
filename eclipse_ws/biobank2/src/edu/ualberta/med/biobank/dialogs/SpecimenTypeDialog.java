package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class SpecimenTypeDialog extends BiobankDialog {

    private static final String TITLE = "Specimen Type ";

    private static final String MSG_NO_ST_NAME = "Specimen type must have a name.";
    private static final String MSG_NO_ST_SNAME = "Specimen type must have a short name.";

    private SpecimenTypeWrapper oldSpecimenType;
    private SpecimenTypeWrapper origSpecimenType;

    // this is the object that is modified via the bound widgets
    private SpecimenTypeWrapper specimenType;

    private String message;

    private String currentTitle;

    public SpecimenTypeDialog(Shell parent, SpecimenTypeWrapper specimenType,
        String message) {
        super(parent);
        Assert.isNotNull(specimenType);
        origSpecimenType = specimenType;
        this.specimenType = new SpecimenTypeWrapper(null);
        this.specimenType.setName(specimenType.getName());
        this.specimenType.setNameShort(specimenType.getNameShort());
        this.message = message;
        oldSpecimenType = new SpecimenTypeWrapper(
            SessionManager.getAppService());
        currentTitle = ((origSpecimenType.getName() == null) ? "Add " : "Edit ")
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
            "Name", null, specimenType, "name", new NonEmptyStringValidator(
                MSG_NO_ST_NAME));

        createBoundWidgetWithLabel(content, BiobankText.class, SWT.BORDER,
            "Short Name", null, specimenType, "nameShort",
            new NonEmptyStringValidator(MSG_NO_ST_SNAME));
    }

    @Override
    protected void okPressed() {
        oldSpecimenType.setName(origSpecimenType.getName());
        oldSpecimenType.setNameShort(origSpecimenType.getNameShort());
        origSpecimenType.setName(specimenType.getName());
        origSpecimenType.setNameShort(specimenType.getNameShort());
        super.okPressed();
    }

    public SpecimenTypeWrapper getOrigSpecimenType() {
        return oldSpecimenType;
    }

}
