package edu.ualberta.med.biobank.forms;

import java.util.HashMap;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class BiobankViewForm extends BiobankFormBase {
    
    protected WritableApplicationService appService;
    
    protected String sessionName;
	
	private HashMap<String, Control> controls;
    
    public BiobankViewForm() {
        super();
        controls = new HashMap<String, Control>();
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
    
    protected Control createWidget(Composite parent, Class<?> widgetClass, 
    		int widgetOptions, String fieldLabel) {
    	if ((widgetClass == Combo.class) || (widgetClass == Text.class) 
    			|| (widgetClass == Label.class)) {
    		Label label = toolkit.createLabel(parent, fieldLabel + ":", SWT.LEFT);
    		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    		if (widgetOptions == SWT.NONE) {
    			widgetOptions = SWT.SINGLE;
    		}    		
    		Label field = toolkit.createLabel(parent, "", widgetOptions | SWT.LEFT | SWT.BORDER);
    		field.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

    		return field;
    	} 
    	else {
    		Assert.isTrue(false, "invalid widget class " + widgetClass.getName());
    	}
    	return null;
    }
    
    protected void createWidgetsFromMap(ListOrderedMap fieldsMap, Composite parent) {
        FieldInfo fi;
        
        MapIterator it = fieldsMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            fi = (FieldInfo) it.getValue();
            
            Control control = createWidget(parent, fi.widgetClass, SWT.NONE,
                fi.label);
            controls.put(key, control);
        }     
    }
    

	protected void setWidgetsValues(ListOrderedMap fieldsMap, Object bean) {
		MapIterator it = fieldsMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            FieldInfo fi = (FieldInfo) it.getValue();
            IObservableValue ov = PojoObservables.observeValue(bean, key);
            Object value = ov.getValue();
            if (value != null) {
            	Control control = controls.get(key);            
            	if ((fi.widgetClass == Combo.class) || (fi.widgetClass == Text.class) 
            			|| (fi.widgetClass == Label.class)) {
            		((Label) control).setText((String)value);
            	}
            }
        }     
	}
	
	protected void addRefreshToolbarAction() {        
		Action reloadAction = new Action("Reload") {
			@Override
			public void run() {
		        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
		            public void run() {
		                reload();
		            }
		        });
			}			
		};
		ImageDescriptor imgDesc = BioBankPlugin.getImageDescriptor("icons/arrow_refresh.png");
		reloadAction.setImageDescriptor(imgDesc);
		form.getToolBarManager().add(reloadAction);
		form.updateToolBar();		
		
		
		Menu menu = new Menu(form);
		form.setMenu(menu);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Reload");
		item.setImage(imgDesc.createImage());
	}
	
	protected abstract void reload();

}
