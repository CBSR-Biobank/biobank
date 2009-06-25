package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.model.Address;

public abstract class AddressViewFormCommon extends BiobankViewForm {
	
	protected Address address;

	protected void createAddressArea(Composite parent) {		
		createWidgetsFromMap(FormConstants.ADDRESS_FIELDS, parent);
		setAdressValues();
	}
	
	protected void setAdressValues() {
		setWidgetsValues(FormConstants.ADDRESS_FIELDS, address);
	}
	protected void createAddressSection() {   
		Composite client = createSectionWithClient("Address");
        Section section = (Section) client.getParent();
        section.setExpanded(false);
        createAddressArea(client);
	}
}
