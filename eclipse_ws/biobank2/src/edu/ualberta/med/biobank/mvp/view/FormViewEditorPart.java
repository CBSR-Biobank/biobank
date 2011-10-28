package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class FormViewEditorPart extends EditorPart {
    public static String ID = "edu.ualberta.med.biobank.mvp.view.FormViewEditorPart"; //$NON-NLS-1$
    private IFormView formView;

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        setSite(site);
        setInput(input);

        formView = getEditorInput().getFormView();
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        // set view's name to something that wraps an internal text field
        // added to this parent composite

        // TODO: add commands
        // TODO: add command exceution listener so the save command triggers
        // entryView.getSave() ?

        formView.create(parent);
    }

    @Override
    public void setFocus() {
        // TODO: set focus on some child element
    }

    @Override
    public FormViewEditorInput getEditorInput() {
        return (FormViewEditorInput) super.getEditorInput();
    }

    public static class FormViewEditorInput implements IEditorInput {
        private final IFormView formView;

        public FormViewEditorInput(IFormView formView) {
            this.formView = formView;
        }

        public IFormView getFormView() {
            return formView;
        }

        @Override
        public Object getAdapter(Class adapter) {
            return null;
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public ImageDescriptor getImageDescriptor() {
            return null;
        }

        @Override
        public String getName() {
            return "TODO: me!";
        }

        @Override
        public IPersistableElement getPersistable() {
            return null;
        }

        @Override
        public String getToolTipText() {
            return "TODO: me!";
        }
    }
}
