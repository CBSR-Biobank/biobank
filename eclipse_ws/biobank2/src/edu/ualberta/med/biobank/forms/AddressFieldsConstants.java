package edu.ualberta.med.biobank.forms;

import java.util.HashMap;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.validators.EmailAddress;
import edu.ualberta.med.biobank.validators.PostalCode;
import edu.ualberta.med.biobank.validators.TelephoneNumber;

@SuppressWarnings("serial")
public class AddressFieldsConstants {
	public static final String[]  ORDERED_FIELDS = new String[] {
		"street1",
		"street2",
		"city",
		"province",
		"postalCode",
		"phoneNumber",
		"faxNumber",
		"email"
	};
	
	public static final HashMap<String, FieldInfo> FIELDS = 
		new HashMap<String, FieldInfo>() {{
			put("street1",     new FieldInfo("Street 1",      Text.class,  null,  null));
			put("street2",     new FieldInfo("Street 2",      Text.class,  null,  null));
			put("city",        new FieldInfo("City",          Text.class,  null,  null));
			put("province",    new FieldInfo("Province",      Combo.class, null,  null));
			put("postalCode",  new FieldInfo("Postal Code",   Text.class,  PostalCode.class, "Invalid postal code"));
			put("phoneNumber", new FieldInfo("Phone Number",  Text.class,  TelephoneNumber.class, "Telephone number is invalid"));
			put("faxNumber",   new FieldInfo("Fax Number",    Text.class,  TelephoneNumber.class, "Telephone number is invalid"));
			put("email",       new FieldInfo("Email Address", Text.class,  EmailAddress.class, "Email address is invalid"));
		}
	};
		
	// used to select a list	
	public static final String[] PROVINCES = new String[] {
			"Alberta", 
			"British Columbia",
			"Manitoba",
			"New Brunswick",
			"Newfoundland and Labrador",
			"Northwest Territories",
			"Nova Scotia",
			"Nunavut",
			"Ontario",
			"Prince Edward Island",
			"Quebec",
			"Saskatchewan",
			"Yukon"
	};	
	
	private AddressFieldsConstants() {
	}

}
