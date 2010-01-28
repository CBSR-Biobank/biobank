package edu.ualberta.med.biobank.dialogs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.helpers.SessionHelper;
import edu.ualberta.med.biobank.rcp.Application;

public class LoginDialog extends TitleAreaDialog {

    private ArrayList<String> servers;

    private ArrayList<String> userNames;

    private Combo serverText;

    private Combo userNameText;

    private Text passwordText;

    private static final String SAVED_SERVERS = "savedServers";

    private static final String SERVER = "server";

    private static final String LAST_SERVER = "lastServer";

    private static final String SAVED_USER_NAMES = "savedUserNames";

    private static final String USER_NAME = "userName";

    private static final String LAST_USER_NAME = "lastUserName";

    private static final Logger logger = Logger.getLogger(LoginDialog.class
        .getName());

    public Preferences pluginPrefs = null;

    private Button secureConnectionButton;

    public LoginDialog(Shell parentShell) {
        super(parentShell);

        servers = new ArrayList<String>();
        userNames = new ArrayList<String>();

        pluginPrefs = new InstanceScope().getNode(Application.PLUGIN_ID);
        Preferences prefsServers = pluginPrefs.node(SAVED_SERVERS);
        Preferences prefsUserNames = pluginPrefs.node(SAVED_USER_NAMES);

        try {
            String[] serverNodeNames = prefsServers.childrenNames();
            for (String serverNodeName : serverNodeNames) {
                Preferences node = prefsServers.node(serverNodeName);
                servers.add(node.get(SERVER, ""));
            }
        } catch (BackingStoreException e) {
            logger.error("Could not get " + SERVER + " preference", e);
        }

        try {
            String[] userNodeNames = prefsUserNames.childrenNames();
            for (String userNodeName : userNodeNames) {
                Preferences node = prefsUserNames.node(userNodeName);
                userNames.add(node.get(USER_NAME, ""));
            }
        } catch (BackingStoreException e) {
            logger.error("Could not get " + USER_NAME + " preference", e);
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("BioBank Login");
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Login to a BioBank server");
        setTitleImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_COMPUTER_KEY));
        setMessage("Enter server name and login details.");
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);

        Composite contents = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label serverLabel = new Label(contents, SWT.NONE);
        serverLabel.setText("&Server:");
        serverLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
            false, false));

        serverText = new Combo(contents, SWT.BORDER);
        serverText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));
        for (Iterator<String> it = servers.iterator(); it.hasNext();) {
            serverText.add(it.next());
        }
        serverText.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String lastServer = serverText.getText();
                secureConnectionButton
                    .setSelection(lastServer.contains("8443"));
            }
        });

        String lastServer = pluginPrefs.get(LAST_SERVER, "");
        serverText.select(serverText.indexOf(lastServer));

        secureConnectionButton = null;
        if (BioBankPlugin.getDefault().isDebugging()) {
            new Label(contents, SWT.NONE);

            secureConnectionButton = new Button(contents, SWT.CHECK);
            secureConnectionButton.setText("Use secure connection");
            secureConnectionButton.setSelection(lastServer.contains("8443"));
        }

        Label userNameLabel = new Label(contents, SWT.NONE);
        userNameLabel.setText("&User Name:");
        userNameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
            false, false));

        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
            false);
        gridData.widthHint = convertHeightInCharsToPixels(20);

        userNameText = new Combo(contents, SWT.BORDER);
        userNameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));

        for (Iterator<String> it = userNames.iterator(); it.hasNext();) {
            userNameText.add(it.next());
        }
        userNameText.select(userNameText.indexOf(pluginPrefs.get(
            LAST_USER_NAME, "")));

        Label passwordLabel = new Label(contents, SWT.NONE);
        passwordLabel.setText("&Password:");
        passwordLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
            false, false));

        passwordText = new Text(contents, SWT.BORDER | SWT.PASSWORD);
        passwordText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
            true, false));

        return contents;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if ((buttonId == IDialogConstants.OK_ID)
            || (buttonId == IDialogConstants.CANCEL_ID)) {
            pluginPrefs.put(LAST_SERVER, serverText.getText());
            pluginPrefs.put(LAST_USER_NAME, userNameText.getText());

            if ((serverText.getText().length() > 0)
                && (serverText.getSelectionIndex() == -1)
                && !servers.contains(serverText.getText())) {
                Preferences prefsServers = pluginPrefs.node(SAVED_SERVERS);
                Preferences prefsServer = prefsServers.node(Integer
                    .toString(servers.size()));
                prefsServer.put(SERVER, serverText.getText().trim());
            }

            if ((userNameText.getText().length() > 0)
                && (userNameText.getSelectionIndex() == -1)
                && !userNames.contains(userNameText.getText())) {
                Preferences prefsUserNames = pluginPrefs.node(SAVED_USER_NAMES);
                Preferences prefsUserName = prefsUserNames.node(Integer
                    .toString(userNames.size()));
                prefsUserName.put(USER_NAME, userNameText.getText().trim());
            }

            try {
                pluginPrefs.flush();
            } catch (BackingStoreException e) {
                logger.error("Could not save loggin preferences", e);
            }
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void okPressed() {
        try {
            new URL("http://" + serverText.getText());
        } catch (MalformedURLException e) {
            MessageDialog.openError(getShell(), "Invalid Server URL",
                "Please enter a valid server URL.");
            return;
        }

        if (userNameText.getText().equals("")
            && !BioBankPlugin.getDefault().isDebugging()) {
            MessageDialog.openError(getShell(), "Invalid User Name",
                "User Name field must not be blank.");
            return;
        }

        boolean secureConnection = ((secureConnectionButton == null) || secureConnectionButton
            .getSelection());

        SessionHelper sessionHelper = new SessionHelper(serverText.getText(),
            secureConnection, userNameText.getText(), passwordText.getText());

        BusyIndicator.showWhile(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell().getDisplay(), sessionHelper);

        Collection<SiteWrapper> sites = sessionHelper.getSites();
        String realUsername = sessionHelper.getUserName();

        if (sites != null) {
            // login successful
            SessionManager.getInstance().addSession(
                sessionHelper.getAppService(), serverText.getText(),
                realUsername, sites);
        }
        super.okPressed();
    }
}
