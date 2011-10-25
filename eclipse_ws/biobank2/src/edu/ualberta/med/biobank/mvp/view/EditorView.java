package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.google.gwt.user.client.ui.HasValue;

public class EditorView {
    HasValue<String> name;

    private IView addressEntryView;
    private IView activityStatusComboView;

    public HasValue<String> getName() {
        return name;
    }

    // TODO: make WindowView interface with "open()"
    public void open(Object presenter) {
        IEditorInput input = null; // must contain reference to this object (the
                                   // view) and presenter or other info to
                                   // determine uniqueness
        String id = null; // must contain id of 'Widget'
        try {
            IEditorPart part = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage()
                .openEditor(input, id, true);
        } catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void close() {
        IEditorInput input = null; // must contain reference to this
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        IEditorPart part = page.findEditor(input);
        if (part != null) {
            page.closeEditor(part, true);
        }
    }

    public static class InnerEditorPart extends EditorPart {
        @Override
        public void doSave(IProgressMonitor monitor) {
            // TODO Auto-generated method stub

        }

        @Override
        public void doSaveAs() {
            // TODO Auto-generated method stub

        }

        @Override
        public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
            // get view, and remember
        }

        @Override
        public boolean isDirty() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isSaveAsAllowed() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void createPartControl(Composite parent) {
            // set view's name to something that wraps an internal text field
            // added to this parent composite

            // view.addressEntryView.create(parent);
        }

        @Override
        public void setFocus() {
            // TODO Auto-generated method stub

        }

    }
}
