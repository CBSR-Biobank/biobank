package edu.ualberta.med.biobank.forms;

import java.util.HashMap;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.model.Address;

public abstract class AddressViewForm extends BiobankViewForm {
	protected boolean dirty = false;
	
	private HashMap<String, Control> controls;
	
	protected Address address;
		
	protected Button okButton;
	
	public AddressViewForm() {
		super();
		controls = new HashMap<String, Control>();
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}

	protected void createAddressArea(Composite parent) {
		Assert.isNotNull(toolkit);
		Assert.isNotNull(form);
		
		for (String key : AddressFieldsConstants.ORDERED_FIELDS) {
			FieldInfo fi = AddressFieldsConstants.FIELDS.get(key);

			Label field = FormUtils.createLabelledField(toolkit, parent, fi.label + " :");
			controls.put(key, field);
		}
	}
    
    protected void bindValues(DataBindingContext dbc) {
		for (String key : AddressFieldsConstants.FIELDS.keySet()) {
			dbc.bindValue(SWTObservables.observeText(controls.get(key)),
					PojoObservables.observeValue(address, key), null, null);
		}
    }
}
