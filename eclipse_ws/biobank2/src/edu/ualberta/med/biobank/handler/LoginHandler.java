package edu.ualberta.med.biobank.handler;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

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
				= PlatformUI.getWorkbench().getActiveWorkbenchWindow();

			loginSuccessful = false;
			Job job = new Job("logging in") {
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Logging in ... ", 100);
					
					try {
						ApplicationService appService 
						= ApplicationServiceProvider.getApplicationServiceFromUrl(
								sc.getServer(), sc.getUserName(), sc.getPassword());
						loginSuccessful = true;
					}
					catch (Exception e) {	
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		return null;
	}
}
