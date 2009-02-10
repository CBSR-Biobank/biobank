package edu.ualberta.med.biobank.forms;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import edu.ualberta.med.biobank.model.Address;

public abstract class AddressEntryForm extends EditorPart {
	
	private HashMap<String, Control> controls;
		
	private HashMap<String, ControlDecoration> fieldDecorators;

	protected boolean dirty = false;

	protected FormToolkit toolkit;
	
	protected Form form;
	
	protected Address address;
		
	protected Button okButton;

	protected IStatus currentStatus;
	
	protected KeyListener keyListener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {
			if ((e.keyCode & SWT.MODIFIER_MASK) == 0) {
				setDirty(true);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {			
		}
	};
	
	public AddressEntryForm() {
		super();
		controls = new HashMap<String, Control>();
		fieldDecorators = new HashMap<String, ControlDecoration>();
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

	protected void createAddressArea() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Address");
		section.setFont(FormUtils.getSectionFont());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 2;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);
		
		for (String key : AddressFieldsConstants.ORDERED_FIELDS) {
			FieldInfo fi = AddressFieldsConstants.FIELDS.get(key);
			
			if (fi.widgetClass == Text.class) {
				Text text = FormUtils.createLabelledText(toolkit, sbody, fi.label + " :", 100, null);
				controls.put(key, text);
				text.addKeyListener(keyListener);
				
				if (fi.validatorClass != null) {
					fieldDecorators.put(key, FormUtils.createDecorator(text, fi.errMsg));
				}
			}
			else if (fi.widgetClass == Combo.class) {
				toolkit.createLabel(sbody, fi.label + " :", SWT.LEFT);
				Combo combo = new Combo(sbody, SWT.READ_ONLY);
				if (key.equals("province")) {
					combo.setItems(AddressFieldsConstants.PROVINCES);
				}
				combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				toolkit.adapt(combo, true, true);
				controls.put(key, combo);
				
				combo.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						setDirty(true);
					}
				});
			}
			else {
				assert false : fi.widgetClass;
			}
		}
	}
	
    protected void bindValues(DataBindingContext dbc) {
		for (String key : AddressFieldsConstants.FIELDS.keySet()) {
			FieldInfo fi = AddressFieldsConstants.FIELDS.get(key);
			UpdateValueStrategy uvs = null;

			if (fi.widgetClass == Text.class) {				
				if (fi.validatorClass != null) {
					try {
						Class<?>[] types = new Class[] { String.class, ControlDecoration.class };				
						Constructor<?> cons = fi.validatorClass.getConstructor(types);
						Object[] args = new Object[] { fi.errMsg, fieldDecorators.get(key) };
						uvs = new UpdateValueStrategy();
						uvs.setAfterConvertValidator((IValidator) cons.newInstance(args));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				dbc.bindValue(SWTObservables.observeText(controls.get(key), SWT.Modify),
						PojoObservables.observeValue(address, key), uvs, null);
			}
			else if (fi.widgetClass == Combo.class) {
		    	dbc.bindValue(SWTObservables.observeSelection(controls.get(key)),
		    			PojoObservables.observeValue(address, "province"), null, null);
			}
			else {
				Assert.isTrue(false, "Invalid class " + fi.widgetClass.getName());
			}
		}       
		
		IObservableValue statusObservable = new WritableValue();
		statusObservable.addChangeListener(new IChangeListener() {
			public void handleChange(ChangeEvent event) {
				IObservableValue validationStatus 
					= (IObservableValue) event.getSource(); 
				handleStatusChanged((IStatus) validationStatus.getValue());
			}
		}); 
		
		dbc.bindValue(statusObservable, new AggregateValidationStatus(
                dbc.getBindings(), AggregateValidationStatus.MAX_SEVERITY),
                null, null); 
    }
    
    protected abstract void handleStatusChanged(IStatus severity);
}
