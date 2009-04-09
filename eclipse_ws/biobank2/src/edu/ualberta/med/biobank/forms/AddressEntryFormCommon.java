package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.model.Address;

public abstract class AddressEntryFormCommon extends BiobankEntryForm {
    
	protected Address address;

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
        super.init(site, input);
	}

	protected void createAddressArea() {
	    Composite client = createSectionWithClient("Address");
        createWidgetsFromMap(FormConstants.ADDRESS_FIELDS, address, client);  
	}
    
    protected abstract void handleStatusChanged(IStatus severity);
}
