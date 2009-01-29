package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import java.util.ArrayList;
import java.util.Iterator;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.BackingStoreException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionCredentials;
import edu.ualberta.med.biobank.rcp.Application;

public class LoginForm extends EditorPart {
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.LoginForm";
	
	private ArrayList<String> servers;
	
	private ArrayList<String> userNames;

	private FormToolkit toolkit;
	
	private ScrolledForm form;	

	private Combo serverText;

	private Combo userNameText;

	private Text passwordText;
	
	private static final String SAVED_SERVERS = "savedServers";
	
	private static final String SERVER = "server";

	private static final String LAST_SERVER = "lastServer";
	
	private static final String SAVED_USER_NAMES = "savedUserNames";
	
	private static final String USER_NAME = "userName";

	private static final String LAST_USER_NAME = "lastUserName";
	
	public LoginForm() {  
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

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);	
		
		form.setText("BioBank Login");
		
		Preferences prefs = new ConfigurationScope().getNode(Application.PLUGIN_ID);
		
		Composite contents = form.getBody();
		
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
		for (Iterator<String> it = servers.iterator(); it.hasNext(); ) {
			serverText.add(it.next());
		}

		serverText.select(serverText.indexOf(prefs.get(LAST_SERVER, "")));
		
		Label userNameLabel = new Label(contents, SWT.NONE);
		userNameLabel.setText("&User Name:");
		userNameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		//GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
		//		false);
		//gridData.widthHint = convertHeightInCharsToPixels(20);

		userNameText = new Combo(contents, SWT.BORDER);
		userNameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		for (Iterator<String> it = userNames.iterator(); it.hasNext(); ) {
			userNameText.add(it.next());
		}
		userNameText.select(userNameText.indexOf(prefs.get(LAST_USER_NAME, "")));
		
		Label passwordLabel = new Label(contents, SWT.NONE);
		passwordLabel.setText("&Password:");
		passwordLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		passwordText = new Text(contents, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));
		
		final Button submitButton = toolkit.createButton(contents, "Submit", SWT.FLAT);
		submitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				buttonPressed(IDialogConstants.OK_ID);
				BioBankPlugin.getDefault().createSession();
				getSite().getPage().closeEditor(LoginForm.this, false);
			}
		});
		
		final Button cancelButton = toolkit.createButton(contents, "Cancel", SWT.FLAT);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				buttonPressed(IDialogConstants.CANCEL_ID);
				getSite().getPage().closeEditor(LoginForm.this, false);
			}
		});
	}

	@Override
	public void setFocus() {
		form.setFocus();
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
			
			if (buttonId == IDialogConstants.CANCEL_ID) return;
			
			SessionCredentials sc = new SessionCredentials();			
			
			sc.setServer(serverText.getText());
			sc.setUserName(userNameText.getText());
			sc.setPassword(passwordText.getText());
			
			BioBankPlugin.getDefault().setSessionCredentials(sc);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		buttonPressed(IDialogConstants.OK_ID);
		BioBankPlugin.getDefault().createSession();
	}

	@Override
	public void doSaveAs() {		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

//	protected void okPressed() {
//		if (serverText.getText().equals("")) {
//			MessageDialog.openError(getShell(), "Invalid Server Name",
//			"Server field must not be blank.");
//			return;
//		}
//		if (userNameText.getText().equals("") && !Activator.getDefault().isDebugging()) {
//			MessageDialog.openError(getShell(), "Invalid User Name",
//			"User Name field must not be blank.");
//			return;
//		}
//		super.okPressed();
//	}

}
