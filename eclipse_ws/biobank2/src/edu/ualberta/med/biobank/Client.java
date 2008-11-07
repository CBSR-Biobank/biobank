package edu.ualberta.med.biobank;

import java.util.Collection;
import java.lang.reflect.Method;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.applicationservice.ApplicationService;

public class Client {	
	public void getUsers() throws Exception {
		ApplicationService appService 
			//= ApplicationServiceProvider.getApplicationService();
			= ApplicationServiceProvider.getApplicationServiceFromUrl(
					"http://localhost:8080/biobank2", "biobank", "changeme");
		
		User user = new User();
		System.out.println(" searching domain object");

		Collection<Object> results = appService.search(User.class, user);
		for (Object obj : results) {
			printObject(obj, User.class);
		}
	}	
	
	private void printObject(Object obj, Class klass) throws Exception {
		System.out.println("Printing "+ klass.getName());
		
		Method[] methods = klass.getMethods();
		for (Method method:methods) {
			if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
				System.out.print("\t"+method.getName().substring(3)+":");
				Object val = method.invoke(obj, (Object[])null);
				if (val instanceof java.util.Set)
					System.out.println("size="+((Collection)val).size());
				else
					System.out.println(val);
			}
		}
	}


}
