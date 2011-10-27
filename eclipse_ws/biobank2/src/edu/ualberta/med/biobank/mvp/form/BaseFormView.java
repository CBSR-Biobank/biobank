package edu.ualberta.med.biobank.mvp.form;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.mvp.view.IView;

public class BaseFormView implements IView {

    @Override
    public void create(Composite parent) {
        try {
            IEditorInput input = null;
            String id = null;
            boolean giveFocus = true;

            IEditorPart part = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage()
                .openEditor(input, id, giveFocus);
        } catch (PartInitException e) {
            // logger.error("Can't open form with id " + id, e); //$NON-NLS-1$
        }
    }
}
