package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.widgets.infotables.UserInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class EditUsersDialog extends BiobankDialog {
    private final String TITLE = "Edit Users";
    private final String TITLE_AREA_MESSAGE = "Right-click to modify, delete or unlock users.";
    private UserInfoTable userInfoTable;

    public EditUsersDialog(Shell parentShell) {
        super(parentShell);
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

        userInfoTable = new UserInfoTable(contents, null, this);

        List<User> users = new ArrayList<User>();
        for (int i = 0; i < UserInfoTable.ROWS_PER_PAGE + 1; i++) {
            User user = new User();
            user.setLogin("loading...");
            users.add(user);
        }
        userInfoTable.setCollection(users);

        final Display display = userInfoTable.getDisplay();

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    BiobankApplicationService appService = SessionManager
                        .getAppService();
                    final List<User> users = appService.getSecurityUsers();

                    display.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            userInfoTable.setCollection(users);
                        }
                    });
                } catch (ApplicationException e) {
                    BioBankPlugin.openAsyncError("Unable to load users.", e);
                }
            }
        };
        t.start();

        userInfoTable.setEnabled(true);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
            true);
    }
}
