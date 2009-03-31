package edu.ualberta.med.biobank.forms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

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
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;

public abstract class BiobankEditForm extends EditorPart {
    
    protected WritableApplicationService appService;
    
    protected String sessionName;

    private boolean dirty = false;

    private ManagedForm mform;

    protected FormToolkit toolkit;
    
    protected ScrolledForm form;
        
    private HashMap<String, ControlDecoration> fieldDecorators;
    
    private HashMap<String, Control> controls;
    
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
    
    public BiobankEditForm() {
        super();
        fieldDecorators = new HashMap<String, ControlDecoration>();
        controls = new HashMap<String, Control>();
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
        setSite(editorSite);
        setInput(input);
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
        mform = new ManagedForm(parent);
        toolkit = mform.getToolkit();
        form = mform.getForm();
        
        // start a new runnable so that database objects are populated in a
        // separate thread.
        BusyIndicator.showWhile(parent.getDisplay(), new Runnable() {
            public void run() {
                createFormContent();
                form.reflow(true);
            }
        });
    }
    
    
    abstract protected void createFormContent();
    
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
    
    protected void createWidgetsFromHashMap(HashMap<String, FieldInfo> fields, 
            String [] fieldOrder, Object pojo, Composite client) {
        
        for (String key : fieldOrder) {
            FieldInfo fi = fields.get(key);
            
            if (fi.widgetClass == Text.class) {
                Label label = toolkit.createLabel(client, fi.label + ":", SWT.LEFT);
                label.setLayoutData(new GridData());
                Text text  = toolkit.createText(client, "", SWT.SINGLE);
                text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                
                controls.put(key, text);
                text.addKeyListener(keyListener);
                
                if (fi.validatorClass != null) {
                    fieldDecorators.put(key, 
                            FormUtils.createDecorator(label, fi.errMsg));
                }
            }    
            else if (fi.widgetClass == Combo.class) {
                toolkit.createLabel(client, fi.label + " :", SWT.LEFT);
                Combo combo = new Combo(client, SWT.READ_ONLY);
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
    
    

    protected void bindValuesFromHashMap(DataBindingContext dbc,
            HashMap<String, FieldInfo> fields, Object pojo) {    
        for (String key : fields.keySet()) {
            FieldInfo fi = fields.get(key);
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
                        PojoObservables.observeValue(pojo, key), uvs, null);
            }
            else if (fi.widgetClass == Combo.class) {
                dbc.bindValue(SWTObservables.observeSelection(controls.get(key)),
                        PojoObservables.observeValue(pojo, "activityStatus"), null, null);
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
    
    protected abstract void handleStatusChanged(IStatus status);

}
