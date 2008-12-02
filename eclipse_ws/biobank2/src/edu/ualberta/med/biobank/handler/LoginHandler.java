package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.Activator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.window.Window;
import edu.ualberta.med.biobank.LoginDialog;
import edu.ualberta.med.biobank.SessionCredentials;
import edu.ualberta.med.biobank.views.SessionsView;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;

public class LoginHandler extends AbstractHandler implements IHandler {
	private SessionCredentials sc;
	ApplicationService appService;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		LoginDialog loginDialog = new LoginDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		if (loginDialog.open() == Window.OK) {
			Activator activator = Activator.getDefault(); 
			//activator.getWsSession().login(activator.getSessionCredentials());
			sc = activator.getSessionCredentials();
			
			IWorkbenchWindow window 
				= PlatformUI.getWorkbench().getActiveWorkbenchWindow();

			Job job = new Job("logging in") {
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Logging in ... ", 100);
					
					try {
						appService = ApplicationServiceProvider.getApplicationServiceFromUrl(
								sc.getServer(), sc.getUserName(), sc.getPassword());
						
						Display.getDefault().asyncExec(new Runnable() {
					          public void run() {
					        	  SessionsView view = Activator.getDefault().getSessionView();
					        	  view.addSession(appService, sc.getServer());
					          }
						});
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
