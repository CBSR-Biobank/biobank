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
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.window.Window;
import edu.ualberta.med.biobank.LoginDialog;
import edu.ualberta.med.biobank.SessionCredentials;
import edu.ualberta.med.biobank.views.SessionsView;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;

public class LoginHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		LoginDialog loginDialog = new LoginDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		if (loginDialog.open() == Window.OK) {
			Activator activator = Activator.getDefault();
			final SessionCredentials sc = activator.getSessionCredentials();

			Job job = new Job("logging in") {
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Logging in ... ", 100);
					
					try {
						final ApplicationService appService;
						final String userName = sc.getUserName(); 
						
						if (userName.length() == 0) {
							appService = ApplicationServiceProvider.getApplicationServiceFromUrl(
									"http://" + sc.getServer() + "/biobank2");
							
						}
						else {
							appService = ApplicationServiceProvider.getApplicationServiceFromUrl(
									"http://" + sc.getServer() + "/biobank2", userName, sc.getPassword());
						}
						
						Display.getDefault().asyncExec(new Runnable() {
					          public void run() {
					        	  SessionsView view = Activator.getDefault().getSessionView();
					        	  view.addSession(appService, sc.getServer());
					          }
						});
					}
					catch (Exception e) {	
						System.out.println(">>>" + e.getMessage());
						
						Display.getDefault().asyncExec(new Runnable() {
					          public void run() {
					        	  Activator.getDefault().getSessionView().loginFailed(sc);
					          }
						});
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
