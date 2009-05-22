package edu.ualberta.med.biobank.helpers;

import edu.ualberta.med.biobank.BioBankPlugin;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.lang.reflect.Constructor;
import java.util.List;

import org.springframework.remoting.RemoteConnectFailureException;

public class GetHelper<T> {

	public List<T> getModelObjects(WritableApplicationService appService, 
	    Class<T> klass) {	
		try {
			Class<?>[] param = new Class<?>[0];
			Constructor<T> ct = klass.getConstructor(param); 
			Object[] args = new Object[0];
			Object obj = ct.newInstance(args);
			return appService.search(klass, obj);
		}
		catch (final RemoteConnectFailureException exp) {
			BioBankPlugin.openRemoteConnectErrorMessage();			
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
        return null;
	}
}
