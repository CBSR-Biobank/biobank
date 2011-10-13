package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import edu.ualberta.med.biobank.mvp.event.EclipseClickEvent;
import edu.ualberta.med.biobank.mvp.presenter.impl.EntryManagerPresenter;

public class EntryManagerView implements EntryManagerPresenter.View {

    @Override
    public void showView(Object object, EntryView view) {
        // TODO: add component to determine uniqueness properly.
        IEditorInput input = new EntryViewEditorInput(view);
        String id = EntryViewEditorPart.ID;

        try {
            IEditorPart part = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage()
                .openEditor(input, id, true);
        } catch (PartInitException e) {
            // TODO: something?
        }
    }

    private static class EntryViewEditorInput implements IEditorInput {
        private final EntryView entryView;

        public EntryViewEditorInput(EntryView entryView) {
            this.entryView = entryView;
        }

        public EntryView getEntryView() {
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

    public static class EntryViewEditorPart extends EditorPart {
        public static String ID = "edu.ualberta.med.biobank.mvp.view.EntryManagerView.EntryViewEditorPart";
        private EntryView entryView;

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
    }

    @Override
    public void create(Composite parent) {
    }
}
