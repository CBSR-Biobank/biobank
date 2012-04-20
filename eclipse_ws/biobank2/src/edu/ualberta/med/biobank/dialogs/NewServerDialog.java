package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class NewServerDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(MoveSpecimensToDialog.class);

    @SuppressWarnings("nls")
    // TR: dialog title
    private static final String TITLE = i18n.tr("New Server");

    protected String serverAddress;

    public NewServerDialog(Shell parentShell) {
        super(parentShell);
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // TR: dialog title area message
        return i18n.tr("Enter the domain-name or IP address of the server:");
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        area.setLayout(layout);
        area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        NonEmptyStringValidator validator = new NonEmptyStringValidator(
            // TR: validation error message
            i18n.tr("Server field cannot be empty"));
        BgcBaseText textBox = (BgcBaseText) createBoundWidgetWithLabel(area,
            BgcBaseText.class, SWT.NONE,
            // TR: label
            i18n.trc("url", "Address"),
            new String[0], this,
            "serverAddress", validator);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        textBox.setLayoutData(gd);
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected void okPressed() {
        this.close();
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String text) {
        this.serverAddress = text;
    }

}
