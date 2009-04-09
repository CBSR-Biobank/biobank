package edu.ualberta.med.biobank.forms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
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
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.SessionManager;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Base class for data entry forms.
 * 
 * Notes:
 *  - createFormContent() and saveForm() are called in their own thread so 
 *    making calls to the database is possible.
 *
 */
public abstract class BiobankEntryForm extends BiobankFormBase {
    
    protected WritableApplicationService appService;
    
    protected String sessionName;

    private boolean dirty = false;

    protected IStatus currentStatus;
    
    protected DataBindingContext dbc;
    
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
    
    public BiobankEntryForm() {
        super();
        controls = new HashMap<String, Control>();
        dbc = new DataBindingContext();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        setDirty(false);
        doSaveInternal();
    }
    
    protected void doSaveInternal() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                saveForm();
            }
        });
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {
        super.init(editorSite, input);
        setDirty(false);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    protected void setDirty(boolean d) {
        dirty = d;
        firePropertyChange(ISaveablePart.PROP_DIRTY);
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }
    

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        bindChangeListener();
    }
    
    abstract protected void saveForm();   
    

    @Override
    public void setFocus() {
        form.setFocus();
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setAppService(WritableApplicationService appService) {
        Assert.isNotNull(appService, "appService is null");
        this.appService = appService;
    }
    
    protected Control createBoundWidget(Composite composite, 
        Class<?> widgetClass, int widgetOptions, String fieldLabel, String [] widgetValues, 
        IObservableValue modelObservableValue, Class<?> validatorClass, 
        String validatorErrMsg) {
        if (widgetClass == Text.class) {
            Label label = toolkit.createLabel(
                composite, fieldLabel + ":", SWT.LEFT);
            label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
            if (widgetOptions == SWT.NONE) {
                widgetOptions = SWT.SINGLE;
            }
            Text text  = toolkit.createText(composite, "", widgetOptions);
            text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            text.addKeyListener(keyListener);
            
            UpdateValueStrategy uvs = null;
            if (validatorClass != null) {
                IValidator validator = createValidator(validatorClass, 
                    FormUtils.createDecorator(label, validatorErrMsg), 
                    validatorErrMsg);
                uvs = new UpdateValueStrategy();
                uvs.setAfterGetValidator(validator);
            }

            dbc.bindValue(SWTObservables.observeText(text, SWT.Modify),
                modelObservableValue, uvs, null);
            return text;
        }    
        else if (widgetClass == Combo.class) {
            toolkit.createLabel(composite, fieldLabel + " :", SWT.LEFT);
            Combo combo = new Combo(composite, SWT.READ_ONLY);
            combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            Assert.isNotNull(widgetValues, "combo values not assigned");
            combo.setItems(widgetValues);
            toolkit.adapt(combo, true, true);
            
            dbc.bindValue(SWTObservables.observeSelection(combo),
                modelObservableValue, null, null);
            
            combo.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    setDirty(true);
                }
            });
            return combo;
        }            
        else {
            Assert.isTrue(false, "invalid widget class " + widgetClass.getName());
        }
        return null;
    }
    
    protected IValidator createValidator(Class<?> validatorClass, 
        ControlDecoration dec, String validatorErrMsg) {
        try {
            Class<?>[] types = new Class[] { String.class, ControlDecoration.class };               
            Constructor<?> cons = validatorClass.getConstructor(types);
            Object[] args = new Object[] { validatorErrMsg, dec};
            return (IValidator) cons.newInstance(args);
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
    
    protected void createWidgetsFromMap(ListOrderedMap fieldsMap, 
            Object pojo, Composite client) {
        FieldInfo fi;
        
        MapIterator it = fieldsMap.mapIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            fi = (FieldInfo) it.getValue();
            
            Control control = createBoundWidget(client, fi.widgetClass, 
                fi.widgetOptions, fi.label, fi.widgetValues, 
                PojoObservables.observeValue(pojo, key),
                fi.validatorClass, fi.errMsg);
            controls.put(key, control);
        }     
    }
    
    protected Combo createSessionSelectionWidget(Composite client) {
        String[] sessionNames = SessionManager.getInstance().getSessionNames();
        
        if (sessionNames.length > 1) {  
            toolkit.createLabel(client, "Session:", SWT.LEFT);
            Combo session = new Combo(client, SWT.READ_ONLY);
            session.setItems(sessionNames);
            session.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            return session;
        }   
        return null;
    }
    
    protected void bindChangeListener() {
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
    
    protected abstract void handleStatusChanged(IStatus status);

}
