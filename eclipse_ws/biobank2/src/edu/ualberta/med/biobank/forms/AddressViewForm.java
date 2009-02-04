package edu.ualberta.med.biobank.forms;

import java.util.HashMap;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import edu.ualberta.med.biobank.model.Address;

public abstract class AddressViewForm extends EditorPart {
	protected boolean dirty = false;

	protected FormToolkit toolkit;
	
	protected Form form;
	
	private HashMap<String, Control> controls;
	
	protected Address address;
		
	protected Button okButton;
	
	public AddressViewForm() {
		super();
		controls = new HashMap<String, Control>();
	}

	public void doSave(IProgressMonitor monitor) {
		setDirty(false);
	}
	
	public void doSaveAs() {
	}
	
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setDirty(false);
	}
	
	public boolean isDirty() {
		return dirty;
	}

	protected void setDirty(boolean d) {
		dirty = d;
		firePropertyChange(ISaveablePart.PROP_DIRTY);
	}

	protected void createAddressArea(Composite parent) {
		Assert.isNotNull(toolkit);
		Assert.isNotNull(form);
		
		for (String key : AddressFieldsConstants.ORDERED_FIELDS) {
			FieldInfo fi = AddressFieldsConstants.FIELDS.get(key);

			Label field = createLabelledField(parent, fi.label + " :", 100, null);
			controls.put(key, field);
		}
	}
	
	protected Label createLabelledField(Composite parent, String label, int limit, String tip) {
		toolkit.createLabel(parent, label, SWT.LEFT);
        Label field = toolkit.createLabel(parent, "", SWT.SINGLE);
        field.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        return field;
    }
    
    protected void bindValues(DataBindingContext dbc) {
		for (String key : AddressFieldsConstants.FIELDS.keySet()) {
			dbc.bindValue(SWTObservables.observeText(controls.get(key)),
					PojoObservables.observeValue(address, key), null, null);
		}
    }
}
