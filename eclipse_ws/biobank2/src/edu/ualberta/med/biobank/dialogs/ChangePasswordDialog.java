package edu.ualberta.med.biobank.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.handlers.LogoutHandler;

public class ChangePasswordDialog extends TitleAreaDialog {
    public static final int MIN_PASSWORD_LENGTH = 5;

    private boolean forceChange;
    private Text oldPassText;
    private Text newPass1Text;
    private Text newPass2Text;

    public ChangePasswordDialog(Shell parentShell) {
        super(parentShell);
    }

    public ChangePasswordDialog(Shell parentShell, boolean forceChange) {
        this(parentShell);

        this.forceChange = forceChange;
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
                if (text.getText().length() < MIN_PASSWORD_LENGTH)
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
    protected void cancelPressed() {
        if (forceChange) {
            return;
        }

        super.cancelPressed();
    }

    @Override
    public boolean close() {
        if (!forceChange || getReturnCode() == OK)
            return super.close();
        else
            return false;
    }

    @Override
    protected void okPressed() {
        if ((this.newPass1Text.getText().length() < MIN_PASSWORD_LENGTH)) {
            newPass1Text.notifyListeners(SWT.Modify, new Event());
            return;
        }
        if (!this.newPass1Text.getText().equals(this.newPass2Text.getText())) {
            newPass2Text.notifyListeners(SWT.Modify, new Event());
            return;
        }
        try {
            SessionManager.getAppService().modifyPassword(
                this.oldPassText.getText(), this.newPass2Text.getText());

            SessionManager.getInstance().getSession().resetAppService();
            BgcPlugin
                .openInformation(
                    "Password modified",
                    "Your password has been successfully changed. You will need to reconnect again to see your data");

            if (forceChange && newPass1Text.getText().isEmpty()) {
                return;
            }

            LogoutHandler lh = new LogoutHandler();
            lh.execute(null);
            // FIXME find a way to reconnect the user automatically ?
            super.okPressed();
        } catch (Exception e) {
            BgcPlugin.openAsyncError("Error changing password", e);
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

        if (forceChange) {
            getButton(IDialogConstants.CANCEL_ID).setEnabled(false);
        }

        return contents;
    }
}