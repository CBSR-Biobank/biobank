package edu.ualberta.med.biobank.mvp.view.form;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import edu.ualberta.med.biobank.mvp.event.EmptyClickEvent;
import edu.ualberta.med.biobank.mvp.user.ui.HasButton;
import edu.ualberta.med.biobank.mvp.view.IFormView;
import edu.ualberta.med.biobank.mvp.view.ISaveableView;

/**
 * 
 * @author jferland
 * 
 */
public class FormViewEditorPart extends EditorPart {
    public static final String ID =
        "edu.ualberta.med.biobank.mvp.view.FormViewEditorPart"; //$NON-NLS-1$
    private IFormView formView;
    private boolean dirty = false;

    @Override
    public void doSave(IProgressMonitor monitor) {
        // always cancel the monitor so the Presenter can handle the save and
        // handle closing the form
        monitor.setCanceled(true);

        if (formView instanceof ISaveableView) {
            HasButton save = ((ISaveableView) formView).getSave();
            save.fireEvent(new EmptyClickEvent());
        }
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

        formView = ((FormViewEditorInput) getEditorInput()).getFormView();
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        firePropertyChange(ISaveablePart.PROP_DIRTY);
    }

    @Override
    public void createPartControl(Composite parent) {
        if (formView instanceof IHasEditor) {
            ((IHasEditor) formView).setEditor(this);
        }

        formView.create(parent);
    }

    @Override
    public void setFocus() {
        // not sure who to set focus on
    }

    @Override
    public void setPartName(String partName) {
        super.setPartName(partName);
    }

    @Override
    public void setTitleImage(Image titleImage) {
        super.setTitleImage(titleImage);
    }

    @Override
    public void setTitleToolTip(String titleToolTip) {
        super.setTitleToolTip(titleToolTip);
    }
}
