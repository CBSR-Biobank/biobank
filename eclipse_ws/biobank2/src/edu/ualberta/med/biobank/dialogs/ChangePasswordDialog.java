package edu.ualberta.med.biobank.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.handlers.LogoutHandler;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class ChangePasswordDialog extends TitleAreaDialog {

    Text oldPassText;
    Text newPass1Text;
    Text newPass2Text;

    public ChangePasswordDialog(Shell parentShell) {
        super(parentShell);

        GridLayout gl = new GridLayout(3, true);

        parentShell.setLayout(gl);

        Label lbl = new Label(parentShell, SWT.NONE);
        lbl.setText("Change Password");
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Change Password");
        setMessage("Change your password to something different.");
        return contents;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Change Password");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginTop = 5;
        layout.marginLeft = 2;
        layout.verticalSpacing = 3;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        oldPassText = createPassWordText(contents, "Old Password");
        new Label(contents, SWT.NONE);
        new Label(contents, SWT.NONE); // shhhh! don't tell anyone i did this.
        newPass1Text = createPassWordText(contents, "Password");
        newPass2Text = createPassWordText(contents, "Re-type Password");

        oldPassText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.widget;
                if (text.getText().equals(""))
                    setErrorMessage("Please enter your old password");
                else {
                    setErrorMessage(null);
                }
            }
        });
        newPass1Text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.widget;
                if (text.getText().length() < 1)
                    setErrorMessage("Please enter your new password (atleast 5 characters)");
                else {
                    setErrorMessage(null);
                    newPass2Text.notifyListeners(SWT.Modify, new Event());
                }
            }
        });
        newPass2Text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.widget;
                if (!text.getText().equals(newPass1Text.getText()))
                    setErrorMessage("Please re-enter your new password");
                else {
                    setErrorMessage(null);
                    oldPassText.notifyListeners(SWT.Modify, new Event());
                }
            }
        });

        return contents;
    }

    private Text createPassWordText(Composite parent, String labelText) {
        createLabel(parent, labelText);
        Text text = new Text(parent, SWT.BORDER | SWT.PASSWORD | SWT.FILL);
        text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            false));
        return text;
    }

    @Override
    protected void okPressed() {
        if ((this.newPass1Text.getText().length() < 1)) {
            newPass1Text.notifyListeners(SWT.Modify, new Event());
            return;
        }
        if (!this.newPass1Text.getText().equals(this.newPass2Text.getText())) {
            newPass2Text.notifyListeners(SWT.Modify, new Event());
            return;
        }
        try {

            ((BiobankApplicationService) SessionManager.getAppService())
                .modifyPassword(this.oldPassText.getText(),
                    this.newPass2Text.getText());
            /*
             * TODO instead of logging the user out, update all references to
             * the old password
             */
            LogoutHandler lh = new LogoutHandler();
            lh.execute(null);

            super.okPressed();
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error changing password", e);
        }

    }

    private Label createLabel(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText + ": ");
        label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
            false));
        return label;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control contents = super.createButtonBar(parent);
        return contents;
    }
}