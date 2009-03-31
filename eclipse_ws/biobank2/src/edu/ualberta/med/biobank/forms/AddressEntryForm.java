package edu.ualberta.med.biobank.forms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.model.Address;

public abstract class AddressEntryForm extends BiobankEditForm {
	
	private HashMap<String, Control> controls;
		
	private HashMap<String, ControlDecoration> fieldDecorators;
	
	protected Address address;
		
	protected Button okButton;

	protected IStatus currentStatus;
	
	public AddressEntryForm() {
		super();
		controls = new HashMap<String, Control>();
		fieldDecorators = new HashMap<String, ControlDecoration>();
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
        super.init(site, input);
	}

	protected void createAddressArea() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Address");
		//section.setFont(FormUtils.getSectionFont());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite client = toolkit.createComposite(section);
		section.setClient(client);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);
		
		for (String key : AddressFieldsConstants.ORDERED_FIELDS) {
			FieldInfo fi = AddressFieldsConstants.FIELDS.get(key);
			
			if (fi.widgetClass == Text.class) {
		        Label label = toolkit.createLabel(client, fi.label + ":", SWT.LEFT);
		        label.setLayoutData(new GridData());
		        Text text  = toolkit.createText(client, "", SWT.SINGLE);
		        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				controls.put(key, text);
				text.addKeyListener(keyListener);
				
				if (fi.validatorClass != null) {
					fieldDecorators.put(key, FormUtils.createDecorator(label, fi.errMsg));
				}
			}
			else if (fi.widgetClass == Combo.class) {
				toolkit.createLabel(client, fi.label + " :", SWT.LEFT);
				Combo combo = new Combo(client, SWT.READ_ONLY);
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
			    Assert.isTrue(false, "invalid widget class " + fi.widgetClass.getName());
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
			        } 
			        catch (NoSuchMethodException e) {
			            throw new RuntimeException(e);
			        }
			        catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
			        } 
			        catch (IllegalArgumentException e) {
                        throw new RuntimeException(e);
			        } 
			        catch (InstantiationException e) {
                        throw new RuntimeException(e);
			        } 
			        catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
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
