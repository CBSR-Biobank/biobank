package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;

public abstract class BiobankEditForm extends EditorPart {

    private boolean dirty = false;

    private ManagedForm mform;

    protected FormToolkit toolkit;
    
    protected ScrolledForm form;
    
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

}
