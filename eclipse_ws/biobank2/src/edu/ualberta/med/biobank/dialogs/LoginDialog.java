package edu.ualberta.med.biobank.dialogs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
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
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.rcp.Application;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;

public class LoginDialog extends TitleAreaDialog {

    private DataBindingContext dbc;

    private ArrayList<String> servers;

    private ArrayList<String> userNames;

    private Combo serverWidget;

    private Combo userNameWidget;

    private Text passwordWidget;

    private static final String SAVED_SERVERS = "savedServers";

    private static final String SERVER = "server";

    private static final String LAST_SERVER = "lastServer";

    private static final String SAVED_USER_NAMES = "savedUserNames";

    private static final String USER_NAME = "userName";

    private static final String LAST_USER_NAME = "lastUserName";

    private static final BiobankLogger logger = BiobankLogger
        .getLogger(LoginDialog.class.getName());

    public Preferences pluginPrefs = null;

    private Button secureConnectionButton;

    private Authentication authentication;

    private Boolean okButtonEnabled;

    private boolean setupFinished = false;

    public LoginDialog(Shell parentShell) {
        super(parentShell);

        authentication = new Authentication();

        dbc = new DataBindingContext();

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
    protected Control createButtonBar(Composite parent) {
        Control contents = super.createButtonBar(parent);
        if (okButtonEnabled != null) {
            // in case the binding wanted to modify it before its creation
            setOkButtonEnabled(okButtonEnabled);
        }
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);

        Composite contents = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        String lastServer = pluginPrefs.get(LAST_SERVER, "");
        NonEmptyStringValidator validator = new NonEmptyStringValidator(
            "Server field cannot be empty");
        serverWidget = createWritableCombo(contents, "&Server", servers
            .toArray(new String[0]), "server", lastServer, validator);

        NonEmptyStringValidator userNameValidator = null;
        NonEmptyStringValidator passwordValidator = null;
        if (BioBankPlugin.getDefault().isDebugging()) {
            new Label(contents, SWT.NONE);
            secureConnectionButton = new Button(contents, SWT.CHECK);
            secureConnectionButton.setText("Use secure connection");
            secureConnectionButton.setSelection(lastServer.contains("8443"));

            serverWidget.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String lastServer = serverWidget.getText();
                    secureConnectionButton.setSelection(lastServer
                        .contains("8443"));
                }
            });
        } else {
            userNameValidator = new NonEmptyStringValidator(
                "Username field cannot be empty");
            passwordValidator = new NonEmptyStringValidator(
                "Password field cannot be empty");
        }

        userNameWidget = createWritableCombo(contents, "&User Name", userNames
            .toArray(new String[0]), "username", pluginPrefs.get(
            LAST_USER_NAME, ""), userNameValidator);

        passwordWidget = createPassWordText(contents, "&Password", "password",
            passwordValidator);

        bindChangeListener();

        setupFinished = true;

        return contents;
    }

    private Text createPassWordText(Composite parent, String labelText,
        String propertyObserved, AbstractValidator validator) {
        createLabel(parent, labelText);
        Text text = new Text(parent, SWT.BORDER | SWT.PASSWORD);
        arrangeAndBindControl(text, validator, SWTObservables.observeText(text,
            SWT.Modify), propertyObserved);
        return text;
    }

    private void arrangeAndBindControl(Control control,
        AbstractValidator validator, ISWTObservableValue observable,
        String propertyObserved) {
        control.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
            false));
        UpdateValueStrategy uvs = null;
        if (validator != null) {
            uvs = new UpdateValueStrategy();
            uvs.setAfterGetValidator(validator);
        }
        dbc.bindValue(observable, PojoObservables.observeValue(authentication,
            propertyObserved), uvs, null);

    }

    private Label createLabel(Composite parent, String labelText) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText + ":");
        label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
            false));
        return label;
    }

    private Combo createWritableCombo(Composite parent, String labelText,
        String[] values, String propertyObserved, String selection,
        AbstractValidator validator) {
        createLabel(parent, labelText);

        Combo combo = new Combo(parent, SWT.BORDER);
        combo.setItems(values);
        if (selection != null) {
            combo.select(combo.indexOf(selection));
        }
        arrangeAndBindControl(combo, validator, SWTObservables
            .observeSelection(combo), propertyObserved);
        return combo;
    }

    protected void bindChangeListener() {
        final IObservableValue statusObservable = new WritableValue();
        statusObservable.addChangeListener(new IChangeListener() {
            public void handleChange(ChangeEvent event) {
                IObservableValue validationStatus = (IObservableValue) event
                    .getSource();
                IStatus status = (IStatus) validationStatus.getValue();
                if (status.getSeverity() == IStatus.OK) {
                    setErrorMessage(null);
                    setOkButtonEnabled(true);
                } else {
                    if (setupFinished) {
                        setErrorMessage(status.getMessage());
                    }
                    setOkButtonEnabled(false);
                }
            }
        });
        dbc.bindValue(statusObservable, new AggregateValidationStatus(dbc
            .getBindings(), AggregateValidationStatus.MAX_SEVERITY));
    }

    protected void setOkButtonEnabled(boolean enabled) {
        Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null && !okButton.isDisposed()) {
            okButton.setEnabled(enabled);
        } else {
            okButtonEnabled = enabled;
        }
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if ((buttonId == IDialogConstants.OK_ID)
            || (buttonId == IDialogConstants.CANCEL_ID)) {
            pluginPrefs.put(LAST_SERVER, serverWidget.getText());
            pluginPrefs.put(LAST_USER_NAME, userNameWidget.getText());

            if ((serverWidget.getText().length() > 0)
                && (serverWidget.getSelectionIndex() == -1)
                && !servers.contains(serverWidget.getText())) {
                Preferences prefsServers = pluginPrefs.node(SAVED_SERVERS);
                Preferences prefsServer = prefsServers.node(Integer
                    .toString(servers.size()));
                prefsServer.put(SERVER, serverWidget.getText().trim());
            }

            if ((userNameWidget.getText().length() > 0)
                && (userNameWidget.getSelectionIndex() == -1)
                && !userNames.contains(userNameWidget.getText())) {
                Preferences prefsUserNames = pluginPrefs.node(SAVED_USER_NAMES);
                Preferences prefsUserName = prefsUserNames.node(Integer
                    .toString(userNames.size()));
                prefsUserName.put(USER_NAME, userNameWidget.getText().trim());
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
            new URL("http://" + serverWidget.getText());
        } catch (MalformedURLException e) {
            MessageDialog.openError(getShell(), "Invalid Server URL",
                "Please enter a valid server URL. Ex. hostname:port");
            return;
        }

        if (!serverWidget.getText().contains(":")) {
            MessageDialog.openError(getShell(), "Invalid Server URL",
                "Please enter a valid server URL. Ex. hostname:port");
            return;
        }

        if (userNameWidget.getText().equals("")
            && !BioBankPlugin.getDefault().isDebugging()) {
            MessageDialog.openError(getShell(), "Invalid User Name",
                "User Name field must not be blank.");
            return;
        }

        boolean secureConnection = ((secureConnectionButton == null) || secureConnectionButton
            .getSelection());

        SessionHelper sessionHelper = new SessionHelper(serverWidget.getText(),
            secureConnection, userNameWidget.getText(), passwordWidget
                .getText());

        BusyIndicator.showWhile(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell().getDisplay(), sessionHelper);

        Collection<SiteWrapper> sites = sessionHelper.getSites();
        String realUsername = sessionHelper.getUserName();

        if (sites != null) {
            // login successful
            SessionManager.getInstance().addSession(
                sessionHelper.getAppService(), serverWidget.getText(),
                realUsername, sites);
        }
        super.okPressed();
    }

    @SuppressWarnings("unused")
    private class Authentication {

        public String server;
        public String username;
        public String password;

        public void setServer(String server) {
            this.server = server;
        }

        public String getServer() {
            return server;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return server + "/" + username + "/" + password;
        }
    }
}
