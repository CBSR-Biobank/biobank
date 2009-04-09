package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.widgets.Composite;
import edu.ualberta.med.biobank.model.Address;

public abstract class AddressViewFormCommon extends BiobankViewForm {
	
	protected Address address;

	protected void createAddressArea(Composite parent) {		
		createWidgetsFromMap(FormConstants.ADDRESS_FIELDS, address, parent);
	}
}
