package edu.ualberta.med.biobank.forms;

import java.util.HashMap;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

public abstract class BiobankViewForm extends EditorPart {
    
    protected WritableApplicationService appService;
    
    protected String sessionName;

    private ManagedForm mform;
    
    protected FormToolkit toolkit;

    protected ScrolledForm form;
	
	private HashMap<String, Control> controls;
    
    protected DataBindingContext dbc;
    
    public BiobankViewForm() {
        super();
        controls = new HashMap<String, Control>();
        dbc = new DataBindingContext();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {
        
        setSite(editorSite);
        setInput(input);
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

    @Override
    public void setFocus() {
    }
    
    protected Section createSection(String title) {
        Section section = toolkit.createSection(form.getBody(),
                Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED); 
        section.setText(title);
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        section.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(false);
            }
        });
        return section;
    }

    protected Composite createSectionWithClient(String title) {
    	Section section = createSection(title);        
        Composite client;
        client = toolkit.createComposite(section);
        section.setClient(client);
        
        client.setLayout(new GridLayout(2, false));
        toolkit.paintBordersFor(client); 
        return client;
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
    		field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    		dbc.bindValue(SWTObservables.observeText(field),
    				modelObservableValue, null, null);
    		return field;
    	} 
    	else {
    		Assert.isTrue(false, "invalid widget class " + widgetClass.getName());
    	}
    	return null;
    }
    
    protected void createWidgetsFromHashMap(HashMap<String, FieldInfo> fields, 
            String [] fieldOrder, Object pojo, Composite client) {
        FieldInfo fi;
        
        for (String key : fieldOrder) {
            fi = fields.get(key);
            
            Control control = createBoundWidget(client, fi.widgetClass, SWT.NONE,
                fi.label, PojoObservables.observeValue(pojo, key));
            controls.put(key, control);
        }     
    }

}
