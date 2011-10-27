package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter;
import edu.ualberta.med.biobank.mvp.view.EntryViewEditorPart.EntryViewEditorInput;

public class FormManagerView implements FormManagerPresenter.View {

    @Override
    public void openForm(Object object, IFormView view) {
        // TODO: add component to determine uniqueness properly.
        IEditorInput input = new EntryViewEditorInput(view);
        String id = EntryViewEditorPart.ID;

        try {
            IEditorPart part = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage()
                .openEditor(input, id, true);
        } catch (PartInitException e) {
            System.out.println(e);
        }
    }

    @Override
    public void create(Composite parent) {
    }
}
