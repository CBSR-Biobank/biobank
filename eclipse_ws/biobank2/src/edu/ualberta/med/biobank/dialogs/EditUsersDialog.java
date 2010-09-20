package edu.ualberta.med.biobank.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.widgets.infotables.UserInfoTable;

public class EditUsersDialog extends BiobankDialog {
    private final String TITLE = "Edit Users";
    private final String TITLE_AREA_MESSAGE = "Modify and/or delete users.";
    private UserInfoTable userInfoTable;
    private List<User> users;

    public EditUsersDialog(Shell parentShell, List<User> users) {
        super(parentShell);

        this.users = users;
    }

    @Override
    protected String getTitleAreaMessage() {
        return TITLE_AREA_MESSAGE;
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        userInfoTable = new UserInfoTable(contents, null);
        userInfoTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (userInfoTable.getSelection() != null) {
                }
            }
        });
        userInfoTable.setCollection(users);
        userInfoTable.setEnabled(true);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
}
