package edu.ualberta.med.biobank.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.dialogs.startup.LoginDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.handlers.LogoutHandler;

public class ChangePasswordDialog extends BgcBaseDialog {

    public static final int MIN_PASSWORD_LENGTH = 5;

    private boolean forceChange;
    private Text oldPassText;
    private Text newPass1Text;
    private Text newPass2Text;

    private Button checkBulk;

    public ChangePasswordDialog(Shell parentShell) {
        super(parentShell);
    }

    public ChangePasswordDialog(Shell parentShell, boolean forceChange) {
        this(parentShell);
        this.forceChange = forceChange;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Change Password");
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginTop = 5;
        layout.marginLeft = 2;
        layout.verticalSpacing = 3;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        oldPassText = createPassWordText(contents,
            "Old Password");
        new Label(contents, SWT.NONE);
        new Label(contents, SWT.NONE); // shhhh! don't tell anyone i did this.
        newPass1Text = createPassWordText(contents,
            "Password");
        newPass2Text = createPassWordText(contents,
            "Re-type Password");

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
                    setErrorMessage("Please enter your new password (at least 5 characters)");
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

        if (forceChange) {
            // will ask about bulk email on the same time
            checkBulk = new Button(contents, SWT.CHECK);
            checkBulk
                .setText("I want to receive emails about new versions");
            checkBulk
                .setSelection(SessionManager.getUser().getRecvBulkEmails());
        }
    }

    private Text createPassWordText(Composite parent, String labelText) {
        widgetCreator.createLabel(parent, labelText);
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
        return false;
    }

    @Override
    protected void okPressed() {
        if ((newPass1Text.getText().length() < MIN_PASSWORD_LENGTH)) {
            newPass1Text.notifyListeners(SWT.Modify, new Event());
            return;
        }
        if (!newPass1Text.getText().equals(this.newPass2Text.getText())) {
            newPass2Text.notifyListeners(SWT.Modify, new Event());
            return;
        }
        try {
            SessionManager.getUser().modifyPassword(oldPassText.getText(),
                newPass2Text.getText(),
                checkBulk == null ? null : checkBulk.getSelection());

            SessionManager.getInstance().getSession().resetAppService();
            BgcPlugin.openInformation(
                "Password modified",
                "Your password has been successfully changed. You will need to reconnect again to see your data");

            if (forceChange && newPass1Text.getText().isEmpty()) {
                return;
            }

            LogoutHandler lh = new LogoutHandler();
            lh.execute(null);

            // FIXME find a way to reconnect the user automatically ?
            super.okPressed();

            LoginDialog ld = new LoginDialog(getShell());

            ld.open();
        } catch (Exception e) {
            BgcPlugin.openAsyncError("Error changing password",
                e);
        }

    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control contents = super.createButtonBar(parent);

        if (forceChange) {
            getButton(IDialogConstants.CANCEL_ID).setEnabled(false);
        }

        return contents;
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Change your password to something different.";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Change Password";
    }

    @Override
    protected String getDialogShellTitle() {
        return "Change Password";
    }

}