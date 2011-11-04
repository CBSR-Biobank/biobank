package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.mvp.presenter.impl.FormManagerPresenter;
import edu.ualberta.med.biobank.mvp.view.form.FormViewEditorInput;
import edu.ualberta.med.biobank.mvp.view.form.FormViewEditorPart;

public class FormManagerView implements FormManagerPresenter.View {
    @Override
    public void openForm(Object object, IFormView view) {
        IEditorInput input = new FormViewEditorInput(view);
        String id = FormViewEditorPart.ID;
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(input, id, true);
        } catch (PartInitException e) {
            // TODO: how do we handle failing to open?
            System.out.println(e);
        }
    }

    @Override
    public void create(Composite parent) {
    }
}
