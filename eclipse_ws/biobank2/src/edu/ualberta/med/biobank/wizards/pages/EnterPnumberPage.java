package edu.ualberta.med.biobank.wizards.pages;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcWizardPage;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class EnterPnumberPage extends BgcWizardPage {
    public static final String PAGE_NAME = EnterPnumberPage.class
        .getCanonicalName();
    private static final String PATIENT_NUMBER_REQUIRED = "Please enter a valid patient number.";
    private String pnumber;

    public EnterPnumberPage() {
        super(PAGE_NAME, "Enter a patient number", null);
    }

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        getWidgetCreator().createBoundWidgetWithLabel(content,
            BgcBaseText.class, SWT.BORDER,
            "Patient Number", null,
            PojoObservables.observeValue(this, PatientPeer.PNUMBER.getName()),
            new NonEmptyStringValidator(PATIENT_NUMBER_REQUIRED));

        setControl(content);
    }
}