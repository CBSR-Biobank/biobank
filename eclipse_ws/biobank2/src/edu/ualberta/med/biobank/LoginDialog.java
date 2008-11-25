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

public class LoginDialog extends Dialog {

	private Text serverText;

	private Combo userNameText;

	private Text passwordText;
	
	private static final String PASSWORD = "password";

	private static final String SERVER = "server";

	private static final String SAVED = "saved-connections";

	private static final String LAST_USER = "prefs_last_connection";


	public LoginDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Label accountLabel = new Label(composite, SWT.NONE);
		accountLabel.setText("Account details:");
		accountLabel.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.CENTER, false, false, 2, 1));

		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setText("&Server:");
		serverLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		serverText = new Text(composite, SWT.BORDER);
		serverText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));

		Label userIdLabel = new Label(composite, SWT.NONE);
		userIdLabel.setText("&User Name:");
		userIdLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));

		userNameText = new Combo(composite, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				false);
		gridData.widthHint = convertHeightInCharsToPixels(20);
		userNameText.setLayoutData(gridData);
		
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
		String userId = userNameText.getText();
		String server = serverText.getText();
		String password = passwordText.getText();
		connectionDetails = new ConnectionDetails(userId, server, password);
		savedDetails.put(userId, connectionDetails);
		if (buttonId == IDialogConstants.OK_ID
				|| buttonId == IDialogConstants.CANCEL_ID)
			saveDescriptors();
		super.buttonPressed(buttonId);
	}
}
