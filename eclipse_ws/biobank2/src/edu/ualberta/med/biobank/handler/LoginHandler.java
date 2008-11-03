package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import edu.ualberta.med.biobank.Client;

public class LoginHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		Client client = new Client();
		
		try {
			client.getUsers();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
