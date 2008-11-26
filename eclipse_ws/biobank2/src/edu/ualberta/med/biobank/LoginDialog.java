package edu.ualberta.med.biobank;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.MessageDialog;
import java.util.ArrayList;
import java.util.Iterator;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.BackingStoreException;

public class LoginDialog extends Dialog {
	
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
	
	public LoginDialog(Shell parentShell) {			
		super(parentShell);	
		
		servers = new ArrayList<String>();
		userNames = new ArrayList<String>();
		
		Preferences prefs = new ConfigurationScope().getNode(Application.PLUGIN_ID);
		Preferences prefsServers = prefs.node(SAVED_SERVERS);		
		Preferences prefsUserNames = prefs.node(SAVED_USER_NAMES);
		
		try {
			String[] serverNodeNames = prefsServers.childrenNames();
			for (String serverNodeName : serverNodeNames) {
				Preferences node = prefsServers.node(serverNodeName);
				servers.add(node.get(SERVER, ""));
			}
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
		
		try {
			String[] userNodeNames = prefsUserNames.childrenNames();
			for (String userNodeName : userNodeNames) {
				Preferences node = prefsUserNames.node(userNodeName);
				userNames.add(node.get(USER_NAME, ""));
			}
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	protected Control createDialogArea(Composite parent) {
		Preferences prefs = new ConfigurationScope().getNode(Application.PLUGIN_ID);
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setText("&Server:");
		serverLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		serverText = new Combo(composite, SWT.BORDER);
		serverText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));
		for (Iterator<String> it = servers.iterator(); it.hasNext(); ) {
			serverText.add(it.next());
		}

		serverText.select(serverText.indexOf(prefs.get(LAST_SERVER, "")));
		

		Label userNameLabel = new Label(composite, SWT.NONE);
		userNameLabel.setText("&User Name:");
		userNameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				false);
		gridData.widthHint = convertHeightInCharsToPixels(20);

		userNameText = new Combo(composite, SWT.BORDER);
		userNameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		for (Iterator<String> it = userNames.iterator(); it.hasNext(); ) {
			userNameText.add(it.next());
		}
		userNameText.select(userNameText.indexOf(prefs.get(LAST_USER_NAME, "")));
		
		Label passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setText("&Password:");
		passwordLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));
		
		return composite;
	}
	
	protected void buttonPressed(int buttonId) {
		if ((buttonId == IDialogConstants.OK_ID)
				|| (buttonId == IDialogConstants.CANCEL_ID)) {			
			Preferences prefs = new ConfigurationScope().getNode(Application.PLUGIN_ID);
			prefs.put(LAST_SERVER, serverText.getText());
			prefs.put(LAST_USER_NAME, userNameText.getText());
			
			if ((serverText.getSelectionIndex() == -1)
					&& !servers.contains(serverText.getText())) {
				Preferences prefsServers = prefs.node(SAVED_SERVERS);
				Preferences prefsServer  = prefsServers.node(Integer.toString(servers.size()));				
				prefsServer.put(SERVER, serverText.getText());
			}
			
			if ((userNameText.getSelectionIndex() == -1)
					&& !userNames.contains(userNameText.getText())) {
				Preferences prefsUserNames = prefs.node(SAVED_USER_NAMES);
				Preferences prefsUserName  = prefsUserNames.node(Integer.toString(userNames.size()));				
				prefsUserName.put(USER_NAME, userNameText.getText());
			}
			
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}

			
			SessionCredentials sc = new SessionCredentials();			
			
			sc.setServer(serverText.getText());
			sc.setUserName(userNameText.getText());
			sc.setPassword(passwordText.getText());
			
			Activator.getDefault().setSessionCredentials(sc);
		}
		super.buttonPressed(buttonId);
	}

	protected void okPressed() {
		if (serverText.getText().equals("")) {
			MessageDialog.openError(getShell(), "Invalid Server Name",
			"Server field must not be blank.");
			return;
		}
		if (userNameText.getText().equals("")) {
			MessageDialog.openError(getShell(), "Invalid User Name",
			"User Name field must not be blank.");
			return;
		}
		super.okPressed();
	}

}
