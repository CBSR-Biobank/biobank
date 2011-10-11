package edu.ualberta.med.biobank.mvp.form;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.mvp.presenter.Presenter;
import edu.ualberta.med.biobank.mvp.view.View;

// TODO: could have a single editorpart that fetches the view then adds it to the presenter. The presenter comes in the init method?
public class BiobankEditorPart implements IEditorPart {
    private Presenter<View> presenter;
    private View view;

    @Override
    public void addPropertyListener(IPropertyListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void createPartControl(Composite parent) {
        //
        // Injector injector = null;
        //
        // injector.inject(presenter);

        // opened after this returns
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public IWorkbenchPartSite getSite() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Image getTitleImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTitleToolTip() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removePropertyListener(IPropertyListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

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
    public boolean isSaveOnCloseNeeded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public IEditorInput getEditorInput() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IEditorSite getEditorSite() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        this.presenter = null; // get presenter;

    }
}
