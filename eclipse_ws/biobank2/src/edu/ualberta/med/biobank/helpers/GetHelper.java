package edu.ualberta.med.biobank.helpers;

import java.lang.reflect.Constructor;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class GetHelper<T> implements Runnable {

	private WritableApplicationService appService;
	
	private List<T> result;
	
	private Class<T> klass;

	public GetHelper(WritableApplicationService appService, Class<T> klass) {
		this.appService = appService;
		this.klass = klass;
	}

	public void run() {	
		try {
			Class<?>[] param = new Class<?>[0];
			Constructor<T> ct = klass.getConstructor(param); 
			Object[] args = new Object[0];
			Object obj = ct.newInstance(args);
			result = appService.search(klass, obj);
		}
		catch (final RemoteConnectFailureException exp) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							"Connection Attempt Failed", 
					"Could not connect to server. Make sure server is running.");
				}
			});
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	public List<T> getResult() {
		return result;
	}
}
