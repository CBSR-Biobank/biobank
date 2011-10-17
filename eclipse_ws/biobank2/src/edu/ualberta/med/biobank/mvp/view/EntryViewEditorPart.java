package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import edu.ualberta.med.biobank.mvp.event.EclipseClickEvent;

public class EntryViewEditorPart extends EditorPart {
    public static String ID = "edu.ualberta.med.biobank.mvp.view.EntryViewEditorPart"; //$NON-NLS-1$
    private FormView entryView;

    @Override
    public void doSave(IProgressMonitor monitor) {
        // TODO: pay attention to the monitor?
        entryView.getSave().fireEvent(new EclipseClickEvent());
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
        entryView = ((EntryViewEditorInput) input).getEntryView();
    }

    @Override
    public boolean isDirty() {
        // TODO: dirty handling
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        // set view's name to something that wraps an internal text field
        // added to this parent composite

        // TODO: add commands
        // TODO: add command exceution listener so the save command triggers
        // entryView.getSave() ?

        entryView.create(parent);
    }

    @Override
    public void setFocus() {
        // TODO: set focus on some child element
    }

    public static class EntryViewEditorInput implements IEditorInput {
        private final FormView entryView;

        public EntryViewEditorInput(FormView entryView) {
            this.entryView = entryView;
        }

        public FormView getEntryView() {
            return entryView;
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
