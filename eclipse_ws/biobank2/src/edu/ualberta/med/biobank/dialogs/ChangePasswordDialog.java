package edu.ualberta.med.biobank.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
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
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.handlers.LogoutHandler;

public class ChangePasswordDialog extends BgcBaseDialog {

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
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.ChangePasswordDialog_title);
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
            Messages.ChangePasswordDialog_oldPassword_label);
        new Label(contents, SWT.NONE);
        new Label(contents, SWT.NONE); // shhhh! don't tell anyone i did this.
        newPass1Text = createPassWordText(contents,
            Messages.ChangePasswordDialog_password_label);
        newPass2Text = createPassWordText(contents,
            Messages.ChangePasswordDialog_password_retype_label);

        oldPassText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.widget;
                if (text.getText().equals("")) //$NON-NLS-1$
                    setErrorMessage(Messages.ChangePasswordDialog_oldPassword_msg);
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
                    setErrorMessage(Messages.ChangePasswordDialog_password_length_msg);
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
                    setErrorMessage(Messages.ChangePasswordDialog_password_reenter_msg);
                else {
                    setErrorMessage(null);
                    oldPassText.notifyListeners(SWT.Modify, new Event());
                }
            }
        });
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
            BgcPlugin.openInformation(
                Messages.ChangePasswordDialog_success_title,
                Messages.ChangePasswordDialog_success_msg);

            if (forceChange && newPass1Text.getText().isEmpty()) {
                return;
            }

            LogoutHandler lh = new LogoutHandler();
            lh.execute(null);
            // FIXME find a way to reconnect the user automatically ?
            super.okPressed();
        } catch (Exception e) {
            BgcPlugin.openAsyncError(Messages.ChangePasswordDialog_error_title,
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
        return Messages.ChangePasswordDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return Messages.ChangePasswordDialog_title;
    }

    @Override
    protected String getDialogShellTitle() {
        return Messages.ChangePasswordDialog_title;
    }

}