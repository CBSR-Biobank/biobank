package edu.ualberta.med.biobank.forms;

import java.util.HashMap;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class BiobankViewForm extends BiobankFormBase {
    
    protected WritableApplicationService appService;
    
    protected String sessionName;
	
	private HashMap<String, Control> controls;
    
    protected DataBindingContext dbc;
    
    public BiobankViewForm() {
        super();
        controls = new HashMap<String, Control>();
        dbc = new DataBindingContext();
    }

	
    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void setFocus() {
    }

    public void setAppService(WritableApplicationService appService) {
        this.appService = appService;
    }
    
    protected Control createBoundWidget(Composite composite, 
    		Class<?> widgetClass, int widgetOptions, String fieldLabel, 
    		IObservableValue modelObservableValue) {
    	if ((widgetClass == Combo.class) || (widgetClass == Text.class) 
    			|| (widgetClass == Label.class)) {
    		Label label = toolkit.createLabel(
    				composite, fieldLabel + ":", SWT.LEFT);
    		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    		if (widgetOptions == SWT.NONE) {
    			widgetOptions = SWT.SINGLE;
    		}    		
    		Label field = toolkit.createLabel(composite, "", 
    				widgetOptions | SWT.LEFT | SWT.BORDER);
    		field.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

    		dbc.bindValue(SWTObservables.observeText(field),
    				modelObservableValue, null, null);
    		return field;
    	} 
    	else {
    		Assert.isTrue(false, "invalid widget class " + widgetClass.getName());
    	}
    	return null;
    }
    
    protected void createWidgetsFromMap(ListOrderedMap fieldsMap, 
            Object pojo, Composite client) {
        FieldInfo fi;
        
        MapIterator it = fieldsMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            fi = (FieldInfo) it.getValue();
            
            Control control = createBoundWidget(client, fi.widgetClass, SWT.NONE,
                fi.label, PojoObservables.observeValue(pojo, key));
            controls.put(key, control);
        }     
    }

	protected void addRefreshToolbarAction() {        
		Action reloadAction = new Action("Reload") {
			@Override
			public void run() {
				reload();
			}			
		};
		reloadAction.setImageDescriptor(BioBankPlugin.getImageDescriptor("icons/arrow_refresh.png"));
		form.getToolBarManager().add(reloadAction);
		form.updateToolBar();		
		
	}

	protected abstract void reload();

}
