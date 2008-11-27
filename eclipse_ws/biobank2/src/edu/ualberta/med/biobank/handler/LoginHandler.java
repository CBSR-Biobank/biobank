package edu.ualberta.med.biobank.handler;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import edu.ualberta.med.biobank.Activator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import edu.ualberta.med.biobank.LoginDialog;
import edu.ualberta.med.biobank.SessionCredentials;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;

public class LoginHandler extends AbstractHandler implements IHandler {
	private SessionCredentials sc;
	private boolean loginSuccessful; 

	public Object execute(ExecutionEvent event) throws ExecutionException {
		LoginDialog loginDialog = new LoginDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		if (loginDialog.open() == Window.OK) {
			Activator activator = Activator.getDefault(); 
			//activator.getWsSession().login(activator.getSessionCredentials());
			sc = activator.getSessionCredentials();
			
			IWorkbenchWindow window 
				//= HandlerUtil.getActiveWorkbenchWindowChecked(event);
				= PlatformUI.getWorkbench().getActiveWorkbenchWindow();

			ProgressMonitorDialog pd = new ProgressMonitorDialog(window.getShell());
			loginSuccessful = false;
			try {
				pd.run(true /*fork*/, false /*cancelable*/,	new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws
					InvocationTargetException, InterruptedException {
						monitor.beginTask("Logging in ... ", 100);
						
						try {
							ApplicationService appService 
							= ApplicationServiceProvider.getApplicationServiceFromUrl(
									sc.getServer(), sc.getUserName(), sc.getPassword());
							loginSuccessful = true;
						}
						catch (Exception e) {	
						}
						
						monitor.done();
					}
				});
			}
			catch (InvocationTargetException e) {
			} 
			catch (InterruptedException e) {
			}
		}
		return null;
	}
}
